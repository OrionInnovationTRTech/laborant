package tr.com.orioninc.laborant.app.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotConnectedException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DataJpaTest
class AdminServiceTest {


    @Autowired
    private LabRepository labRepository;
    private AdminService underTest;


    @BeforeEach
    void setUp() {
        underTest = new AdminService(labRepository, null);
    }

    @Test
    void canGetAllLabs() {
        // given
        Lab lab = new Lab(
                "testLab", "testUser", "testPassword", "testHost", 22);
        labRepository.save(lab);
        // when
        List<Lab> test = underTest.getAllLabs();
        // then
        assertEquals(labRepository.findAll(), test);
    }

    @Test
    void canFindLabByName() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        labRepository.save(lab);
        // when
        Lab foundLab = underTest.findLabByName(labName);
        // then
        assertEquals(lab, foundLab);
        assertThat(foundLab).isNotNull().returns("testLab", Lab::getLabName);
    }

    @Test
    void canUpdateLabByName() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        labRepository.save(lab);
        log.info("Lab saved: {}", lab);
        Lab toBeUpdated = new Lab(
                labName, "update", "update", "update", 222);
        // when
        underTest.updateLabByName(labName, toBeUpdated);
        // then
        assertThat(underTest.findLabByName(labName)).isNotNull()
                .returns("update", Lab::getUserName)
                .returns("update", Lab::getPassword)
                .returns("update", Lab::getHost)
                .returns(222, Lab::getPort);
    }
    @Test
    void willGiveErrorWhenUpdateLabByName() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        labRepository.save(lab);
        log.info("Lab saved: {}", lab);
        Lab toBeUpdated = new Lab(
                "update", "update", "update", "update", 222);
        // when
        try {
            underTest.updateLabByName(toBeUpdated.getLabName(), toBeUpdated);
            fail("Expected NotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
        // then
        // the test will pass if the NotFound exception is thrown
    }


    @Test
    void canAddNewLab() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        // when
        try {
            underTest.addNewLab(lab);
            fail("Expected NotConnect exception since the testlab is not connected");
        } catch (NotConnectedException notConnected) {
            // expected
        }
    }

    @Test
    void willGiveErrorWhenAddingLabNameExists() {
        //given
        Lab lab = new Lab( "test",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        Lab actuallySame = new Lab( "test",
                "userName", "password", "host", 22);
        //when
        try {
            underTest.addNewLab(actuallySame);
            fail("Expected NotFound exception");
        } catch (AlreadyExistsException e) {
            // expected
        }
    }
    @Test
    void willGiveErrorWhenPairOfLabNameAndHostExists() {
        //given
        Lab lab = new Lab( "test",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        Lab actuallySamePair = new Lab( "test2",
                "userName", "password2", "host", 222);
        //when
        try {
            underTest.addNewLab(actuallySamePair);
            fail("Expected NotFound exception");
        } catch (AlreadyExistsException e) {
            // expected
        }
    }

    @Test
    void canGetLab() {
        //given
        Lab lab = new Lab( "canGetLab test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        Lab gettedLab = underTest.getLab("canGetLab test lab");
        //then
        assertEquals(gettedLab, lab);
    }
    @Test
    void willGiveErrorWhenCantGetLab() {
        //given
        Lab lab = new Lab( "canGetLab test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            underTest.getLab("canGetLab test lab2");
            fail("Expected NotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void canDeleteLabByName() {
        //given
        Lab lab = new Lab( "canDeleteLabByName test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        Lab deletedLab = underTest.deleteLabByName("canDeleteLabByName test lab");
        //then
        assertNotEquals(null, deletedLab);
    }

    @Test
    void willGiveErrorWhenCantDeleteLabByName() {
        //given
        Lab lab = new Lab( "canDeleteLabByName test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            Lab deletedLab = underTest.deleteLabByName("canDeleteLabByName test lab2");
            fail("Expected NotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }
}