package by.gto.erip.service;

import by.gto.erip.dao.interfaces.CommonDAO;
import by.gto.erip.exceptions.GenericEripException;
import by.gto.erip.helpers.ApplicationContextProvider;
import by.gto.erip.helpers.LockerJ;
import by.gto.erip.helpers.Settings;
import by.gto.erip.model.EripOfflineMsg210;
import by.gto.erip.model.EripServicesEnum;
import by.gto.erip.model.hib.DeliveryQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by timo on 18.11.2016.
 */
@Component
public class Schedules implements ApplicationContextAware {
    private static final Logger log = Logger.getLogger(Schedules.class);
    private EripServiceImpl eripService = null;

    @Autowired
    private CommonDAO commonDAO;

    @Autowired
    @Qualifier("eripDS")
    private javax.sql.DataSource eripDS;

    @Autowired
    @Qualifier("errorNotificationsMailSession")
    private javax.mail.Session errorNotificationsMailSession;

    private static final String LOCK210_NAME = "a624b36dabc0";
    private static final String LOCK_SEND_NAME = "d49b156a432b";

    public EripServiceImpl getEripService() {
        if (eripService == null) {
            eripService = (EripServiceImpl) ApplicationContextProvider.getBean("eripService");
        }
        return eripService;
    }

    @Scheduled(fixedDelay = 86400000L, initialDelay = 0L)
    public void updateTariffsFromDB() {
        getEripService().updateTariffsFromDB();
    }

    @Scheduled(fixedDelay = 86400000L, initialDelay = 0L)
    public void removeOldRegisteredPayments() {
        getEripService().removeOldRegisteredPayments(Settings.getRegisteredPaymentKeepDays());
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 300000)
    public void scheduleSendDispatched() {
        try {
            sendDispatched();
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    public int sendDispatched() throws Exception {
        List<DeliveryQueue> entities = getEripService().getEntities(DeliveryQueue.class);
        if (entities.size() == 0) {
            return 0;
        }
        try (Connection c = eripDS.getConnection(); LockerJ l = new LockerJ(c, LOCK_SEND_NAME)) {
            if (!l.getLock()) {
                log.info("sendDispatched: не удалось получить блокировку");
                return -1;
            }

            switch (Settings.getEripRegistriesTransportMethod()) {
                case "webdav":
                    sendByWebdav(entities);
                    break;
                case "mail":
                    sendByMail(entities);
                    break;
                default:
                    return 0;
            }
            getEripService().deleteDeliveryQueue(entities);
        }
        return entities.size();
    }

    private void sendByMail(List<DeliveryQueue> entities) throws IOException {
        Map<String, byte[]> attaches = new HashMap<>(1);
        for (DeliveryQueue item : entities) {
            attaches.clear();

            byte[] content;
            try (FileInputStream fis = new FileInputStream(item.getFilename())) {
                content = org.apache.commons.io.IOUtils.toByteArray(fis);
                String reportFileName = item.getTargetFilename();
                attaches.put(reportFileName, content);
                mail(item.getMessage(), "", attaches);
            }
        }
    }

    private static CloseableHttpClient getCloseableHttpClient() throws
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, (chain, authType) -> true);

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(Settings.getEripWebdavUser(), Settings.getEripWebdavPassword()));
        return HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setSSLContext(builder.build())
                .build();
    }

    private void sendByWebdav(List<DeliveryQueue> entities) throws
            NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, GenericEripException {
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpClient = getCloseableHttpClient()) {
            for (DeliveryQueue msg : entities) {
                File file = new File(msg.getFilename()); // file.exists()
                FileEntity entity = new FileEntity(file);
                HttpPut put = new HttpPut(Settings.getEripWebdavUrl() + "/" + StringUtils.replace(msg.getTargetFilename(), " ", "%20"));

                entity.setContentType("application/octet-stream");
                put.addHeader("content-type", "application/octet-stream");
                put.addHeader("Accept", "*/*");
                put.addHeader("Accept-Encoding", "gzip,deflate,sdch");
                put.addHeader("Accept-Language", "en-US,en;q=0.8");
                put.setEntity(entity);
                response = httpClient.execute(put);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode < 200 || statusCode > 220) {
                    throw new GenericEripException("Error PUT " + put.getURI() + "(" + response.getStatusLine() + ")");
                }
                response.close();
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Scheduled(fixedDelay = 600000, initialDelay = 1)
    public void scheduledProcess210() {
        process210();
    }

    public int process210() {
        int counter = 0;
        FTPClient ftpClient = null;
        try (Connection connection = eripDS.getConnection(); LockerJ locker = new LockerJ(connection, LOCK210_NAME)) {
            if (!locker.getLock()) {
                log.info("process210: не удалось получить блокировку");
                return -1;
            }
            log.info("process210: started");
            ftpClient = new FTPClient();
            ftpClient.setControlKeepAliveTimeout(100);
            ftpClient.connect(Settings.getEripFtpHost(), 21);
            ftpClient.login(Settings.getEripFtpUser(), Settings.getEripFtpPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.cwd("out");
            FTPFile[] ftpFiles = ftpClient.listFiles();

            for (FTPFile fi : ftpFiles) {
                String ftpFileName = fi.getName();
                if (!(fi.isFile() && ftpFileName.endsWith(".210"))) {
                    continue;
                }
                log.info("processing " + ftpFileName);

                // String localTempPathToFile = String.format("%1$s/210/%2$tY-%2$tm/%2$td", SettingsKt.getREQUEST_LOG_PATH(), new Date());
                String tempDir = String.format("%1$s/210", Settings.getRequestLogPath());
                new File(tempDir).mkdirs();
                String tempPath = tempDir + "/" + ftpFileName;
                try (FileOutputStream fos = new FileOutputStream(tempPath)) {
                    ftpClient.retrieveFile(ftpFileName, fos);
                }
                EripOfflineMsg210 msg210;
                try (InputStream inputStream = new FileInputStream(tempPath);
                     InputStreamReader isr = new InputStreamReader(inputStream, "windows-1251");
                     BufferedReader br = new BufferedReader(isr)) {
                    msg210 = Msg210Parser.parse210(br);
                }
                Msg210Parser.markTransactionsPayed(msg210);
                String target210dir = String.format("%s/210/%s-%s/%s",
                        Settings.getRequestLogPath(),
                        msg210.getHeader()[10].substring(0, 4),
                        msg210.getHeader()[10].substring(4, 6),
                        msg210.getHeader()[10].substring(6, 8)
                );
                String target210XlsxDir = String.format("%s/210_xlsx/%s-%s/%s",
                        Settings.getRequestLogPath(),
                        msg210.getHeader()[10].substring(0, 4),
                        msg210.getHeader()[10].substring(4, 6),
                        msg210.getHeader()[10].substring(6, 8)
                );
                new File(target210dir).mkdirs();
                new File(target210XlsxDir).mkdirs();
                String target210Path = target210dir + "/" + ftpFileName;
                Files.move(new File(tempPath).toPath(), new File(target210Path).toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                // Преобразуем в Excel все 210-е сообщения, кроме тех, которые содержат госпошлину:
                var servicesToSkip = Arrays.asList(
                        String.valueOf(EripServicesEnum.STATE_TAX_PHYS),
                        String.valueOf(EripServicesEnum.STATE_TAX_JUR));
                if (msg210.getBody().stream().noneMatch(bl -> servicesToSkip.contains(bl.getFields()[1]))) {
                    Msg210Parser.fillAdditionalTransactionInfo(msg210);
                    byte[] content = Msg210Parser.convertToExcel(msg210);
                    String reportFileName = msg210.getHeader()[10].substring(0, 4) + "."
                            + msg210.getHeader()[10].substring(4, 6) + "."
                            + msg210.getHeader()[10].substring(6, 8) + "_"
                            + msg210.getHeader()[9] + ".xlsx";

                    String fullXlsPath = target210dir + "/" + reportFileName;
                    try (FileOutputStream fos = new FileOutputStream(fullXlsPath)) {
                        fos.write(content);
                    }
                    Files.copy(Path.of(fullXlsPath), Path.of(target210XlsxDir, reportFileName),
                            StandardCopyOption.REPLACE_EXISTING);

                    // dispatchToSend(fullXlsPath, reportFileName, "Реестр платежей " + msg210.getHeader()[2] + " 59be7c71-f054-4411-9e9e-11c60fdbaeb7");
                    // dispatchToSend(target210Path, null);
                }
                try {
                    ftpClient.deleteFile(ftpFileName);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                counter++;
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            log.info("process210: finished");
            try {
                if (ftpClient != null && ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
        return counter;
    }

    private void dispatchToSend(String localPath, String targetPath, String message) {
        getEripService().saveEntity(new DeliveryQueue(
                localPath,
                (targetPath == null) ? new File(localPath).getName() : targetPath,
                message));
    }

    private void mail(String subject, String body, Map<String, byte[]> attaches) {
        Session session = errorNotificationsMailSession;
        try {
            MimeMessage message = new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Settings.getEripMailTo()));
            message.setSubject(subject);
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(body);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            for (String key : attaches.keySet()) {
                messageBodyPart = new MimeBodyPart();
                messageBodyPart.setFileName(key);
                DataSource source = new ByteArrayDataSource(attaches.get(key), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                messageBodyPart.setDataHandler(new DataHandler(source));
                multipart.addBodyPart(messageBodyPart);
            }
            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            log.info("message sent: " + subject);
        } catch (MessagingException mex) {
            log.error(mex);
        }
    }

    public void reloadSettings() {
        System.out.println(this);
        List<by.gto.erip.model.hib.Settings> list = commonDAO.customQueryTransactional("from Settings s where s.application='*default*' or s.application='online_listener.war' order by s.application", Collections.emptyMap());
        String gaiServiceUser = "";
        String gaiServicePassword = "";
        String gaiServiceV1User = "";
        String gaiServiceV1Password = "";
        for (by.gto.erip.model.hib.Settings s : list) {
            Settings.setEripFtpHost(s.getEripFtpHost());
            Settings.setEripFtpUser(s.getEripFtpUsername());
            Settings.setEripFtpPassword(s.getEripFtpPassword());
            Settings.setEripRegistriesTransportMethod(s.getEripRegistriesTransportMethod());
            Settings.setEripWebdavUrl(s.getEripWebdavUrl());
            Settings.setEripWebdavUser(s.getEripWebdavUsername());
            Settings.setEripWebdavPassword(s.getEripWebdavPassword());
            Settings.setEripMailTo(s.getEripMailTo());
            Settings.setGaiServiceEnabled(s.getGaiserviceEnabled());
            Settings.setGaiServiceUrl(s.getGaiServiceUrl());
            Settings.setGaiServiceV1Url(s.getGaiServiceV1Url());
            Settings.setGaiRetries(s.getGaiserviceRetries());
            Settings.setRequestLogPath(s.getRequestLogPath());
            if (s.getGaiServiceUser() != null)
                gaiServiceUser = s.getGaiServiceUser();
            if (s.getGaiServicePassword() != null)
                gaiServicePassword = s.getGaiServicePassword();
            if (s.getGaiServiceV1User() != null)
                gaiServiceV1User = s.getGaiServiceV1User();
            if (s.getGaiServiceV1Password() != null)
                gaiServiceV1Password = s.getGaiServiceV1Password();
        }
        Settings.setGaiServiceEncodedAuth(
                "Basic " + Base64.getEncoder().encodeToString((gaiServiceUser + ":" + gaiServicePassword).getBytes())
        );
        Settings.setGaiServiceV1EncodedAuth(
                "Basic " + Base64.getEncoder().encodeToString((gaiServiceV1User + ":" + gaiServiceV1Password).getBytes())
        );

        // пытаемся создать файлы в папках, куда у нас должен быть доступ:
        List<String> folders = Arrays.asList("/nonexistent/folder/deep", Settings.getRequestLogPath(), "/var/log/erip");
        for (String folder : folders) {
            try (FileWriter wr = new FileWriter(folder + "/testFile", StandardCharsets.UTF_8)) {
                wr.write(String.valueOf(Math.random()));
            } catch (Throwable ex) {
                log.error("не смог записать в файл " + folder + "/testFile", ex);
                mail("online_listener access denied", "не смог записать в файл " + folder + "/testFile", Collections.emptyMap());
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        reloadSettings();
    }
}

