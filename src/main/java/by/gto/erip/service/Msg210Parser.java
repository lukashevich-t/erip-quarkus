package by.gto.erip.service;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import by.gto.ais.model.PaymentTypesEnum;
import by.gto.erip.exceptions.MessageFormatException;
import by.gto.erip.helpers.ApplicationContextProvider;
import by.gto.erip.helpers.ResHolder;
import by.gto.erip.model.EripOfflineMsg210;
import by.gto.erip.model.hib.Service;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

public class Msg210Parser {
    private static final Logger logger = Logger.getLogger(Msg210Parser.class.getName());

    /**
     * Заполняет дополнительные поля в 210-м сообщении (номер ДС для услуги "Техосмотр", УНП для ЮЛ). В файле их нет, зато есть в БД
     *
     * @param msg210 сообщение 210 с незаполненными доп. полями
     */
    static void fillAdditionalTransactionInfo(EripOfflineMsg210 msg210) {
        String sqlTemplate =
                "SELECT "
                        + "  et.id,"
                        + "  oi.legal_type, oi.unp,"
                        + "  di.number,"
                        + "  oi.name"
                        + "  FROM erip.erip_transaction et"
                        + "  LEFT JOIN erip.order_info oi ON (et.id = oi.transaction_id)"
                        + "  LEFT JOIN ti.payment p ON (et.id = p.pay_id AND p.type=" + PaymentTypesEnum.ERIP + ")"
                        + "  LEFT JOIN ti.ti t ON (p.payment_set_id = t.payment_set_id)"
                        + "  LEFT JOIN ti.ds_info di ON (di.ds_id = t.ds_id AND t.ti_date BETWEEN di.date1 AND di.date2)"
                        + "  WHERE et.id IN ";
        try (ResHolder rh = new ResHolder()) {
            DataSource ds = (DataSource) ApplicationContextProvider.getBean("eripDS");
            Connection c = rh.addToAutoClose(ds.getConnection());
            Statement stmt = rh.addToAutoClose(c.createStatement());
            String stringTxIDs = msg210.getBody().stream().map(l -> l.getFields()[12]).collect(joining(","));
            Map<Long, EripOfflineMsg210.BodyLine> fieldsIndexedById =
                    msg210.getBody().stream().collect(toMap(bl -> Long.parseLong(bl.getFields()[12]), bl -> bl));
            ResultSet rs = rh.addToAutoClose(stmt.executeQuery(sqlTemplate + " (" + stringTxIDs + ");"));
            while (rs.next()) {
                long txId = rs.getLong(1);
                if (rs.wasNull()) {
                    continue;
                }
                EripOfflineMsg210.BodyLine bl = fieldsIndexedById.get(txId);
                int unp = rs.getInt(3);
                bl.setUnp(rs.wasNull() ? null : unp);
                bl.setDsNumber(rs.getString(4));
                bl.setName(StringUtils.firstNonBlank(rs.getString(5), bl.getFields()[3]));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static void markTransactionsPayed(EripOfflineMsg210 msg210) {
        EripServiceImpl eripService = (EripServiceImpl) ApplicationContextProvider.getBean("eripService");
        eripService.repayTransactions(msg210);
    }

    public static EripOfflineMsg210 parse210(BufferedReader br) throws IOException, MessageFormatException {
        EripOfflineMsg210 result = null;
        logger.info("Msg210Parser.parse210");
        // Date now = new Date();
        int lineCounter = 1;
        ArrayList<String[]> body = new ArrayList<>();

        while (true) {
            String line = StringUtils.trim(br.readLine());
            if (line == null) {
                break;
            }
            if (lineCounter == 1) {
                String[] headerFields = line.split("\\^", 500);

                if (headerFields.length != 18) {
                    throw new MessageFormatException("В заголовке собщения 210 должно быть 18 полей!");
                }

                result = new EripOfflineMsg210(headerFields, body);
                // logger.info("210 header " + _210Header2String(headerFields));
            } else {
                String[] fileFields = line.split("\\^", 500);
                if (fileFields.length == 0) { // пустая строка допускается
                    break;
                }
                if (fileFields.length != 20) {
                    throw new MessageFormatException("В записях сообщения 210 должно быть 20 полей");
                }
                body.add(fileFields);
            }
            lineCounter++;
        }
        if (result != null) {
            result.replaceBodyWith(body);
        }
        return result;
    }

    /**
     * Создает файл в формате Microsoft Excel на основании данных, содержащихся в 210-м сообщении.
     *
     * @param msg210 210-е сообщение из ЕРИП
     * @return байтовый массив с файлом в формате Excel.
     */
    public static byte[] convertToExcel(EripOfflineMsg210 msg210) throws IOException {
        String[] header = Arrays.copyOf(msg210.getHeader(), msg210.getHeader().length);
        header[3] = eripDateTimeToRussianDate(header[3]);

        List<EripOfflineMsg210.BodyLine> body = msg210.getBody();
        EripServiceImpl eripService = (EripServiceImpl) ApplicationContextProvider.getBean("eripService");
        Map<Integer, Service> services = body.stream().map(record -> record.getFields()[1]).distinct().map(Integer::parseInt)
                .collect(toMap(serviceNo -> serviceNo, eripService::getServiceCacheable));
        body.forEach(record -> {
            record.setDateFormatted(eripDateTimeToRussianDateTime(record.getFields()[9]));
            record.setServiceName(services.get(Integer.parseInt(record.getFields()[1])).getName());
        });
        try (InputStream _210template = Msg210Parser.class.getClassLoader().getResourceAsStream("210template.xlsx");
             ByteArrayOutputStream os = new ByteArrayOutputStream(50 * 1024)
        ) {
            Context context = new Context();
            context.putVar("header", header);
            context.putVar("body", msg210.getBody());
            context.putVar("comission", new BigDecimal(header[12]).subtract(new BigDecimal(header[14])));
            JxlsHelper.getInstance().processTemplate(_210template, os, context);
            return os.toByteArray();
        }
    }

    private static String eripDateTimeToRussianDate(String eripDate) {
        if (null == eripDate) {
            return null;
        }
        return eripDate.substring(6, 8) + '.' + eripDate.substring(4, 6) + '.' + eripDate.substring(0, 4);
    }

    private static String eripDateTimeToRussianDateTime(String eripDate) {
        if (null == eripDate) {
            return null;
        }
        return eripDate.substring(6, 8) + '.' + eripDate.substring(4, 6) + '.' + eripDate.substring(0, 4)
                + ' ' + eripDate.substring(8, 10) + ':' + eripDate.substring(10, 12) + ':' + eripDate.substring(12, 14);
    }
}
