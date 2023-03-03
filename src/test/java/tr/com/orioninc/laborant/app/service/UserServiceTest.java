package tr.com.orioninc.laborant.app.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.app.repository.UserRepository;
import tr.com.orioninc.laborant.security.config.PasswordConfig;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Log4j2
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LabRepository labRepository;
    @MockBean
    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, labRepository);
    }

    @Test
    @DisplayName("Should return false when the user does not exist")
    void deleteUserByUsernameWhenUserDoesNotExistThenReturnFalse() {
        String username = "test";
        boolean result = underTest.deleteUserByUsername(username);
        assertFalse(result);
    }

    @Test
    @DisplayName("Should delete the user when the user exists")
    void deleteUserByUsernameWhenUserExists() {
        User user = new User("test", "test", "test");
        userRepository.save(user);
        boolean result = underTest.deleteUserByUsername("test");
        assertTrue(result);
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