package tr.com.orioninc.laborant.security.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tr.com.orioninc.laborant.app.model.Lab;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
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

    @ManyToMany
    @JoinTable(
            name = "lab_user_table",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "lab_id")
    )
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    private List<Lab> labs = new ArrayList<>();



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

