package tr.com.orioninc.laborant.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.repository.TeamRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    @DisplayName("Should throw an exception when the name is already taken")
    void addNewTeamWhenNameIsAlreadyTakenThenThrowException() {
        Team team = new Team("team");
        when(teamRepository.findByName(team.getName())).thenReturn(team);
        assertThrows(AlreadyExistsException.class, () -> teamService.addNewTeam(team));
    }

    @Test
    @DisplayName("Should save the team when the name is not taken")
    void addNewTeamWhenNameIsNotTaken() {
        Team team = new Team("team1");
        when(teamRepository.findByName(team.getName())).thenReturn(null);
        teamService.addNewTeam(team);
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    @DisplayName("Should throw an exception when the team is not found")
    void deleteTeamByNameWhenTeamIsNotFoundThenThrowException() {
        String teamName = "team";
        when(teamRepository.findByName(teamName)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> teamService.deleteTeamByName(teamName));
    }

    @Test
    @DisplayName("Should delete the team when the team is found")
    void deleteTeamByNameWhenTeamIsFound() {
        String teamName = "team";
        Team team = new Team(teamName);
        when(teamRepository.findByName(teamName)).thenReturn(team);
        teamService.deleteTeamByName(teamName);
        verify(teamRepository, times(1)).delete(team);
    }

    @Test
    @DisplayName("Should throw an exception when the team is not found")
    void getTeamByNameWhenTeamIsNotFoundThenThrowException() {
        String teamName = "team";
        when(teamRepository.findByName(teamName)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> teamService.getTeamByName(teamName));
    }

    @Test
    @DisplayName("Should return the team when the team is found")
    void getTeamByNameWhenTeamIsFound() {
        String teamName = "team";
        Team team = new Team(teamName);
        when(teamRepository.findByName(teamName)).thenReturn(team);
        teamService.getTeamByName(teamName);
        verify(teamRepository, times(1)).findByName(teamName);
    }

    @Test
    @DisplayName("Should throw an notfound exception when no teams found at all")
    void getAllTeams() {
        assertThrows(NotFoundException.class, () -> teamService.getAllTeams());
    }
}