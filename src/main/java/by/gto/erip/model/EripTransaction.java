package by.gto.erip.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "erip_transaction")
public class EripTransaction implements java.io.Serializable {
    private static final long serialVersionUID = -4090954581302855333L;

    private Long id;
    private BigDecimal amount;
    private byte transactionstateId;
    private Date operationDate;
    private String authorizationType;
    private short agent;
    private EripRequest request;
    private long peerTransactionId;
    private Date lastModified;
    private EripTransactionBlockReason transactionBlockReason;
    private Address address;
    private Set<UserHistory> userHistories = new HashSet<>();
    private Set<History> histories = new LinkedHashSet<>();
    private PartsOfName partsOfName;
    private String name;
    private Integer payDocNumber;
    private Date payDocDate;

    public EripTransaction() {
    }

    public EripTransaction(Long id, byte transactionStateId, BigDecimal amount,
                           Date operationDate, String authorizationType, short agent,
                           long peerTransactionId, EripRequest request) {
        this.id = id;
        this.transactionstateId = transactionStateId;
        this.request = request;
        this.amount = amount;
        this.operationDate = operationDate;
        this.authorizationType = authorizationType;
        this.agent = agent;
        this.peerTransactionId = peerTransactionId;
    }

    public void addHistory(History h) {
        histories.add(h);
    }

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "transaction", cascade = CascadeType.ALL)
    public Address getAddress() {
        return this.address;
    }

    @Column(name = "agent", nullable = false)
    public short getAgent() {
        return this.agent;
    }

    @Column(name = "amount", nullable = false)
    public BigDecimal getAmount() {
        return this.amount;
    }

    @Column(name = "authorization_type", nullable = false, length = 10)
    public String getAuthorizationType() {
        return this.authorizationType;
    }

    @Transient
    public boolean isRepayable() {
        return this.transactionstateId == EripTransactionStatesEnum.TRANSACTION_COMMITED;
    }

    @OrderBy("date asc")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "history", joinColumns = {@JoinColumn(name = "transaction_id", referencedColumnName = "id")})
    public Set<History> getHistories() {
        return Collections.unmodifiableSet(this.histories);
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    @Column(name = "last_modified", nullable = false)
    public Date getLastModified() {
        return lastModified;
    }

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "transaction", cascade = CascadeType.ALL)
    public PartsOfName getPartsOfName() {
        return this.partsOfName;
    }

    @Column(name = "operation_date", nullable = false)
    public Date getOperationDate() {
        return this.operationDate;
    }

    @Column(name = "peer_transaction_id", nullable = false)
    public long getPeerTransactionId() {
        return this.peerTransactionId;
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "request_id", nullable = false)
    public EripRequest getRequest() {
        return request;
    }

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "transaction", cascade = CascadeType.ALL)
    public EripTransactionBlockReason getTransactionBlockReason() {
        return this.transactionBlockReason;
    }

    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
    public Set<UserHistory> getUserHistories() {
        return userHistories;
    }


    public void setAddress(Address address) {
        this.address = address;
    }

    public void setAgent(short agent) {
        this.agent = agent;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAuthorizationType(String authorizationType) {
        this.authorizationType = authorizationType;
    }

    protected void setHistories(Set<History> histories) {
        this.histories = histories;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setPartsOfName(PartsOfName nameParts) {
        this.partsOfName = nameParts;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public void setPeerTransactionId(long peerTransactionId) {
        this.peerTransactionId = peerTransactionId;
    }

    public void setRequest(EripRequest request) {
        this.request = request;
    }

    public void setTransactionBlockReason(EripTransactionBlockReason transactionBlockReason) {
        this.transactionBlockReason = transactionBlockReason;
    }

    public void setUserHistories(Set<UserHistory> userHistories) {
        this.userHistories = userHistories;
    }

    @Column(name = "transactionstate_id")
    public byte getTransactionstateId() {
        return transactionstateId;
    }

    public void setTransactionstateId(byte transactionstateId) {
        this.transactionstateId = transactionstateId;
    }

    @Column(name = "name", length = 90, nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "pay_doc_number")
    public Integer getPayDocNumber() {
        return payDocNumber;
    }

    public void setPayDocNumber(Integer payDocNumber) {
        this.payDocNumber = payDocNumber;
    }

    @Column(name = "pay_doc_date")
    public Date getPayDocDate() {
        return payDocDate;
    }

    public void setPayDocDate(Date payDocDate) {
        this.payDocDate = payDocDate;
    }

    @Override
    public String toString() {
        return "EripTransaction{"
            + "id=" + id
            + ", amount=" + amount
            + ", transactionstateId=" + transactionstateId
            + ", operationDate=" + operationDate
            + ", authorizationType='" + authorizationType + '\''
            + ", agent=" + agent
            + ", request=" + request
            + ", peerTransactionId=" + peerTransactionId
            + ", payDocNumber=" + payDocNumber
            + ", payDocDate=" + payDocDate
            + ", lastModified=" + lastModified
            + ", transactionBlockReason=" + transactionBlockReason
            + '}';
    }
}
