package tr.com.orioninc.laborant.security.authenticate.model;

import lombok.*;
import tr.com.orioninc.laborant.security.config.PasswordConfig;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "LAB_USER")
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username", unique = true)
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "user_role")
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = PasswordConfig.passwordEncoder().encode(password);
        this.role = role;
    }


    public User() {
        this.id = null;
        this.username = null;
        this.password = null;
        this.role = null;
    }

    public User(Integer id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = PasswordConfig.passwordEncoder().encode(password);
        this.role = role;
    }
}

