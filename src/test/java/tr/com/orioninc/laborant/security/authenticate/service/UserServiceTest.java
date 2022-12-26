package tr.com.orioninc.laborant.security.authenticate.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.security.config.PasswordConfig;
import tr.com.orioninc.laborant.security.model.User;
import tr.com.orioninc.laborant.security.repository.UserRepository;
import tr.com.orioninc.laborant.security.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Log4j2
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository);
    }

    @Test
    void addNewUser() {
        //when
        User user = new User(
                "test", "testPassword", "testRole");
        underTest.addNewUser(user);
        //then
        assertEquals(user, userRepository.findByUsername("test"));

    }

    @Test
    void deleteUserByUsername() {
        //given
        User user = new User(
                "test", "testPassword", "testRole");
        underTest.addNewUser(user);
        //when
        boolean result = underTest.deleteUserByUsername("test");
        //then
        assertTrue(result);
    }

    @Test
    void getUser() {
        //given
        User user = new User(
                "test", "testPassword", "testRole");
        underTest.addNewUser(user);
        //when
        int result = underTest.getAllUsers().size();
        //then
        assertEquals(1, result);
    }

    @Test
    void changePassword() {
        //given
        User user = new User("test", "testPassword", "testRole");
        underTest.addNewUser(user);
        //when
        underTest.changePassword("test", "testPassword", "newPassword");
        //then
        assertTrue(PasswordConfig.passwordEncoder().matches("newPassword", userRepository.findByUsername("test").getPassword()));

    }
}