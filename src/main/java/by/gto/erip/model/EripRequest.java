package by.gto.erip.model;

import by.gto.erip.model.gai.R;
import by.gto.erip.model.hib.GaiRegCache;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "erip_request")
public class EripRequest implements java.io.Serializable {
    private static final long serialVersionUID = -5025877195059863693L;

    private long id;
    private Service service;
    private BigDecimal tariff;
    private Date operationDate;
    private Date lastModified;
    private String personalAccount;
    private String message;
    private short currency;
    private EripTransaction transaction;
    private short serviceFlags;

    @Column(name = "currency", nullable = false)
    public short getCurrency() {
        return currency;
    }

    public void setCurrency(short currency) {
        this.currency = currency;
    }


    public EripRequest() {
    }

    public EripRequest(long pa) {
        this.personalAccount = String.valueOf(pa);
    }

    public EripRequest(String pa) {
        this.personalAccount = pa;
    }

    public EripRequest(R regRecord) {
        if (regRecord != null) {
            this.personalAccount = regRecord.getCn();
        }
    }

    public EripRequest(GaiRegCache rc) {
        if (rc != null) {
            this.personalAccount = rc.getCertNum();
        }
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return this.id;
    }

    @Column(name = "last_modified", nullable = false)
    public Date getLastModified() {
        return lastModified;
    }

    @Column(name = "message", nullable = true, length = 255)
    public String getMessage() {
        return message;
    }

    @Column(name = "operation_date", nullable = false)
    public Date getOperationDate() {
        return this.operationDate;
    }


    @Column(name = "personal_account", nullable = false, length = 30)
    public String getPersonalAccount() {
        return this.personalAccount;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", nullable = false)
    public Service getService() {
        return this.service;
    }

    @Column(name = "tariff", nullable = false)
    public BigDecimal getTariff() {
        return tariff;
    }


    @OneToOne(fetch = FetchType.LAZY, mappedBy = "request", cascade = CascadeType.ALL)
    public EripTransaction getTransaction() {
        return transaction;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public void setPersonalAccount(String personalAccount) {
        this.personalAccount = personalAccount;
    }

    public void setService(Service services) {
        this.service = services;
    }

    public void setTariff(BigDecimal tariff) {
        this.tariff = tariff;
    }

    public void setTransaction(EripTransaction transaction) {
        this.transaction = transaction;
    }

    @Column(name = "service_flags")
    public short getServiceFlags() {
        return serviceFlags;
    }

    public void setServiceFlags(short serviceFlags) {
        this.serviceFlags = serviceFlags;
    }

    @Override
    public String toString() {
        return "EripRequest{" +
            "id=" + id +
            ", service=" + service +
            ", tariff=" + tariff +
            ", operationDate=" + operationDate +
            ", lastModified=" + lastModified +
            ", personalAccount='" + personalAccount + '\'' +
            ", message='" + message + '\'' +
            ", currency=" + currency +
            ", serviceFlags=" + serviceFlags +
            '}';
    }
}
