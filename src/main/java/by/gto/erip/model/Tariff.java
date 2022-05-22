package by.gto.erip.model;

import java.math.BigDecimal;
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
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "tariff", uniqueConstraints = {@UniqueConstraint(columnNames = {"service_id", "start_date"})})
public class Tariff implements java.io.Serializable {

    private static final long serialVersionUID = 8219640721585545194L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "sum", nullable = false)
    private BigDecimal sum;

    @Column(name = "calculated", nullable = false)
    private boolean calculated;

    @Column(name = "main", nullable = false)
    private boolean main;

    public Tariff() {
    }

    public Tariff(Service service, Date startDate, Date endDate, BigDecimal sum, boolean calculated, boolean main) {
        this.service = service;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sum = sum;
        this.calculated = calculated;
        this.main = main;
    }

    public Integer getId() {
        return this.id;
    }

    public Service getService() {
        return service;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public BigDecimal getSum() {
        return this.sum;
    }

    public boolean isCalculated() {
        return this.calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Tariff{" +
            "id=" + id +
            ", service=" + service +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", sum=" + sum +
            ", calculated=" + calculated +
            ", main=" + main +
            '}';
    }
}
