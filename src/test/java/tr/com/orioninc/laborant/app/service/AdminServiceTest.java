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
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.UserRepository;


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
        assertThat(underTest.findLab("canAssignUserToLab test lab")).isNotNull();
        assertThat(user.getLabs()).contains(lab);
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
        assertThat(underTest.findLab("canUnassignUserFromLab test lab")).isNotNull();
        assertThat(!user.getLabs().contains(lab)).isTrue();
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
        assertThat(underTest.findLab("canAssignTeamToLab test lab")).isNotNull();
        assertThat(team.getLabs()).contains(lab);
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
        assertThat(underTest.findLab("canUnassignTeamFromLab test lab")).isNotNull();
        assertThat(!team.getLabs().contains(lab)).isTrue();
    }
}