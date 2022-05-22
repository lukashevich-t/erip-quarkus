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
@Table(name = "address")
public class Address implements java.io.Serializable {
    private static final long serialVersionUID = 2954270358914057288L;
    @GenericGenerator(name = "generatorAddress", strategy = "foreign", parameters = @Parameter(name = "property", value = "transaction"))
    @Id
    @GeneratedValue(generator = "generatorAddress")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private EripTransaction transaction;

    @Column(name = "address", length = 99, nullable = true)
    private String address;
    @Column(name = "city", length = 30, nullable = true)
    private String city;
    @Column(name = "street", length = 30, nullable = true)
    private String street;
    @Column(name = "house", length = 10, nullable = true)
    private String house;
    @Column(name = "building", length = 10, nullable = true)
    private String building;
    @Column(name = "apartment", length = 10, nullable = true)
    private String apartment;

    public Address() {
    }

    public Address(EripTransaction transaction) {
        this.transaction = transaction;
    }

    public Address(EripTransaction transaction, String address, String city, String street, String house, String building,
                   String apartment) {
        this.transaction = transaction;
        this.address = address;
        this.city = city;
        this.street = street;
        this.house = house;
        this.building = building;
        this.apartment = apartment;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EripTransaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(EripTransaction transaction) {
        this.transaction = transaction;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return this.house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getBuilding() {
        return this.building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getApartment() {
        return this.apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

}
