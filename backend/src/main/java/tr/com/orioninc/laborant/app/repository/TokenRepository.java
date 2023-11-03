package tr.com.orioninc.laborant.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.com.orioninc.laborant.app.model.Token;
import tr.com.orioninc.laborant.app.model.User;

import javax.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);

    Token findByUser(User user);

    @Override
    @Transactional
    void deleteById(Long id);

    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByUserId(Integer id);

    Token findByEmail(String email);
}
