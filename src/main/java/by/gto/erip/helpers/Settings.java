package by.gto.erip.helpers;

public class Settings {
    private static String eripFtpHost = "127.0.0.1";
    private static String eripFtpUser = "me";
    private static String eripFtpPassword = "secret";
    private static String eripRegistriesTransportMethod = "webdav";
    private static String eripWebdavUser = "xchange";
    private static String eripWebdavPassword = "secret";
    private static String eripWebdavUrl = "https://vpn.gto.by/xchange/210/";
    private static String eripMailTo = "belgto@gmail.com";
    private static boolean gaiServiceEnabled = false;
    private static String gaiServiceUrl = "http://127.0.0.1:18181/rq";
    private static String gaiServiceV1Url = "http://127.0.0.1:8280/bto/vx/registration";
    private static int gaiRetries = 3;
    private static String requestLogPath = "/var/log/requests_log";
    private static String gaiServiceEncodedAuth = "";
    private static String gaiServiceV1EncodedAuth = "";

    /**
     * Сколько дней сохранять номера заказов.
     */
    private static int registeredPaymentKeepDays = 20;

    public static String getEripFtpHost() {
        return eripFtpHost;
    }

    public static void setEripFtpHost(String value) {
        if (value != null)
            eripFtpHost = value;
    }

    public static String getEripFtpUser() {
        return eripFtpUser;
    }

    public static void setEripFtpUser(String value) {
        if (value != null)
            eripFtpUser = value;
    }

    public static String getEripFtpPassword() {
        return eripFtpPassword;
    }

    public static void setEripFtpPassword(String value) {
        if (value != null)
            eripFtpPassword = value;
    }

    public static String getEripRegistriesTransportMethod() {
        return eripRegistriesTransportMethod;
    }

    public static void setEripRegistriesTransportMethod(String value) {
        if (value != null)
            eripRegistriesTransportMethod = value;
    }

    public static String getEripWebdavUser() {
        return eripWebdavUser;
    }

    public static void setEripWebdavUser(String value) {
        if (value != null)
            eripWebdavUser = value;
    }

    public static String getEripWebdavPassword() {
        return eripWebdavPassword;
    }

    public static void setEripWebdavPassword(String value) {
        if (value != null)
            eripWebdavPassword = value;
    }

    public static String getEripWebdavUrl() {
        return eripWebdavUrl;
    }

    public static void setEripWebdavUrl(String value) {
        if (value != null)
            eripWebdavUrl = value;
    }

    public static String getEripMailTo() {
        return eripMailTo;
    }

    public static void setEripMailTo(String value) {
        if (value != null)
            eripMailTo = value;
    }

    public static boolean isGaiServiceEnabled() {
        return gaiServiceEnabled;
    }

    public static void setGaiServiceEnabled(Boolean value) {
        if (value != null)
            gaiServiceEnabled = value;
    }

    public static String getGaiServiceUrl() {
        return gaiServiceUrl;
    }

    public static void setGaiServiceUrl(String value) {
        if (value != null)
            gaiServiceUrl = value;
    }

    public static String getGaiServiceV1Url() {
        return gaiServiceV1Url;
    }

    public static void setGaiServiceV1Url(String value) {
        if (value != null)
            gaiServiceV1Url = value;
    }

    public static int getGaiRetries() {
        return gaiRetries;
    }

    public static void setGaiRetries(Integer value) {
        if (value != null)
            gaiRetries = value;
    }

    public static String getRequestLogPath() {
        return requestLogPath;
    }

    public static void setRequestLogPath(String value) {
        if (value != null)
            requestLogPath = value;
    }

    public static String getGaiServiceEncodedAuth() {
        return gaiServiceEncodedAuth;
    }

    public static void setGaiServiceEncodedAuth(String value) {
        gaiServiceEncodedAuth = value;
    }

    public static String getGaiServiceV1EncodedAuth() {
        return gaiServiceV1EncodedAuth;
    }

    public static void setGaiServiceV1EncodedAuth(String v) {
        gaiServiceV1EncodedAuth = v;
    }

    public static int getRegisteredPaymentKeepDays() {
        return registeredPaymentKeepDays;
    }

    public static void setRegisteredPaymentKeepDays(Integer value) {
        if (value != null)
            registeredPaymentKeepDays = value;
    }
}
