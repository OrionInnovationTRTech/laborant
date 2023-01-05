package tr.com.orioninc.laborant.app.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.app.repository.TeamRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DataJpaTest
class AdminServiceTest {


    @Autowired
    private LabRepository labRepository;
    private AdminService underTest;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;


    @BeforeEach
    void setUp() {
        underTest = new AdminService(labRepository, userRepository,teamRepository);
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
        underTest.addNewLab(lab);
        // then
        assertThat(underTest.findLabByName(labName)).isNotNull()
                .returns("testLab", Lab::getLabName)
                .returns("testUser", Lab::getUserName)
                .returns("testPassword", Lab::getPassword)
                .returns("testHost", Lab::getHost)
                .returns(22, Lab::getPort);
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
        Lab obtLab = underTest.getLab("canGetLab test lab");
        //then
        assertEquals(obtLab, lab);
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

    @Test
    void canAssignLabToUser(){
        //given
        Lab lab = new Lab( "canAssignUserToLab test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        //when
        underTest.assignLabToUser("test", "canAssignUserToLab test lab");
        //then
        assertThat(underTest.findLabByName("canAssignUserToLab test lab")).isNotNull();
        assertThat(user.getLabs().contains(lab));
    }

    @Test void canUnassignLabFromUser(){
        //given
        Lab lab = new Lab( "canUnassignUserFromLab test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        underTest.assignLabToUser("test", "canUnassignUserFromLab test lab");
        //when
        underTest.unassignLabFromUser("test", "canUnassignUserFromLab test lab");
        //then
        assertThat(underTest.findLabByName("canUnassignUserFromLab test lab")).isNotNull();
        assertThat(!user.getLabs().contains(lab));
    }

    @Test
    void canAssignLabToTeam () {
        //given
        Lab lab = new Lab( "canAssignTeamToLab test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        Team team = new Team("test");
        teamRepository.save(team);
        //when
        underTest.assignLabToTeam("canAssignTeamToLab test lab", "test");
        //then
        assertThat(underTest.findLabByName("canAssignTeamToLab test lab")).isNotNull();
        assertThat(team.getLabs().contains(lab));
    }

    @Test
    void canUnassignLabFromTeam () {
        //given
        Lab lab = new Lab( "canUnassignTeamFromLab test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        Team team = new Team( "test");
        teamRepository.save(team);
        underTest.assignLabToTeam("canUnassignTeamFromLab test lab", "test");
        //when
        underTest.unassignLabFromTeam("canUnassignTeamFromLab test lab", "test");
        //then
        assertThat(underTest.findLabByName("canUnassignTeamFromLab test lab")).isNotNull();
        assertThat(!team.getLabs().contains(lab));
    }
    @Test
    void assignUserThrowsAlreadyAssigned(){
        Lab lab = new Lab( "assignUserThrowsAlreadyAssigned test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        User user2 = new User("test2", "test2", "USER");
        userRepository.save(user2);
        underTest.assignLabToUser("test", "assignUserThrowsAlreadyAssigned test lab");
        //when
        try {
            underTest.assignLabToUser("test2", "assignUserThrowsAlreadyAssigned test lab");
            fail("Expected AlreadyAssigned exception");
        } catch (AlreadyExistsException e) {
            // expected
        }

    }
}