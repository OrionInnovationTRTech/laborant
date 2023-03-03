
package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.app.repository.TeamRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.UserRepository;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class AdminService {

    private LabRepository labRepo;
    private UserRepository userRepo;
    private TeamRepository teamRepo;

    public Lab findLab(String labName){
        Lab lab = labRepo.findByLabName(labName);
        if (lab == null) {
            throw new NotFoundException("Lab not found");
        }
        return lab;
    }

    public String assignLabToTeam(String labName, String teamName) {
        log.debug("[assignLabToTeam] called");
        Lab lab = labRepo.findByLabName(labName);
        Team team = teamRepo.findByName(teamName);
        if (Objects.equals(labName, "") || labName == null) {
            log.error("[assignLabToTeam] Lab name is empty");
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if (Objects.equals(teamName, "") || teamName == null) {
            log.error("[assignLabToTeam] Team name is empty");
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        if (Objects.isNull(lab)) {
            log.error("[assignLabToTeam] Lab not found");
            throw new NotFoundException("Lab not found");
        } else if (Objects.isNull(team)) {
            log.error("[assignLabToTeam] Team not found");
            throw new NotFoundException("Team not found");
        } else if (team.getLabs().contains(lab)) {
            log.error("[assignLabToTeam] Lab {} already assigned to team {}", labName, teamName);
            throw new AlreadyExistsException("Lab already assigned to team");
        } else {
            team.getLabs().add(lab);
            teamRepo.save(team);
            log.info("[assignLabToTeam] Lab {} assigned to team {}", labName, teamName);
            return "Lab assigned to team";
        }
    }

    public String unassignLabFromTeam(String labName, String teamName) {
        log.debug("[unassignTeamFromLab] called");
        Lab lab = labRepo.findByLabName(labName);
        Team team = teamRepo.findByName(teamName);
        if (Objects.equals(labName, "") || labName == null) {
            log.error("[unassignLabFromTeam] Lab name is empty");
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if (Objects.equals(teamName, "") || teamName == null) {
            log.error("[unassignLabFromTeam] Team name is empty");
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        if (Objects.isNull(lab)) {
            log.error("[unassignLabFromTeam] Lab not found");
            throw new NotFoundException("Lab not found");
        } else if (Objects.isNull(team)) {
            log.error("[unassignLabFromTeam] Team not found");
            throw new NotFoundException("Team not found");
        } else if (!team.getLabs().contains(lab)) {
            log.error("[unassignLabFromTeam] Lab {} not assigned to team {}", labName, teamName);
            throw new NotFoundException("Lab not assigned to team");
        } else {
            team.getLabs().remove(lab);
            teamRepo.save(team);
            log.info("[unassignLabFromTeam] Lab unasigned from team");
            return "Lab unassigned from team";
        }
    }

    public String assignLabToUser(String userName, String labName) {
        log.debug("[assignLabToUser] called");
        User user = userRepo.findByUsername(userName);
        Lab lab = labRepo.findByLabName(labName);
        if (Objects.equals(userName, "") || userName == null) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (Objects.equals(labName, "") || labName == null) {
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if (Objects.isNull(user)) {
            log.warn("[assignLabToUser] User with username {} not found in database", userName);
            throw new NotFoundException("User with username " + userName + " not found in database");
        } else if (Objects.isNull(lab)) {
            log.warn("[assignLabToUser] Lab with name {} not found in database", labName);
            throw new NotFoundException("Lab with name " + labName + " not found in database");
        } else {
            if (user.getLabs().contains(lab)) {
                log.warn("[assignLabToUser] User with username {} already assigned to lab with name {}", userName, labName);
                throw new AlreadyExistsException(userName + " already assigned to lab " + labName);
            } else {
                for (User u : userRepo.findAll()) {
                    if (u.getLabs().contains(lab)) {
                        log.warn("[assignLabToUser] Lab with name {} already assigned to user with username {}", labName, u.getUsername());
                        throw new AlreadyExistsException("Lab with name " + labName + " already assigned to user with username " + u.getUsername());
                    }
                }
                user.getLabs().add(lab);
                userRepo.save(user);
                log.info("[assignLabToUser] User with username {} assigned to lab with name {}", userName, labName);
                return "User with username " + userName + " assigned to " + labName;
            }
        }
    }

    public String unassignLabFromUser(String userName, String labName) {
        log.debug("[unassignLabFromUser] called");
        User user = userRepo.findByUsername(userName);
        Lab lab = labRepo.findByLabName(labName);
        if (Objects.equals(userName, "") || userName == null) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (Objects.equals(labName, "") || labName == null) {
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if (Objects.isNull(user)) {
            log.warn("[unassignLabFromUser] User with username {} not found in database", userName);
            throw new NotFoundException("User with username " + userName + " not found in database");
        } else if (Objects.isNull(lab)) {
            log.warn("[unassignLabFromUser] Lab with name {} not found in database", labName);
            throw new NotFoundException("Lab with name " + labName + " not found in database");
        } else {
            if (user.getLabs().contains(lab)) {
                user.getLabs().remove(lab);
                userRepo.save(user);
                log.info("[unassignLabFromUser] User with username {} unassigned from lab with name {}", userName, labName);
                return "User with username " + userName + " unassigned from lab with name " + labName;
            } else {
                log.warn("[unassignLabFromUser] User with username {} not assigned to lab with name {}", userName, labName);
                throw new NotFoundException("User with username " + userName + " not assigned to lab with name " + labName);
            }
        }
    }

    public ArrayList<String> getAssignedLabTeams(String labName) {
        log.debug("[getAssignedLabTeams] called");
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        Lab lab = labRepo.findByLabName(labName);
        if (Objects.isNull(lab)) {
            log.warn("[getAssignedLabTeams] no lab in the database named: {}", labName);
            throw new NotFoundException("There isn't a lab named " + labName + " found in the database to get assigned teams");
        } else {
            ArrayList<String> assignedTeams = new ArrayList<>();
            for (Team team : teamRepo.findAll()) {
                if (team.getLabs().contains(lab)) {
                    assignedTeams.add(team.getName());
                }
            }
            log.info("[getAssignedLabTeams] returning assigned teams for lab: {}", labName);
            return assignedTeams;
        }
    }

    public ArrayList<String> getAssignedLabUsers(String labName) {
        log.debug("[getAssignedLabUsers] called");
        Lab lab = labRepo.findByLabName(labName);
        if (Objects.isNull(lab)) {
            log.warn("[getAssignedLabUsers] Lab with name {} not found in database", labName);
            throw new NotFoundException("Lab with name " + labName + " not found in database");
        } else {
            log.info("[getAssignedLabUsers] All users assigned to lab with name {} are listed", labName);
            ArrayList<String> assignedUsers = new ArrayList<>();
            for (User user : userRepo.findAll()) {
                if (user.getLabs().contains(lab)) {
                    assignedUsers.add(user.getUsername());
                }
            }
            return assignedUsers;
        }
    }

    public ArrayList<String> getAssignedTeamLabs(String teamName) {
        log.debug("[getAssignedTeamLabs] called");
        Team team = teamRepo.findByName(teamName);
        if (Objects.isNull(team)) {
            log.warn("[getAssignedTeamLabs] Team with name {} not found in database", teamName);
            throw new NotFoundException("Team with name " + teamName + " not found in database");
        } else {
            log.info("[getAssignedTeamLabs] All labs assigned to team with name {} are listed", teamName);
            ArrayList<String> assignedLabs = new ArrayList<>();
            for (Lab lab : labRepo.findAll()) {
                if (lab.getTeams().contains(team)) {
                    assignedLabs.add(lab.getLabName());
                }
            }
            return assignedLabs;
        }
    }

    public ArrayList<String> getAssignedUserLabs(String username) {
        log.debug("[getAssignedUserLabs] called");
        User user = userRepo.findByUsername(username);
        if (Objects.isNull(user)) {
            log.warn("[getAssignedUserLabs] User with username {} not found in database", username);
            throw new NotFoundException("User with username " + username + " not found in database");
        } else {
            log.info("[getAssignedUserLabs] All labs assigned to user with username {} are listed", username);
            ArrayList<String> assignedLabs = new ArrayList<>();
            for (Lab lab : labRepo.findAll()) {
                if (lab.getUsers().contains(user)) {
                    assignedLabs.add(lab.getLabName());
                }
            }
            return assignedLabs;
        }
    }
}