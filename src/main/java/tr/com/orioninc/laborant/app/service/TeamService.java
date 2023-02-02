package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.repository.TeamRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class TeamService {

    private TeamRepository teamRepo;

    public Team addNewTeam(Team team) {
        if (teamRepo.findByName(team.getName())==null) {
            teamRepo.save(team);
            log.info("[addNewTeam] Team added: {}", team.toString());
            return team;
        } else {
            log.error("[addNewTeam] Team {} already exists", team.getName());
            throw new AlreadyExistsException("Team " + team.getName() + " already exists");
        }
    }

    public boolean deleteTeamByName(String teamName) {
        Team team = teamRepo.findByName(teamName);
        if (team != null) {
            teamRepo.delete(team);
            log.info("[deleteTeamByName] Team {} deleted", teamName);
            return true;
        } else {
            log.info("[deleteTeamByName] Team {} not found", teamName);
            throw new NotFoundException("Team " + teamName + " not found");
        }
    }


    public List<Team> getAllTeams() {
        if (teamRepo.findAll().isEmpty()) {
            log.warn("[getAllTeams] No team found");
            throw new NotFoundException("No team found in database");
        } else {
            return teamRepo.findAll();
        }
    }

    public Team getTeamByName(String teamName) {
        Team team = teamRepo.findByName(teamName);
        if (team != null) {
            log.info("[getTeamByName] Team {} found", teamName);
            return team;
        } else {
            log.warn("[getTeamByName] Team {} not found", teamName);
            throw new NotFoundException("Team " + teamName + " not found");
        }
    }
}
