package by.gto.erip.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_info")
public class OrderInfo implements java.io.Serializable {
    private long transactionId;
    private Integer unp;
    private byte legalType;
    private String certNumber;
    private Integer orderNo;

    public OrderInfo() {
    }

    public OrderInfo(long transactionId, RegisteredPayment rp) {
        this.transactionId = transactionId;

        if (rp == null) {
            return;
        }

        this.unp = rp.getUnp();
        this.legalType = rp.getLegalType();
        this.certNumber = rp.getCertNumber();
        this.orderNo = rp.getId().intValue();
    }

    @Id
    @Column(name = "transaction_id", unique = true, nullable = false)
    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "unp", nullable = true)
    public Integer getUnp() {
        return unp;
    }

    public void setUnp(Integer unp) {
        this.unp = unp;
    }

    @Column(name = "legal_type", nullable = false)
    public byte getLegalType() {
        return legalType;
    }

    public void setLegalType(byte legalType) {
        this.legalType = legalType;
    }

    @Column(name = "cert_number", nullable = true)
    public String getCertNumber() {
        return certNumber;
    }

    public void setCertNumber(String certNumber) {
        this.certNumber = certNumber;
    }

    @Column(name = "order_no", nullable = true)
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
}
