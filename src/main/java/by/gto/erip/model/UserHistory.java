package by.gto.erip.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_history")
public class UserHistory implements Serializable {
    private static final long serialVersionUID = -4019205552919880084L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_type_id", nullable = false)
    private ActionType actionType;

    //	@Column(name = "action_type_id", unique = true, nullable = false)
    //	private byte actionTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true)
    private EripTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date", nullable = false)
    private Date date;

    //@Column(name = "content", nullable = true)
    @Column(name = "content", nullable = true, columnDefinition = "Text")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public EripTransaction getTransaction() {
        return transaction;
    }

    //	public byte getActionTypeId() {
    //		return actionTypeId;
    //	}
    //
    //	public void setActionTypeId(byte actionTypeId) {
    //		this.actionTypeId = actionTypeId;
    //	}

    public void setTransaction(EripTransaction transaction) {
        this.transaction = transaction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
