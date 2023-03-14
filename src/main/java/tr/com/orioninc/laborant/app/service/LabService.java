package tr.com.orioninc.laborant.app.service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.app.repository.TeamRepository;
import tr.com.orioninc.laborant.app.repository.UserRepository;
import tr.com.orioninc.laborant.exception.custom.AlreadyExistsException;
import tr.com.orioninc.laborant.exception.custom.NotConnectedException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@EnableScheduling
@EnableAsync
public class LabService {

    @Autowired
    LabRepository labRepo;
    @Autowired
    TeamRepository teamRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    AsyncService asyncService;
    @Autowired
    UserService userService;

    public Lab unreserveLab(String labName) {
        log.debug("[unreserveLab] called");
        if (Objects.equals(labName, "") || labName == null) {
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        Lab labToBeUnreserved = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeUnreserved)) {
            log.warn("[unreserveLab] no lab in the database named: {}", labName);
            throw new NotFoundException("There isn't a lab named " + labName + " found in the database to be unreserved");
        } else {
            if (labToBeUnreserved.getReserved() == null) {
                labToBeUnreserved.setReserved(false);
                labToBeUnreserved.setReservedBy(null);
                labToBeUnreserved.setReservedUntil(null);
                labRepo.save(labToBeUnreserved);
                throw new AlreadyExistsException("Lab with name " + labName + " already not reserved");
            } else {
                if (labToBeUnreserved.getReserved()) {
                    labToBeUnreserved.setReservedBy(null);
                    labToBeUnreserved.setReservedUntil(null);
                    labToBeUnreserved.setReserved(false);
                    labRepo.save(labToBeUnreserved);
                    log.info("[unreserveLab] lab with name {} is unreserved", labName);
                    asyncService.sendMailToWaiters(labToBeUnreserved);
                    return labToBeUnreserved;
                } else {
                    log.warn("[unreserveLab] lab with name {} is already unreserved", labName);
                    throw new AlreadyExistsException("Lab with name " + labName + " is already unreserved");
                }
            }
        }
    }

    public Lab findLabByName(String labName) {
        if (Objects.equals(labName, "") || labName == null) {
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        log.debug("[findLabByName] called");
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
                    if (lab.getTeamName() != null && !lab.getTeamName().isEmpty()) {
                        lab = labRepo.save(lab);
                        if (teamRepo.findByName(lab.getTeamName()) == null) {
                            Team team = new Team();
                            team.setName(lab.getTeamName());
                            teamRepo.save(team);
                        }
                        teamRepo.findByName(lab.getTeamName()).getLabs().add(lab);
                        teamRepo.save(teamRepo.findByName(lab.getTeamName()));
                    }
                    log.info("[addNewLab] User: {}", lab.getUserEmail());
                    if (lab.getUserEmail() != null && !lab.getUserEmail().isEmpty()) {
                        lab = labRepo.save(lab);
                        if (userRepo.findByEmail(lab.getUserEmail()) == null) {
                            User user = new User();
                            userService.addNewUserWithJustEmail(user);
                        }
                        userRepo.findByEmail(lab.getUserEmail()).getLabs().add(lab);
                        userRepo.save(userRepo.findByEmail(lab.getUserEmail()));
                    }

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
        if (Objects.equals(labName, "") || labName == null) {
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
        if (Objects.equals(labName, "") || labName == null) {
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)) {
            log.warn("[deleteLabByName] no lab in the database named: {}", labName);
            throw new NotFoundException("There isn't a lab named " + labName + " found in the database to be deleted");
        } else {
            if (!userRepo.findAll().isEmpty()) {
                if (!labToBeDeleted.getUsers().isEmpty()) {
                    for (User user : labToBeDeleted.getUsers()) {
                        user.getLabs().remove(labToBeDeleted);
                        userRepo.save(user);
                        log.info("[deleteLabByName] removing lab named: {} from user named: {}", labToBeDeleted.getLabName(), user.getUsername());
                    }
                }
            }
        }
        if (!teamRepo.findAll().isEmpty()) {
            if (!labToBeDeleted.getTeams().isEmpty()) {
                for (Team team : labToBeDeleted.getTeams()) {
                    team.getLabs().remove(labToBeDeleted);
                    teamRepo.save(team);
                    log.info("[deleteLabByName] removing lab named: {} from team named: {}", labToBeDeleted.getLabName(), team.getName());
                }
            }
        }
        labRepo.delete(labToBeDeleted);
        log.info("[deleteLabByName] deleting new lab named: {}", labToBeDeleted.getLabName());
        return labToBeDeleted;

    }

    public Lab reserveLab(String labName, String username, Date date) {
        log.debug("[reserveLab] called");
        if (Objects.equals(labName, "") || labName == null) {
            throw new IllegalArgumentException("Lab name cannot be empty");
        }
        User reservedToUser = userRepo.findByUsername(username);
        Lab labToBeReserved = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeReserved)) {
            log.warn("[reserveLab] no lab in the database named: {}", labName);
            throw new NotFoundException("There isn't a lab named " + labName + " found in the database to be reserved");
        } else {
            if (Objects.isNull(reservedToUser)) {
                log.warn("[reserveLab] no user in the database named: {}", username);
                throw new NotFoundException("There isn't a user named " + username + " found in the database to reserve the lab");
            } else {
                if (labToBeReserved.getReserved() == null) {
                    labToBeReserved.setReserved(true);
                    labToBeReserved.setReservedBy(reservedToUser);
                    labToBeReserved.setReservedUntil(date);
                    labRepo.save(labToBeReserved);
                    log.info("[reserveLab] lab with name {} is reserved", labName);
                    return labToBeReserved;
                } else {
                    if (labToBeReserved.getReserved()) {
                        log.warn("[reserveLab] lab with name {} is already reserved", labName);
                        throw new AlreadyExistsException("Lab with name " + labName + " is already reserved");
                    } else {
                        labToBeReserved.setReserved(true);
                        labToBeReserved.setReservedBy(reservedToUser);
                        labToBeReserved.setReservedUntil(date);
                        labRepo.save(labToBeReserved);
                        log.info("[reserveLab] lab with name {} is reserved", labName);
                        return labToBeReserved;
                    }
                }
            }
        }
    }

    public List<Lab> getAllLabs() {
        if (labRepo.findAll().isEmpty()) {
            log.warn("[getAllLabs] No lab found in database");
            throw new NotFoundException("There is no lab in database");
        } else {
            log.info("[getAllLabs] All labs are listed");
            return labRepo.findAll();
        }
    }


    public String connectAndExecuteCommand(String username, String password,
                                           String host, int port, String command) throws InterruptedException {

        log.debug("[connectAndExecuteCommand] called");

        Session session = null;
        ChannelExec channel = null;
        String responseString = null;
            try {
                session = new JSch().getSession(username, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setServerAliveInterval(500); // Check if server is alive every 200  miliseconds
                session.setServerAliveCountMax(1); // If server is not alive, try to reconnect once
                session.connect();
                if (session.isConnected()) {
                    log.info("[connectAndExecuteCommand] session connected for lab {}", host);

                } else {
                    log.info("[connectAndExecuteCommand] session not connected for lab {}", host);
                }
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                log.info("[connectAndExecuteCommand] command to be executed: {} for lab {}", command, host);
                ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                channel.setOutputStream(responseStream);
                channel.connect();

                while (channel.isConnected()) {
                    Thread.sleep(100);
                }

                responseString = responseStream.toString();
            } catch (JSchException e) {
                throw new NotConnectedException(host + " is not reachable.");
            } finally {
                if (session != null) {
                    session.disconnect();
                }
                if (channel != null) {
                    channel.disconnect();
                }
            }
        log.info("For lab {} response is: {}", host, responseString);
        return responseString;
    }

    public String runCommandOnSelectedLab(String labName, String command) {
        String outputString = null;
        log.debug("[runStatusOnSelectedLab] called");
        Lab labToExecute = findLabByName(labName);
        if (Objects.isNull(labToExecute)) {
            log.info("[runStatusOnSelectedLab] lab couldn't found in database");
            throw new NotFoundException("Lab couldn't found in database");
        }
        try {
            outputString = connectAndExecuteCommand(labToExecute.getUserName(), labToExecute.getPassword(),
                    labToExecute.getHost(), labToExecute.getPort(), command);
            log.info("[runStatusOnSelectedLab] out string: {}", outputString);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotConnectedException("Couldn't connect to the lab");
        }
        return outputString;
    }

    public String registerUserToWaitingList(String labName, String username) {
        log.debug("[registerUserToWaitingList] called");
        Lab labToRegister = findLabByName(labName);
        if (Objects.isNull(labToRegister)) {
            log.info("[registerUserToWaitingList] lab couldn't found in database");
            throw new NotFoundException("Lab couldn't found in database");
        }
        User userToRegister = userRepo.findByUsername(username);
        if (Objects.isNull(userToRegister)) {
            log.info("[registerUserToWaitingList] user couldn't found in database");
            throw new NotFoundException("User couldn't found in database");
        }
        List<User> awaitingUsers = labToRegister.getMailAwaitingUsers();
        if (awaitingUsers.contains(userToRegister)) {
            log.info("[registerUserToWaitingList] user is already in the waiting list");
            throw new AlreadyExistsException("User is already in the waiting list");
        } else {
            awaitingUsers.add(userToRegister);
            labToRegister.setMailAwaitingUsers(awaitingUsers);
            labRepo.save(labToRegister);
            log.info("[registerUserToWaitingList] user is added to the waiting list");
            return "User is added to the waiting list";
        }
    }

    public String unregisterUserFromWaitingList(String labName, String username) {
        log.debug("[unregisterUserFromWaitingList] called");
        Lab labToUnregister = findLabByName(labName);
        if (Objects.isNull(labToUnregister)) {
            log.info("[unregisterUserFromWaitingList] lab couldn't found in database");
            throw new NotFoundException("Lab couldn't found in database");
        }
        User userToUnregister = userRepo.findByUsername(username);
        if (Objects.isNull(userToUnregister)) {
            log.info("[unregisterUserFromWaitingList] user couldn't found in database");
            throw new NotFoundException("User couldn't found in database");
        }
        List<User> awaitingUsers = labToUnregister.getMailAwaitingUsers();
        if (awaitingUsers.contains(userToUnregister)) {
            awaitingUsers.remove(userToUnregister);
            labToUnregister.setMailAwaitingUsers(awaitingUsers);
            labRepo.save(labToUnregister);
            log.info("[unregisterUserFromWaitingList] user is removed from the waiting list");
            return "User is removed from the waiting list";
        } else {
            log.info("[unregisterUserFromWaitingList] user is not in the waiting list");
            throw new NotFoundException("User is not in the waiting list");
        }
    }

    @Scheduled(fixedRate = 60000) // 1 minute
    public void checkLabs() {
        log.info("[checkLabs] called");
        List<Lab> labs = labRepo.findAll();
        Date now = new Date();

        for (Lab lab : labs) {
            if (lab.getReserved()) {
                if (lab.getReservedUntil().before(now)) {
                    lab.setReserved(false);
                    lab.setReservedUntil(null);
                    lab.setReservedBy(null);
                    labRepo.save(lab);
                    asyncService.sendMailToWaiters(lab);
                }
            }
        }
    }

}

