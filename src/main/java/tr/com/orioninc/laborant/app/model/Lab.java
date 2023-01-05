package tr.com.orioninc.laborant.app.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "lab")
public class Lab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String labName;
    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "host")
    private String host;
    @Column(name = "port")
    private Integer port;
    @Column(name = "reserved", nullable = true)
    private Boolean reserved = false;

    @ManyToMany(mappedBy = "labs")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    private List<User> users;

    @ManyToMany(mappedBy = "labs")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
    private List<Team> teams;




    public Lab() {
        this.id = null;
        this.labName = null;
        this.userName = null;
        this.password = null;
        this.host = null;
        this.port = null;
    }

    public Lab(String labName, String userName, String password, String host, Integer port) {
        this.labName = labName;
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public Lab(Boolean reserved) {
        this.reserved = reserved;
    }

    public Lab(Integer id, String labName, String userName, String password, String host, Integer port) {
        this.id = id;
        this.labName = labName;
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return "Lab {" +
                "id=" + id +
                ", labName='" + labName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
