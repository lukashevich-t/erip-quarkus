package by.gto.erip.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service")
public class Service implements java.io.Serializable {
    private static final long serialVersionUID = 420217243670959092L;

    private static int nameLength = Integer.MAX_VALUE;

    static {
        Class cl = Service.class;
        if (cl.isAnnotationPresent(Entity.class)) {
            try {
                Method m = cl.getDeclaredMethod("getName");
                Column anno = (Column) m.getDeclaredAnnotation(Column.class);

/*				Field f = cl.getDeclaredField("name");
				Column anno = (Column)f.getDeclaredAnnotation(Column.class);
*/
                nameLength = anno.length();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int id;
    private String name;
    private short serviceFlags;
    private byte personalAccountFormat;
    private Date startDate;
    private Set<Tariff> tariffs = new HashSet<Tariff>(0);
    private Set<EripRequest> requests = new HashSet<EripRequest>(0);

    @Column(name = "service_flags")
    public short getServiceFlags() {
        return serviceFlags;
    }

    public void setServiceFlags(short serviceFlags) {
        this.serviceFlags = serviceFlags;
    }

    public Service() {
    }

    public Service(int id) {
        this.id = id;
    }

    public Service(int id, String name) {
        this.id = id;
        this.setName(name);
    }

    public Service(int id, String name, Set<Tariff> tariffs) {
        this.id = id;
        this.setName(name);
        this.tariffs = tariffs;
    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    @Column(name = "name", length = 100, unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY)
    public Set<EripRequest> getRequests() {
        return requests;
    }

    @OneToMany(mappedBy = "service", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<Tariff> getTariffs() {
        return tariffs;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = StringUtils.substring(name, 0, nameLength);
    }

    public void setRequests(Set<EripRequest> requests) {
        this.requests = requests;
    }

    public void setTariffs(Set<Tariff> tariffs) {
        this.tariffs = tariffs;
    }

    @Column(name = "personal_account_format", nullable = false)
    public byte getPersonalAccountFormat() {
        return personalAccountFormat;
    }

    public void setPersonalAccountFormat(byte personalAccountFormat) {
        this.personalAccountFormat = personalAccountFormat;
    }

    @Column(name = "start_date", nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "Service [id=" + id + ", name=" + name + "]";
    }
}
