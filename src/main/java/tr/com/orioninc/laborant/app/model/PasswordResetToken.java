package tr.com.orioninc.laborant.app.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public PasswordResetToken() {
        super();
    }

    public PasswordResetToken(User user, String token, LocalDateTime expiryDate) {
        super();
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

}
