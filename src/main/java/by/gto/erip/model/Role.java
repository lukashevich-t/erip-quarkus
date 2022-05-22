package by.gto.erip.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name;

    @Column(name = "comment", length = 255)
    private String comment;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = {
        @JoinColumn(name = "role_id", nullable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "user_id", nullable = false, updatable = false)})
    private Set<User> users = new HashSet<User>(0);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        // return "Role [id=" + id + ", name=" + name + ", comment=" + comment +
        // "]";

        StringBuilder sb = new StringBuilder(
            "Role [id=" + id + ", name=" + name + ", comment=" + comment + ", roles= ");

        for (User user_ : users) {
            sb.append(user_.getName()).append(", ");
        }
        return sb.append("]\n").toString();
    }

}
