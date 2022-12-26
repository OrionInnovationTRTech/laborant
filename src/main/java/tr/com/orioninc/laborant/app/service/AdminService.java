
package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.repository.LabRepository;
import tr.com.orioninc.laborant.exception.AlreadyExists;
import tr.com.orioninc.laborant.exception.NotConnected;
import tr.com.orioninc.laborant.exception.NotFound;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class AdminService {

    private LabRepository labRepo;

    public String addNewLab(String labName, String username, String password, String host, Integer port) throws IOException {
        log.debug("[addNewLab] called");
        Lab searchLab = labRepo.findByLabName(labName);
        if (labName == null || labName.isEmpty() || username == null || username.isEmpty() || password == null || password.isEmpty() || host == null || host.isEmpty() || port == null) {
            throw new IllegalArgumentException("Lab credentials cannot be empty");
        } else {
            if (Objects.isNull(searchLab)) {
                Lab searchHostUserPair = labRepo.findByUserNameAndHost(username, host);
                if (Objects.isNull(searchHostUserPair)) {
                    Lab labToBeAdded = new Lab(labName, username, password, host, port);
                    if (isLabReachable(labToBeAdded.getHost(), 500)) {
                        labToBeAdded = labRepo.save(labToBeAdded);
                        log.info("[addNewLab] added new lab named: {}", labToBeAdded.getLabName());
                        return "Successfully added the new lab named " + labToBeAdded.getLabName() + " to the database";
                    }
                } else {
                    log.warn("[addNewLab] Lab with username {} and host {} already exists in database", username, host);
                    return "There is already a pair in the database with username: " + username + " and host: " + host;
                }
            } else {
                log.warn("[addNewLab] Lab with name {} already exists in database", labName);
                return "There is already a lab named " + labName + " in the database. Try again";
            }
            return "Lab could not be added to the database";
        }
    }

    public List<Lab> getAllLabs() {
        if (labRepo.findAll().isEmpty()) {
            log.warn("[getAllLabs] No lab found in database");
            throw new NotFound("There is no lab in database");
        } else {
            log.info("[getAllLabs] All labs are listed");
            return labRepo.findAll();
        }
    }

    public String deleteLab(String labName) {
        log.debug("[deleteLab] called");
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)) {
            log.warn("[deleteLab] no lab in the database named: {}", labName);
            return "There isn't a lab named " + labName + " found in the database to be deleted";
        } else {
            labRepo.delete(labToBeDeleted);
            log.info("[deleteLab] deleting new lab named: {}", labToBeDeleted.getLabName());
            return "Lab named " + labName + " is successfully deleted from the database";
        }
    }

    public Lab findLabByName(String labName) {
        log.info("[findLabByName] called");
        return labRepo.findByLabName(labName);
    }

    // REST IMPLEMENTATION
    public Lab updateLabByName(String labName, Lab lab) {
        log.debug("[updateLabByName] called");
        Lab labToBeUpdated = findLabByName(labName);
        if (lab.getLabName() == null || lab.getLabName().isEmpty() || lab.getUserName() == null || lab.getUserName().isEmpty() || lab.getPassword() == null || lab.getPassword().isEmpty() || lab.getHost() == null || lab.getHost().isEmpty() || lab.getPort() == null) {
            throw new IllegalArgumentException("Lab credentials cannot be empty");
        } else {
            if (Objects.isNull(labToBeUpdated)) {
                log.warn("[updateLabByName] no lab in the database named: {}", labName);
                throw new NotFound("There isn't a lab named " + labName + " found in the database to be updated");
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
                    if (isLabReachable(lab.getHost(), 500)) {
                        lab = labRepo.save(lab);
                        log.info("[addNewLab] adding new lab named: {}", lab.getLabName());
                        return lab;
                    } else {
                        log.error("[addNewLab] Cannot add lab. Lab is not reachable");
                        throw new NotConnected("Cannot add lab. The lab is not reachable");
                    }
                } else {
                    log.warn("[addNewLab] there is already a pair in the database with username: {} and host: {}", lab.getUserName(), lab.getHost());
                    throw new AlreadyExists("There is already a pair in the database with username: " + lab.getUserName() + " and host: " + lab.getHost());
                }
            } else {
                log.warn("[addNewLab] there is already a lab named {} in the database. Try again", lab.getLabName());
                throw new AlreadyExists("There is already a lab named " + lab.getLabName() + " in the database. Try again");
            }
        }
    }

    public Lab getLab(String labName) {
        if (findLabByName(labName) == null) {
            log.warn("[getLab] No lab found in database named {}", labName);
            throw new NotFound("There is no lab in database named " + labName);
        } else {
            log.info("[getLab] Lab is listed");
            return labRepo.findByLabName(labName);
        }
    }

    public Lab deleteLabByName(String labName) {
        log.debug("[deleteLab] called");
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)) {
            log.warn("[deleteLab] no lab in the database named: {}", labName);
            throw new NotFound("There isn't a lab named " + labName + " found in the database to be deleted");
        } else {
            labRepo.delete(labToBeDeleted);
            log.info("[deleteLab] deleting new lab named: {}", labToBeDeleted.getLabName());
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
}
