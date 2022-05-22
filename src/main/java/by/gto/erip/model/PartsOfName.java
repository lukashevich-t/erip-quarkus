package by.gto.erip.model;

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
@Table(name = "parts_of_name")
public class PartsOfName implements java.io.Serializable {

    private static final long serialVersionUID = -3180830689507620862L;
    private Long id;
    private EripTransaction transaction;
    private String surname;
    private String firstname;
    private String patronymic;

    public PartsOfName() {
    }

    public PartsOfName(EripTransaction transaction) {
        this.transaction = transaction;
    }

    public PartsOfName(EripTransaction transaction, String surname, String firstname, String patronymic) {
        this.transaction = transaction;
        this.surname = surname;
        this.firstname = firstname;
        this.patronymic = patronymic;
    }

    @Column(name = "firstname", length = 30, nullable = true)
    public String getFirstname() {
        return this.firstname;
    }

    @GenericGenerator(name = "generatorNameParts", strategy = "foreign", parameters = @Parameter(name = "property", value = "transaction"))
    @Id
    @GeneratedValue(generator = "generatorNameParts")
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    @Column(name = "patronymic", length = 30, nullable = true)
    public String getPatronymic() {
        return this.patronymic;
    }

    @Column(name = "surname", length = 37, nullable = true)
    public String getSurname() {
        return this.surname;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    public EripTransaction getTransaction() {
        return this.transaction;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setTransaction(EripTransaction transaction) {
        this.transaction = transaction;
    }
}
