package by.gto.erip.model;

import by.gto.erip.model.EripActionTypesEnum;

import javax.persistence.*;
import java.util.Date;

@Embeddable
public class History implements java.io.Serializable {
    private static final long serialVersionUID = -1347622637132833585L;

    private int actionTypeId;
    private Date date;

    public History() {
    }

    public History(int actionTypeId, Date date) {
        this.actionTypeId = actionTypeId;
        this.date = date;
    }

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "action_type_id", nullable = false)
    public int getActionTypeId() {
        return actionTypeId;
    }

    public void setActionTypeId(int actionTypeId) {
        this.actionTypeId = actionTypeId;
    }

    @Override
    public String toString() {
        return "History{" +
            "actionTypeId=" + actionTypeId +
            "(" + EripActionTypesEnum.getDescription(actionTypeId) +
            "), date=" + date +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        History history = (History) o;

        if (actionTypeId != history.actionTypeId) return false;
        return date.equals(history.date);
    }

    @Override
    public int hashCode() {
        int result = (int) actionTypeId;
        result = 31 * result + date.hashCode();
        return result;
    }
}
