
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
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class AdminService {

    private LabRepository labRepo;
    private UserRepository userRepo;
    private TeamRepository teamRepo;

    public List<Lab> getAllLabs() {
        if (labRepo.findAll().isEmpty()) {
            log.warn("[getAllLabs] No lab found in database");
            throw new NotFoundException("There is no lab in database");
        } else {
            log.info("[getAllLabs] All labs are listed");
            return labRepo.findAll();
        }
    }

    public String assignLabToTeam(String labName, String teamName) {
        log.debug("[assignLabToTeam] called");
        Lab lab = labRepo.findByLabName(labName);
        Team team = teamRepo.findByName(teamName);
        if(Objects.equals(labName,"") || labName == null){
            log.error("[assignLabToTeam] Lab name is empty");
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if(Objects.equals(teamName,"") || teamName == null){
            log.error("[assignLabToTeam] Team name is empty");
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        if (Objects.isNull(lab)) {
            log.error("[assignLabToTeam] Lab not found");
            throw new NotFoundException("Lab not found");
        }
        else if (Objects.isNull(team)) {
            log.error("[assignLabToTeam] Team not found");
            throw new NotFoundException("Team not found");
        }
        else if (team.getLabs().contains(lab)) {
            log.error("[assignLabToTeam] Lab {} already assigned to team {}", labName, teamName);
            throw new AlreadyExistsException("Lab already assigned to team");
        }
        else {
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
        if(Objects.equals(labName,"") || labName == null){
            log.error("[unassignLabFromTeam] Lab name is empty");
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if(Objects.equals(teamName,"") || teamName == null){
            log.error("[unassignLabFromTeam] Team name is empty");
            throw new IllegalArgumentException("Team name cannot be empty");
        }
        if (Objects.isNull(lab)) {
            log.error("[unassignLabFromTeam] Lab not found");
            throw new NotFoundException("Lab not found");
        }
        else if (Objects.isNull(team)) {
            log.error("[unassignLabFromTeam] Team not found");
            throw new NotFoundException("Team not found");
        }
        else if (!team.getLabs().contains(lab)) {
            log.error("[unassignLabFromTeam] Lab {} not assigned to team {}", labName, teamName);
            throw new NotFoundException("Lab not assigned to team");
        }
        else {
            team.getLabs().remove(lab);
            teamRepo.save(team);
            log.info("[unassignLabFromTeam] Lab unasigned from team");
            return "Lab unassigned from team";
        }
    }

    public String assignLabToUser(String userName, String labName) {
        log.debug("[assignUserToLab] called");
        User user = userRepo.findByUsername(userName);
        Lab lab = labRepo.findByLabName(labName);
        if (Objects.equals(userName, "") || userName == null){
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if (Objects.isNull(user)) {
            log.warn("[assignUserToLab] User with username {} not found in database", userName);
            throw new NotFoundException("User with username " + userName + " not found in database");
        } else if (Objects.isNull(lab)) {
            log.warn("[assignUserToLab] Lab with name {} not found in database", labName);
            throw new NotFoundException("Lab with name " + labName + " not found in database");
        } else {
            if (user.getLabs().contains(lab)) {
                log.warn("[assignUserToLab] User with username {} already assigned to lab with name {}", userName, labName);
                throw new AlreadyExistsException(userName + " already assigned to lab " + labName);
            } else {
                for (User u : userRepo.findAll()) {
                    if (u.getLabs().contains(lab)) {
                        log.warn("[assignUserToLab] Lab with name {} already assigned to user with username {}", labName, u.getUsername());
                        throw new AlreadyExistsException("Lab with name " + labName + " already assigned to user with username " + u.getUsername());
                    }
                }
                user.getLabs().add(lab);
                userRepo.save(user);
                log.info("[assignUserToLab] User with username {} assigned to lab with name {}", userName, labName);
                return "User with username " + userName + " assigned to " + labName;
            }
        }
    }

    public String unassignLabFromUser(String userName, String labName) {
        log.debug("[unassignUserFromLab] called");
        User user = userRepo.findByUsername(userName);
        Lab lab = labRepo.findByLabName(labName);
        if (Objects.equals(userName, "") || userName == null){
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if (Objects.isNull(user)) {
            log.warn("[unassignUserFromLab] User with username {} not found in database", userName);
            throw new NotFoundException("User with username " + userName + " not found in database");
        } else if (Objects.isNull(lab)) {
            log.warn("[unassignUserFromLab] Lab with name {} not found in database", labName);
            throw new NotFoundException("Lab with name " + labName + " not found in database");
        } else {
            if (user.getLabs().contains(lab)) {
                user.getLabs().remove(lab);
                userRepo.save(user);
                log.info("[unassignUserFromLab] User with username {} unassigned from lab with name {}", userName, labName);
                return "User with username " + userName + " unassigned from lab with name " + labName;
            } else {
                log.warn("[unassignUserFromLab] User with username {} not assigned to lab with name {}", userName, labName);
                throw new NotFoundException("User with username " + userName + " not assigned to lab with name " + labName);
            }
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

    public Lab findLabByName(String labName) {
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        log.info("[findLabByName] called");
        return labRepo.findByLabName(labName);
    }

    public Lab updateLabByName(String labName, Lab lab) {
        log.debug("[updateLabByName] called");
        Lab labToBeUpdated = findLabByName(labName);
        if (lab.getLabName() == null || lab.getLabName().isEmpty() || lab.getUserName() == null || lab.getUserName().isEmpty() || lab.getPassword() == null || lab.getPassword().isEmpty() || lab.getHost() == null || lab.getHost().isEmpty() || lab.getPort() == null) {
            throw new IllegalArgumentException("Lab credentials cannot be empty");
        } else {
            if (Objects.isNull(labToBeUpdated)) {
                log.warn("[updateLabByName] no lab in the database named: {}", labName);
                throw new NotFoundException("There isn't a lab named " + labName + " found in the database to be updated");
            } else {
                labToBeUpdated.setUserName(lab.getUserName());
                labToBeUpdated.setPassword(lab.getPassword());
                labToBeUpdated.setHost(lab.getHost());
                labToBeUpdated.setPort(lab.getPort());
                labRepo.save(labToBeUpdated);
                log.info("[updateLabByName] updating lab named: {} to credentials: {}",
                        labToBeUpdated.getLabName(), labToBeUpdated.toString());
                return labToBeUpdated;
            }
        }
    }

    public Lab addNewLab(Lab lab) {
        log.debug("[addNewLab] called");
        Lab searchLab = findLabByName(lab.getLabName());
        if (lab.getLabName() == null || lab.getLabName().isEmpty() || lab.getUserName() == null || lab.getUserName().isEmpty() || lab.getPassword() == null || lab.getPassword().isEmpty() || lab.getHost() == null || lab.getHost().isEmpty() || lab.getPort() == null) {
            throw new IllegalArgumentException("Lab credentials cannot be empty");
        } else {

            if (Objects.isNull(searchLab)) {
                Lab searchHostUserPair = labRepo.findByUserNameAndHost(lab.getUserName(), lab.getHost());
                if (Objects.isNull(searchHostUserPair)) {
                        lab = labRepo.save(lab);
                        log.info("[addNewLab] adding new lab named: {}", lab.getLabName());
                        return lab;
                } else {
                    log.warn("[addNewLab] there is already a pair in the database with username: {} and host: {}", lab.getUserName(), lab.getHost());
                    throw new AlreadyExistsException("There is already a pair in the database with username: " + lab.getUserName() + " and host: " + lab.getHost());
                }
            } else {
                log.warn("[addNewLab] there is already a lab named {} in the database. Try again", lab.getLabName());
                throw new AlreadyExistsException("There is already a lab named " + lab.getLabName() + " in the database. Try again");
            }
        }
    }

    public Lab getLab(String labName) {
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        if (findLabByName(labName) == null) {
            log.warn("[getLab] No lab found in database named {}", labName);
            throw new NotFoundException("There is no lab in database named " + labName);
        } else {
            log.info("[getLab] Lab is listed");
            return labRepo.findByLabName(labName);
        }
    }

    public Lab deleteLabByName(String labName) {
        log.debug("[deleteLabByName] called");
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)) {
            log.warn("[deleteLabByName] no lab in the database named: {}", labName);
            throw new NotFoundException("There isn't a lab named " + labName + " found in the database to be deleted");
        } else {
            if (!userRepo.findAll().isEmpty()) {
                for (User user : userRepo.findAll()) {
                    if (user.getLabs().contains(labToBeDeleted)) {
                        user.getLabs().remove(labToBeDeleted);
                        userRepo.save(user);
                        log.info("[deleteLabByName] User with username {} unassigned from lab with name {}", user.getUsername(), labName);
                    }
                }
            }
            labRepo.delete(labToBeDeleted);
            log.info("[deleteLabByName] deleting new lab named: {}", labToBeDeleted.getLabName());
            return labToBeDeleted;
        }
    }
    public boolean isLabReachable (String host,int timeout) {
        log.info("[isLabReachable] checking if lab with host {} is reachable", host);
        try {
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    log.info("[isLabReachable] lab is reachable");
                    return true;
                }
                else {
                    log.info("[isLabReachable] lab is not reachable");
                    return false;
                }
            } catch (IOException e) {
                log.error("[isLabReachable] lab is not reachable");
                return false;
            }
        }


    public Lab reserveLab(String labName) {
        log.debug("[reserveLab] called");
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        Lab labToBeReserved = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeReserved)) {
            log.warn("[reserveLab] no lab in the database named: {}", labName);
            throw new NotFoundException("There isn't a lab named " + labName + " found in the database to be reserved");
        } else {
            if (labToBeReserved.getReserved() == null){
                labToBeReserved.setReserved(true);
                labRepo.save(labToBeReserved);
                log.info("[reserveLab] lab with name {} is reserved", labName);
                return labToBeReserved;
            }
            else {
                if (labToBeReserved.getReserved()) {
                    log.warn("[reserveLab] lab with name {} is already reserved", labName);
                    throw new AlreadyExistsException("Lab with name " + labName + " is already reserved");
                } else {
                    labToBeReserved.setReserved(true);
                    labRepo.save(labToBeReserved);
                    log.info("[reserveLab] lab with name {} is reserved", labName);
                    return labToBeReserved;
                }
            }
        }
    }
    public Lab unreserveLab (String labName){
        log.debug("[unreserveLab] called");
        if (Objects.equals(labName, "") || labName == null){
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        Lab labToBeUnreserved = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeUnreserved)) {
            log.warn("[unreserveLab] no lab in the database named: {}", labName);
            throw new NotFoundException("There isn't a lab named " + labName + " found in the database to be unreserved");
        } else {
            if (labToBeUnreserved.getReserved() == null){
                labToBeUnreserved.setReserved(false);
                labRepo.save(labToBeUnreserved);
                throw new AlreadyExistsException("Lab with name " + labName + " is not reserved already");
            }
            else {
                if (labToBeUnreserved.getReserved()) {
                    labToBeUnreserved.setReserved(false);
                    labRepo.save(labToBeUnreserved);
                    log.info("[unreserveLab] lab with name {} is unreserved", labName);
                    return labToBeUnreserved;
                } else {
                    log.warn("[unreserveLab] lab with name {} is already unreserved", labName);
                    throw new AlreadyExistsException("Lab with name " + labName + " is already unreserved");
                }
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
            log.info("[getAssignedLabTeams] returning assigned teams");
            return assignedTeams;
        }
    }
}
