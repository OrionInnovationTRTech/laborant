package tr.com.orioninc.laborant.security.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "LAB_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "user_role")
    private String user_role;

    public User(String username, String password, String user_role) {
        this.username = username;
        this.password = password;
        this.user_role = user_role;
    }


    public User() {
        this.id = null;
        this.username = null;
        this.password = null;
        this.user_role = null;
    }

    public User(Integer id, String username, String password, String user_role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.user_role = user_role;
    }
}

