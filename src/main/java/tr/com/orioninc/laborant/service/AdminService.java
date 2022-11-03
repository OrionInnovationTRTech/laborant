
package tr.com.orioninc.laborant.service;

import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class AdminService {
    @Autowired
    private LabRepository labRepo;

    public String addNewLab(String labName, String userName, String password, String host, Integer port) {
        log.debug("[addNewLab] called");
        Lab searchLab = labRepo.findByLabName(labName);
        if (Objects.isNull(searchLab)) {
            Lab searchHostUserPair = labRepo.findByUserNameAndHost(userName, host);
            if (Objects.isNull(searchHostUserPair)) {
                Lab labToBeAdded = new Lab(labName, userName, password, host, port);
                labToBeAdded = labRepo.save(labToBeAdded);
                log.info("[addNewLab] adding new lab named: {}", labToBeAdded.getLabName());
                return "Successfully added the new lab named " + labToBeAdded.getLabName() + " to the database";
            } else {
                return "There is already a pair in the database with username: " + userName + " and host: " + host;
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
}
