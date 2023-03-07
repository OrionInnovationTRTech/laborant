package tr.com.orioninc.laborant.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.orioninc.laborant.app.model.PasswordResetToken;
import tr.com.orioninc.laborant.app.model.User;

import javax.transaction.Transactional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

    @Override
    @Transactional
    void deleteById(Long id);

    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByUserId(Integer id);
}
