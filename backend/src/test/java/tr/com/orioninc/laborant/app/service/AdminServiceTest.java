package tr.com.orioninc.laborant.app.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.app.repository.TeamRepository;
import tr.com.orioninc.laborant.app.repository.UserRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import java.util.ArrayList;

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
        underTest = new AdminService(labRepository, userRepository, teamRepository);
    }

    @Test
    @DisplayName("Should throw an exception when the lab does not exist")
    void getAssignedLabUsersWhenLabDoesNotExistThenThrowException() {
        assertThrows(NotFoundException.class, () -> underTest.getAssignedLabUsers("lab1"));
    }

    @Test
    @DisplayName("Should return a list of users when the lab exists")
    void getAssignedLabUsersWhenLabExistsThenReturnListOfUsers() {
        Lab lab = new Lab("lab1", "user1", "pass1", "host1", 22);
        labRepository.save(lab);
        User user = new User("user1", "pass1", "role1");
        userRepository.save(user);
        underTest.assignLabToUser("user1", "lab1");
        ArrayList<String> result = underTest.getAssignedLabUsers("lab1");
        assertThat(result).contains("user1");
    }

    @Test
    @DisplayName("Should throw an exception when the team does not exist")
    void getAssignedTeamLabsWhenTeamDoesNotExistThenThrowException() {
        String teamName = "team1";
        assertThrows(NotFoundException.class, () -> underTest.getAssignedTeamLabs(teamName));
    }

    @Test
    @DisplayName("Should throw an exception when the user does not exist")
    void getAssignedUserLabsWhenUserDoesNotExistThenThrowException() {
        String username = "test";
        assertThrows(NotFoundException.class, () -> underTest.getAssignedUserLabs(username));
    }

    @Test
    @DisplayName("Should return an empty list when the user is not assigned to any lab")
    void getAssignedUserLabsWhenUserIsNotAssignedToAnyLabThenReturnEmptyList() {
        User user = new User("user", "password", "user");
        userRepository.save(user);
        ArrayList<String> assignedLabs = underTest.getAssignedUserLabs("user");
        assertThat(assignedLabs).isEmpty();
    }

    @Test
    @DisplayName("Should throw an exception when the lab does not exist")
    void getAssignedLabTeamsWhenTheLabDoesNotExistThenThrowException() {
        assertThrows(NotFoundException.class, () -> underTest.getAssignedLabTeams("lab1"));
    }

    @Test
    @DisplayName("Should return an empty list when the lab is not assigned to any team")
    void getAssignedLabTeamsWhenLabIsNotAssignedToAnyTeamThenReturnEmptyList() {
        Lab lab = new Lab("lab1", "user1", "pass1", "host1", 22);
        labRepository.save(lab);
        ArrayList<String> assignedLabTeams = underTest.getAssignedLabTeams(lab.getLabName());
        assertThat(assignedLabTeams).isEmpty();
    }

    @Test
    @DisplayName("Should return a list of teams when the lab is assigned to some teams")
    void getAssignedLabTeamsWhenLabIsAssignedToSomeTeamsThenReturnListOfTeams() {
        Lab lab = new Lab("lab1", "user1", "pass1", "host1", 22);
        labRepository.save(lab);
        Team team1 = new Team("team1");
        Team team2 = new Team("team2");
        teamRepository.save(team1);
        teamRepository.save(team2);
        team1.getLabs().add(lab);
        team2.getLabs().add(lab);
        teamRepository.save(team1);
        teamRepository.save(team2);

        ArrayList<String> assignedTeams = underTest.getAssignedLabTeams("lab1");

        assertThat(assignedTeams).contains("team1", "team2");
    }

    @Test
    void assignUserThrowsAlreadyAssigned() {
        Lab lab = new Lab("assignUserThrowsAlreadyAssigned test lab",
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
    void canAssignLabToUser() {
        //given
        Lab lab = new Lab("canAssignUserToLab test lab",
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

    @Test
    void canUnassignLabFromUser() {
        //given
        Lab lab = new Lab("canUnassignUserFromLab test lab",
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
    void canAssignLabToTeam() {
        //given
        Lab lab = new Lab("canAssignTeamToLab test lab",
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
    void canUnassignLabFromTeam() {
        //given
        Lab lab = new Lab("canUnassignTeamFromLab test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        Team team = new Team("test");
        teamRepository.save(team);
        underTest.assignLabToTeam("canUnassignTeamFromLab test lab", "test");
        //when
        underTest.unassignLabFromTeam("canUnassignTeamFromLab test lab", "test");
        //then
        assertThat(underTest.findLab("canUnassignTeamFromLab test lab")).isNotNull();
        assertThat(!team.getLabs().contains(lab)).isTrue();
    }

    @Test
    void assignUserThrowsExceptionWhenUserNotFound() {
        Lab lab = new Lab("assignUserThrowsExceptionWhenUserNotFound test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            underTest.assignLabToUser("non-existent-user", "assignUserThrowsExceptionWhenUserNotFound test lab");
            fail("Expected user not found exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void assignLabThrowsExceptionWhenLabNotFound() {
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        //when
        try {
            underTest.assignLabToUser("test", "non-existent-lab");
            fail("Expected lab not found exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void unassignUserThrowsExceptionWhenUserNotFound() {
        Lab lab = new Lab("unassignUserThrowsExceptionWhenUserNotFound test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            underTest.unassignLabFromUser("non-existent-user", "unassignUserThrowsExceptionWhenUserNotFound test lab");
            fail("Expected user not found exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void unassignLabThrowsExceptionWhenLabNotFound() {
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        //when
        try {
            underTest.unassignLabFromUser("test", "non-existent-lab");
            fail("Expected lab not found exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void assignLabToTeamThrowsExceptionWhenLabNotFound() {
        Team team = new Team("test");
        teamRepository.save(team);
        //when
        try {
            underTest.assignLabToTeam("non-existent-lab", "test");
            fail("Expected lab not found exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void assignLabToTeamThrowsExceptionWhenTeamNotFound() {
        Lab lab = new Lab("assignLabToTeamThrowsExceptionWhenTeamNotFound test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            underTest.assignLabToTeam("assignLabToTeamThrowsExceptionWhenTeamNotFound test lab", "non-existent-team");
            fail("Expected team not found exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void unassignLabFromTeamThrowsExceptionWhenLabNotFound() {
        Team team = new Team("test");
        teamRepository.save(team);
        //when
        try {
            underTest.unassignLabFromTeam("non-existent-lab", "test");
            fail("Expected lab not found exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    void assignLabToUserWithNonExistentLabThrowsException() {
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        //when
        try {
            underTest.assignLabToUser("test", "non-existent lab");
            fail("Expected LabNotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void assignLabToUserWithNonExistentUserThrowsException() {
        Lab lab = new Lab("assignLabToUserWithNonExistentUser test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            underTest.assignLabToUser("non-existent user", "assignLabToUserWithNonExistentUser test lab");
            fail("Expected UserNotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void unassignLabFromUserWithNonExistentLabThrowsException() {
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        //when
        try {
            underTest.unassignLabFromUser("test", "non-existent lab");
            fail("Expected LabNotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void unassignLabFromUserWithNonExistentUserThrowsException() {
        Lab lab = new Lab("unassignLabFromUserWithNonExistentUser test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            underTest.unassignLabFromUser("non-existent user", "unassignLabFromUserWithNonExistentUser test lab");
            fail("Expected UserNotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void assignLabToTeamWithNonExistentLabThrowsException() {
        Team team = new Team("test");
        teamRepository.save(team);
        //when
        try {
            underTest.assignLabToTeam("non-existent lab", "test");
            fail("Expected LabNotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void assignLabToTeamWithNonExistentTeamThrowsException() {
        Lab lab = new Lab("assignLabToTeamWithNonExistentTeam test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        //when
        try {
            underTest.assignLabToTeam("assignLabToTeamWithNonExistentTeam test lab", "non-existent team");
            fail("Expected TeamNotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void assignUserThrowsNotFound() {
        //when
        try {
            underTest.assignLabToUser("test", "assignUserThrowsNotFound test lab");
            fail("Expected NotFound exception");
        } catch (NotFoundException e) {
            //expected
        }
    }

    @Test
    void assignUserThrowsIllegalArgument() {
        //when
        try {
            underTest.assignLabToUser("", "assignUserThrowsIllegalArgument test lab");
            fail("Expected IllegalArgument exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    void assignUserThrowsIllegalArgument2() {
        //when
        try {
            underTest.assignLabToUser("test", "");
            fail("Expected IllegalArgument exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    void assignUserThrowsIllegalArgument3() {
        //when
        try {
            underTest.assignLabToUser("", "");
            fail("Expected IllegalArgument exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    void assignUserThrowsIllegalArgument4() {
        //when
        try {
            underTest.assignLabToUser(null, "assignUserThrowsIllegalArgument4 test lab");
            fail("Expected IllegalArgument exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    void assignUserThrowsIllegalArgument5() {
        //when
        try {
            underTest.assignLabToUser("test", null);
            fail("Expected IllegalArgument exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    void assignUserThrowsIllegalArgument6() {
        //when
        try {
            underTest.assignLabToUser(null, null);
            fail("Expected IllegalArgument exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    void assignUser() {
        Lab lab = new Lab("assignUser test lab",
                "userName", "password", "host", 22);
        labRepository.save(lab);
        User user = new User("test", "test", "USER");
        userRepository.save(user);
        underTest.assignLabToUser("test", "assignUser test lab");
        assertEquals(1, user.getLabs().size());
    }

    @Test
    void unassignUserThrowsNotFound() {
        //when
        try {
            underTest.unassignLabFromUser("test", "unassignUserThrowsNotFound test lab");
            fail("Expected NotFound exception");
        } catch (NotFoundException e) {
            // expected
        }
    }

    @Test
    void unassignUserThrowsIllegalArgument() {
        //when
        try {
            underTest.unassignLabFromUser("", "unassignUserThrowsIllegalArgument test lab");
            fail("Expected IllegalArgument exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}