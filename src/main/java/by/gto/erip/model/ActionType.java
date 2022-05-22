package by.gto.erip.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "action_type")
public class ActionType implements java.io.Serializable {
    private static final long serialVersionUID = -8365996909720954887L;

    private byte id;
    private String name;

    private Set<History> histories = new HashSet<History>(0);

    private Set<UserHistory> userHistories = new HashSet<UserHistory>(0);


    public ActionType() {
    }

    public ActionType(byte id, String name) {
        this.id = id;
        this.name = name;
    }

    public ActionType(byte id) {
        this.id = id;
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public byte getId() {
        return this.id;
    }

    public void setId(byte id) {
        this.id = id;
    }

    @Column(name = "NAME", length = 255, unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHistories(Set<History> histories) {
        this.histories = histories;
    }

    @OneToMany(mappedBy = "actionType", fetch = FetchType.LAZY)
    public Set<UserHistory> getUserHistories() {
        return userHistories;
    }

    public void setUserHistories(Set<UserHistory> userHistories) {
        this.userHistories = userHistories;
    }


}
