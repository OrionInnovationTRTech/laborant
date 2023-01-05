package tr.com.orioninc.laborant.security.authenticate.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.UserRepository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Log4j2
public class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @Test
    void itShouldCheckIfUserExists() {
        // given
        String userName = "test";
        User user = new User(
                "test", "testPassword", "testRole");
        underTest.save(user);
        // when
        User foundUser = underTest.findByUsername(userName);
        // then
        assertThat(foundUser).isNotNull();
        log.info("User found: {}", foundUser);
    }

    @Test
    void itShouldCheckIfUserNotExists() {
        // given
        String userName = "test";
        User user = new User(
                "test", "testPassword", "testRole");
        underTest.save(user);
        // when
        User foundUser = underTest.findByUsername("test2");
        // then
        assertThat(foundUser).isNull();
        log.info("User not found");

    }
}
