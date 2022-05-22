// /*
//  * Copyright (c) 2016. УП "Белтехосмотр"
//  */
//
// package by.gto.erip.webservices;
//
// import by.gto.erip.helpers.ApplicationContextProvider;
// import by.gto.erip.helpers.Settings;
// import by.gto.erip.helpers.StringHelpers;
// import by.gto.erip.service.EripServiceImpl;
// import by.gto.erip.service.Schedules;
// import java.io.FileInputStream;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.net.InetAddress;
// import java.net.NetworkInterface;
// import java.nio.charset.StandardCharsets;
// import java.util.Date;
// import java.util.Enumeration;
// import java.util.HashMap;
// import javax.mail.Message;
// import javax.mail.MessagingException;
// import javax.mail.Session;
// import javax.mail.Transport;
// import javax.mail.internet.InternetAddress;
// import javax.mail.internet.MimeMessage;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;
// import javax.ws.rs.GET;
// import javax.ws.rs.Path;
// import javax.ws.rs.Produces;
// import javax.ws.rs.core.Context;
// import javax.ws.rs.core.MediaType;
// import javax.ws.rs.core.SecurityContext;
// import org.apache.commons.io.IOUtils;
// import org.apache.log4j.Logger;
//
// /**
//  * @author ltv
//  */
// @Path("f7fcd472-dcb6-4c55-937e-f4e4352656a9")
// /*@Produces({ "application/xml", "application/json" })
// @Consumes({ "application/xml", "application/json" })*/
// public class Srv1 {
//     private EripServiceImpl eripService = null;
//     private final static Logger log = Logger.getLogger(Srv1.class);
//
//     private EripServiceImpl getEripService() {
//         if (null == eripService) {
//             eripService = (EripServiceImpl) ApplicationContextProvider.getBean("eripService");
//         }
//         return eripService;
//     }
//
//     @GET
//     @Path("random")
//     @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
//     //@Consumes("application/x-www-form-urlencoded")
//     public String getRandomCertNumber() {
//         return getEripService().acquireRandomCertNumber();
//     }
//
//     @GET
//     @Path("sendMail")
//     @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
//     //@Consumes("application/x-www-form-urlencoded")
//     public String sendMail() {
//         Session session = (Session) ApplicationContextProvider.getBean("errorNotificationsMailSession");
//         try {
//             MimeMessage message = new MimeMessage(session);
//             message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("efreet@tut.by"));
//             message.setSubject("topic");
//             message.setText("textMessage");
//             Transport.send(message);
//
//         } catch (MessagingException ex) {
//             log.error("Cannot send mail", ex);
//         }
//         return new Date().toString() + ": message sent";
//     }
//
//     /**
//      * Начать забирать файлы *.210 c FTP
//      *
//      * @return
//      */
//     @GET
//     @Path("process210")
//     @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
//     //@Consumes("application/x-www-form-urlencoded")
//     public String process210() {
//         try {
//             Schedules schedules = (Schedules) ApplicationContextProvider.getBean("schedules");
//             int count = schedules.process210();
//             if (count < 0) {
//                 return new Date().toString() + ": Не удалось получить блокировку";
//             } else {
//                 return new Date().toString() + ": Успешно получили " + count + " файлов *.210 с FTP";
//             }
//         } catch (Exception ex) {
//             log.warn("process210 error", ex);
//             return new Date().toString() + "\n" + StringHelpers.describeExceptionWithStacktrace(ex);
//         }
//     }
//
//     /**
//      * Начать отправку файлов xlsx (полученных из *.210) на офис по webdav ли email
//      *
//      * @return
//      */
//     @GET
//     @Path("sendDispatched")
//     @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
//     public String sendDispatched() {
//         try {
//             Schedules schedules = (Schedules) ApplicationContextProvider.getBean("schedules");
//             int counter = schedules.sendDispatched();
//             if (counter < 0) {
//                 return new Date().toString() + ": Не удалось получить блокировку";
//             } else {
//                 return new Date().toString() + ": Успешно отослано " + counter + " реестров";
//             }
//         } catch (Exception ex) {
//             log.warn("sendDispatched error", ex);
//             return new Date().toString() + "\n" + StringHelpers.describeExceptionWithStacktrace(ex);
//         }
//     }
//
//     @GET
//     @Path("getMacAddresses")
//     @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//     public Object getMacAddresses(@Context HttpServletRequest request,
//                                   @Context SecurityContext secContext) {
//         HashMap<String, Object> h = new HashMap<>();
//         Enumeration<String> headerNames = request.getHeaderNames();
//         HttpSession session = request.getSession();
//
//         StringBuilder sb = new StringBuilder();
//         while (headerNames.hasMoreElements()) {
//             String s = headerNames.nextElement();
//             String header = request.getHeader(s);
//             sb.append(s).append(" -> ").append(header).append("\n");
//         }
//         try {
//             h.put("string", "s1");
//             h.put("int", 2);
//             h.put("randomdouble", Math.random());
//             h.put("d", new Date());
//             h.put("headers", sb.toString());
//
//             sb = new StringBuilder();
//             Enumeration<String> attributeNames = session.getAttributeNames();
//             while (attributeNames.hasMoreElements()) {
//                 String s = attributeNames.nextElement();
//                 Object attr = session.getAttribute(s);
//                 sb.append(s).append(" -> ").append(attr).append("\n");
//             }
//
//             h.put("session attributes", sb.toString());
//
//             InetAddress[] allAddresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
//             HashMap ipInfo = new HashMap();
//             for (InetAddress a : allAddresses) {
//                 sb.setLength(0);
//                 NetworkInterface network = NetworkInterface.getByInetAddress(a);
//                 byte[] mac = network.getHardwareAddress();
//                 for (int i = 0; i < mac.length; i++) {
//                     sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
//                 }
//                 ipInfo.put(a.getHostAddress(), sb.toString());
//             }
//             h.put("ip info", ipInfo);
//
//             h.put("getContextPath", request.getContextPath());
//             h.put("getAuthType", request.getAuthType());
//             h.put("getPathInfo", request.getPathInfo());
//             h.put("getServletPath", request.getServletPath());
//             h.put("getPathTranslated", request.getPathTranslated());
//             h.put("getQueryString", request.getQueryString());
//             h.put("getRequestURI", request.getRequestURI());
//             h.put("getRequestURL", request.getRequestURL());
//             h.put("getRemoteUser", request.getRemoteUser());
//             h.put("getServerName", request.getServerName());
//             h.put("getServerPort", request.getServerPort());
//
//
//         } catch (Exception ex) {
//             log.error("test", ex);
//             ex.printStackTrace();
//         }
//         return h;
//     }
//
//     @GET
//     @Path("changelog")
//     @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
//     public String getChangelog() {
//         try (InputStream fis = getClass().getClassLoader().getResourceAsStream("changelog.txt");
//              InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
//             return IOUtils.toString(isr);
//         } catch (Exception ex) {
//             log.error(ex);
//             return "ошибка";
//         }
//     }
//
//     @GET
//     @Path("reloadSettings")
//     @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
//     public String reloadSettings() {
//         try {
//             Schedules schedules = (Schedules) ApplicationContextProvider.getBean("schedules");
//             schedules.reloadSettings();
//             return "Settings reloaded";
//         } catch (Exception ex) {
//             return StringHelpers.describeExceptionWithStacktrace(ex);
//         }
//     }
//
//
//     @GET
//     @Path("randomFile")
//     @Produces(MediaType.TEXT_PLAIN + ";charset=UTF-8")
//     public String randomFile() {
//         try (InputStream fis = new FileInputStream(Settings.getRequestLogPath() + "/123");
//              InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
//             return IOUtils.toString(isr);
//         } catch (Exception ex) {
//             log.error(ex);
//             return "ошибка";
//         }
//     }
// }
