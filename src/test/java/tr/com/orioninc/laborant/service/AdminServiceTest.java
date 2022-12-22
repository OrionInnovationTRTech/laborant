package tr.com.orioninc.laborant.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.repository.LabRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Log4j2
@DataJpaTest
class AdminServiceTest {


    @Autowired
    private LabRepository labRepository;
    private AdminService underTest;


    @BeforeEach
    void setUp() {
        underTest = new AdminService(labRepository);
    }

    @Test
    void canGetAllLabs() {
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
        underTest.updateLabByName(toBeUpdated.getLabName(), toBeUpdated);
        // then
        assertNotEquals(underTest.getLab(labName), toBeUpdated);
    }

    @Test
    void canAddNewLab() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        // when
        underTest.addNewLab(lab);
        log.info("Lab added: {}", lab);
        // then
        assertNotEquals(null, labRepository.findByLabName(labName));
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
        underTest.addNewLab(actuallySame);
        //then
        assertEquals(null, underTest.addNewLab(actuallySame));
        log.info("Lab already exists");
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
        Lab test = underTest.addNewLab(actuallySamePair);
        //then
        assertEquals(null, test);
        log.info("Lab already exists");
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
        Lab gettedLab = underTest.getLab("canGetLab test lab2");
        //then
        assertEquals(null, gettedLab);
    }

    @Test
    void canDeleteLabByName() {
        //given
        Lab lab = new Lab( "canDeleteLabByName test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        boolean isDeleted = underTest.deleteLabByName("canDeleteLabByName test lab");
        //then
        assertEquals(true, isDeleted);
    }

    @Test
    void willGiveErrorWhenCantDeleteLabByName() {
        //given
        Lab lab = new Lab( "canDeleteLabByName test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        boolean isDeleted = underTest.deleteLabByName("canDeleteLabByName test lab2");
        //then
        assertEquals(false, isDeleted);
    }
}