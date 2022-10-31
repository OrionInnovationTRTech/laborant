package tr.com.orioninc.laborant.model;

import javax.persistence.*;

@Entity
@Table(name = "lab")
public class Lab
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="name")
    private String labName;
    @Column(name ="username")
    private String userName;
    @Column(name ="password")
    private String password;
    @Column(name ="host")
    private String host;
    @Column(name ="port")
    private Integer port;

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

    public Lab(Integer id, String labName, String userName, String password, String host, Integer port) {
        this.id = id;
        this.labName = labName;
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Lab{" +
                "id=" + id +
                ", labName='" + labName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
