
package tr.com.orioninc.laborant.service;

import lombok.AllArgsConstructor;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.repository.LabRepository;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
@AllArgsConstructor
public class AdminService {

    private LabRepository labRepo;

    public String addNewLab(String labName, String username, String password, String host, Integer port) {
        log.debug("[addNewLab] called");
        Lab searchLab = labRepo.findByLabName(labName);
        if (Objects.isNull(searchLab)) {
            Lab searchHostUserPair = labRepo.findByUserNameAndHost(username, host);
            if (Objects.isNull(searchHostUserPair)) {
                Lab labToBeAdded = new Lab(labName, username, password, host, port);
                labToBeAdded = labRepo.save(labToBeAdded);
                log.info("[addNewLab] adding new lab named: {}", labToBeAdded.getLabName());
                return "Successfully added the new lab named " + labToBeAdded.getLabName() + " to the database";
            } else {
                return "There is already a pair in the database with username: " + username + " and host: " + host;
            }
        } else {
            return "There is already a lab named " + labName + " in the database. Try again";
        }
    }

    public List<Lab> getAllLabs() {
        log.debug("[getAllALabs] called");
        return labRepo.findAll();
    }

    public String deleteLab(String labName) {
        log.debug("[deleteLab] called");
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)) {
            log.info("[deleteLab] no lab in the database named: {}", labName);
            return "There isn't a lab named " + labName + " found in the database to be deleted";
        } else {
            labRepo.delete(labToBeDeleted);
            log.info("[deleteLab] deleting new lab named: {}", labToBeDeleted.getLabName());
            return "Lab named " + labName + " is successfully deleted from the database";
        }
    }

    public Lab findLabByName(String labName) {
        log.debug("[findLabByName] called");
        return labRepo.findByLabName(labName);
    }

    // TODO REST IMPLEMENTATION
    // TODO REST IMPLEMENTATION
    public Lab updateLabByName(String labName, Lab lab) {
        log.debug("[updateLabByName] called");
        Lab labToBeUpdated = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeUpdated)) {
            log.info("[updateLabByName] no lab in the database named: {}", labName);
            return null;
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

    public Lab addNewLab(Lab lab) {
        log.debug("[addNewLab] called");
        Lab searchLab = labRepo.findByLabName(lab.getLabName());
        if (Objects.isNull(searchLab)) {
            Lab searchHostUserPair = labRepo.findByUserNameAndHost(lab.getUserName(), lab.getHost());
            if (Objects.isNull(searchHostUserPair)) {
                labRepo.save(lab);
                log.info("[addNewLab] adding new lab named: {}", lab.getLabName());
                return lab;
            } else {
                log.info("[addNewLab] there is already a pair in the database with username: {} and host: {}", lab.getUserName(), lab.getHost());
                return null;
            }
        } else {
            log.info("[addNewLab] there is already a lab named {} in the database. Try again", lab.getLabName());
            return null;
        }
    }


    public Lab getLab(String labName) {
        return labRepo.findByLabName(labName);
    }

    public boolean deleteLabByName(String labName) {
        log.debug("[deleteLab] called");
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)) {
            log.info("[deleteLab] no lab in the database named: {}", labName);
            return false;
        } else {
            labRepo.delete(labToBeDeleted);
            log.info("[deleteLab] deleting new lab named: {}", labToBeDeleted.getLabName());
            return true;
        }
    }
}
