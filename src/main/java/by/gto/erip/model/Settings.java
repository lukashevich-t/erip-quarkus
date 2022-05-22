package by.gto.erip.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Настройки приложения, хранящиеся в БД
 */
@Entity
@Table(name = "ti.settings")
public class Settings implements Serializable {

    private String application;
    private String requestLogPath = "/var/log/requests_log";
    private String gaiServiceUrl = "http://127.0.0.1:18181/rq";
    private String gaiServiceUser = "bto";
    private String gaiServicePassword = "";
    private String gaiServiceV1Url = "http://127.0.0.1:18181/rq";
    private String gaiServiceV1User = "bto";
    private String gaiServiceV1Password = "";
    private Boolean gaiserviceEnabled = false;
    private Integer gaiserviceRetries = 3;

    private String eripFtpHost = "127.0.0.1";
    private String eripFtpUsername = "u32104480";
    private String eripFtpPassword = "";

    private String eripRegistriesTransportMethod = "webdav";
    private String eripWebdavUrl = "https://vpn.gto.by/xchange/210/";
    private String eripWebdavUsername = "xChange";
    private String eripWebdavPassword = "";

    //    private String eripMailHost = "smtp.gmail.com";
    //    private Integer eripMailPort = 587;
    //    private String eripMailFrom = "belgto.sender@gmail.com";
    private String eripMailTo = "belgto@gmail.com";
    //    private String eripMailUser = "belgto.sender@gmail.com";
    //    private String eripMailPassword = "";


    @Id
    @Column(name = "application", unique = true, nullable = false)
    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    @Column(name = "request_log_path")
    public String getRequestLogPath() {
        return requestLogPath;
    }

    public void setRequestLogPath(String requestLogPath) {
        this.requestLogPath = requestLogPath;
    }

    @Column(name = "gaiservice_url")
    public String getGaiServiceUrl() {
        return gaiServiceUrl;
    }

    public void setGaiServiceUrl(String v) {
        this.gaiServiceUrl = v;
    }

    @Column(name = "gaiservice_user")
    public String getGaiServiceUser() {
        return gaiServiceUser;
    }

    public void setGaiServiceUser(String v) {
        this.gaiServiceUser = v;
    }

    @Column(name = "gaiservice_password")
    public String getGaiServicePassword() {
        return gaiServicePassword;
    }

    public void setGaiServicePassword(String v) {
        this.gaiServicePassword = v;
    }

    @Column(name = "gaiservice_v1_url")
    public String getGaiServiceV1Url() {
        return gaiServiceV1Url;
    }

    public void setGaiServiceV1Url(String v) {
        this.gaiServiceV1Url = v;
    }

    @Column(name = "gaiservice_v1_user")
    public String getGaiServiceV1User() {
        return gaiServiceV1User;
    }

    public void setGaiServiceV1User(String v) {
        this.gaiServiceV1User = v;
    }

    @Column(name = "gaiservice_v1_password")
    public String getGaiServiceV1Password() {
        return gaiServiceV1Password;
    }

    public void setGaiServiceV1Password(String v) {
        this.gaiServiceV1Password = v;
    }

    @Column(name = "gaiservice_enabled")
    public Boolean getGaiserviceEnabled() {
        return gaiserviceEnabled;
    }

    public void setGaiserviceEnabled(Boolean gaiserviceEnabled) {
        this.gaiserviceEnabled = gaiserviceEnabled;
    }

    @Column(name = "gaiservice_retries")
    public Integer getGaiserviceRetries() {
        return gaiserviceRetries;
    }

    public void setGaiserviceRetries(Integer gaiserviceRetries) {
        this.gaiserviceRetries = gaiserviceRetries;
    }

    @Column(name = "erip_ftp_host")
    public String getEripFtpHost() {
        return eripFtpHost;
    }

    public void setEripFtpHost(String eripFtpHost) {
        this.eripFtpHost = eripFtpHost;
    }

    @Column(name = "erip_ftp_username")
    public String getEripFtpUsername() {
        return eripFtpUsername;
    }

    public void setEripFtpUsername(String eripFtpUsername) {
        this.eripFtpUsername = eripFtpUsername;
    }

    @Column(name = "erip_ftp_password")
    public String getEripFtpPassword() {
        return eripFtpPassword;
    }

    public void setEripFtpPassword(String eripFtpPassword) {
        this.eripFtpPassword = eripFtpPassword;
    }

    @Column(name = "erip_registries_transport_method")
    public String getEripRegistriesTransportMethod() {
        return eripRegistriesTransportMethod;
    }

    public void setEripRegistriesTransportMethod(String eripRegistriesTransportMethod) {
        this.eripRegistriesTransportMethod = eripRegistriesTransportMethod;
    }

    @Column(name = "erip_webdav_url")
    public String getEripWebdavUrl() {
        return eripWebdavUrl;
    }

    public void setEripWebdavUrl(String eripWebdavUrl) {
        this.eripWebdavUrl = eripWebdavUrl;
    }

    @Column(name = "erip_webdav_username")
    public String getEripWebdavUsername() {
        return eripWebdavUsername;
    }

    public void setEripWebdavUsername(String eripWebdavUsername) {
        this.eripWebdavUsername = eripWebdavUsername;
    }

    @Column(name = "erip_webdav_password")
    public String getEripWebdavPassword() {
        return eripWebdavPassword;
    }

    public void setEripWebdavPassword(String eripWebdavPassword) {
        this.eripWebdavPassword = eripWebdavPassword;
    }

    //    @Column(name = "erip_mail_host")
    //    public String getEripMailHost() {
    //        return eripMailHost;
    //    }
    //
    //    public void setEripMailHost(String eripMailHost) {
    //        this.eripMailHost = eripMailHost;
    //    }
    //
    //    @Column(name = "erip_mail_port")
    //    public Integer getEripMailPort() {
    //        return eripMailPort;
    //    }
    //
    //    public void setEripMailPort(Integer eripMailPort) {
    //        this.eripMailPort = eripMailPort;
    //    }
    //
    //    @Column(name = "erip_mail_from")
    //    public String getEripMailFrom() {
    //        return eripMailFrom;
    //    }
    //
    //    public void setEripMailFrom(String eripMailFrom) {
    //        this.eripMailFrom = eripMailFrom;
    //    }

    @Column(name = "erip_mail_to")
    public String getEripMailTo() {
        return eripMailTo;
    }

    public void setEripMailTo(String eripMailTo) {
        this.eripMailTo = eripMailTo;
    }

    //    @Column(name = "erip_mail_user")
    //    public String getEripMailUser() {
    //        return eripMailUser;
    //    }
    //
    //    public void setEripMailUser(String eripMailUser) {
    //        this.eripMailUser = eripMailUser;
    //    }
    //
    //    @Column(name = "erip_mail_password")
    //    public String getEripMailPassword() {
    //        return eripMailPassword;
    //    }
    //
    //    public void setEripMailPassword(String eripMailPassword) {
    //        this.eripMailPassword = eripMailPassword;
    //    }
}
