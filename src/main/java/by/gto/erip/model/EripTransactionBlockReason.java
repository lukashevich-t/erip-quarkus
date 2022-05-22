package by.gto.erip.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "erip_transaction_block_reason")
public class EripTransactionBlockReason implements java.io.Serializable {
    private static final long serialVersionUID = -4171891122232273990L;

    @GenericGenerator(name = "generatorReasons", strategy = "foreign", parameters = @Parameter(name = "property", value = "transaction"))
    @Id
    @GeneratedValue(generator = "generatorReasons")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private EripTransaction transaction;

    @Column(name = "reason", length = 255, nullable = false)
    private String reason;

    @Column(name = "date", nullable = false)
    private Date date;


    public EripTransactionBlockReason() {
    }

    public EripTransactionBlockReason(EripTransaction transaction, String reason, Date date) {
        this.transaction = transaction;
        this.reason = reason;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EripTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(EripTransaction transaction) {
        this.transaction = transaction;
    }

    public EripTransaction getTransactions() {
        return this.transaction;
    }

    public void setTransactions(EripTransaction transaction) {
        this.transaction = transaction;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "EripTransactionBlockReason{" +
            "id=" + id +
            ", reason='" + reason + '\'' +
            ", date=" + date +
            '}';
    }
}
