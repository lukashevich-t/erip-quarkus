package by.gto.erip.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "login"))
public class User implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7879870956633232015L;

    private int id;
    private String name;
    private String login;
    private String passwd_sha;
    private String comment;

    private Set<Role> roles = new HashSet<Role>(0);

    private Set<UserHistory> userHistories = new HashSet<UserHistory>(0);

    @Column(name = "comment", length = 255)
    public String getComment() {
        return comment;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    @Column(name = "login", length = 50, nullable = false, unique = true)
    public String getLogin() {
        return login;
    }

    @Column(name = "name", length = 255)
    public String getName() {
        return name;
    }

    @Column(name = "passwd_sha", length = 100, nullable = false)
    public String getPasswd_sha() {
        return passwd_sha;
    }


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", catalog = "erip", joinColumns = {
        @JoinColumn(name = "user_id", nullable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "role_id", nullable = false, updatable = false)})
    public Set<Role> getRoles() {
        return roles;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    public Set<UserHistory> getUserHistories() {
        return userHistories;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswd_sha(String password) {
        this.passwd_sha = password;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setUserHistories(Set<UserHistory> userHistories) {
        this.userHistories = userHistories;
    }

    @Override
    public String toString() {
        /*return "User [id=" + id + ", name=" + name + ", login=" + login + ", password=" + password + ", roles=" + roles
                + ", comment=" + comment + "]";*/

        StringBuilder sb = new StringBuilder("User [id=" + id + ", name=" + name + ", login=" + login + ", password="
            + passwd_sha + ", comment=" + comment + ", roles= ");

        for (Role role_ : roles) {
            sb.append(role_.getName()).append(", ");
        }
        return sb.append("]\n").toString();
    }
}
