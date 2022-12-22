package tr.com.orioninc.laborant.app.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.exception.AlreadyExists;
import tr.com.orioninc.laborant.exception.NotFound;

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
        underTest = new AdminService(labRepository);
    }

    @Test
    void canGetAllLabs() {
        // given
        Lab lab = new Lab("test", "test", "test", "test", 1);
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
        assertEquals(underTest.getLab(labName).getLabName(), toBeUpdated.getLabName());
        assertEquals(underTest.getLab(labName).getUserName(), toBeUpdated.getUserName());
        assertEquals(underTest.getLab(labName).getPassword(), toBeUpdated.getPassword());
        assertEquals(underTest.getLab(labName).getHost(), toBeUpdated.getHost());
        assertEquals(underTest.getLab(labName).getPort(), toBeUpdated.getPort());
    }
    @Test
    void willGiveErrorWhenUpdateLabByName() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        labRepository.save(lab);
        Exception exception = assertThrows(NotFound.class, () -> { underTest.updateLabByName("notFound", lab); });


        String expectedMessage = "isn't a lab";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
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
        Exception exception = assertThrows(AlreadyExists.class, () -> { underTest.addNewLab(actuallySame); });
        //then
        String expectedMessage = "There is already a lab";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    void willGiveErrorWhenPairOfLabNameAndHostExists() {
        //given
        Lab lab = new Lab( "test",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        Lab actuallySamePair = new Lab( "test2",
                "userName", "password2", "host", 222);

        Exception exception = assertThrows(AlreadyExists.class, () -> { underTest.addNewLab(actuallySamePair); });

        String expectedMessage = "There is";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

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

        Exception exception = assertThrows(NotFound.class, () -> { underTest.getLab("notFound"); });

        String expectedMessage = "no lab";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
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
        Exception exception = assertThrows(NotFound.class, () -> { underTest.deleteLabByName("canDeleteLabByName test lab2"); });
        String expectedMessage = "There";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}