
package tr.com.orioninc.laborant.service;

import lombok.extern.log4j.Log4j2;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class AdminService
{
    @Autowired
    private LabRepository labRepo;

    public  String addNewLab(String labName, String userName, String password, String host, Integer port)
    {
        Lab searchLab = labRepo.findByLabName(labName);
        if (Objects.isNull(searchLab))
        {
            Lab searchHostUserPair = labRepo.findByUserNameAndHost(userName, host) ;
            if (Objects.isNull(searchHostUserPair)){
                Lab labToBeAdded = new Lab(labName,userName,password,host,port);
                labToBeAdded = labRepo.save(labToBeAdded);
                log.info("[addNewLab] Successfully added new lab with name: {}", labToBeAdded.getLabName());
                return "Successfully added the new lab named "+  labToBeAdded.getLabName() + " to the database";
            }
            else{
                log.warn("[addNewLab] The lab named {} already exists in the database", labName);
                return "There is already a pair in the database with username: "+userName + " and host: " + host ;
            }
        }
        else
        {

            log.warn("[addNewLab] There is already a lab named {} in the database. Try again", labName);
            return "There is already a lab named " + labName + " in the database. Try again";
        }


    }

    public List<Lab> getAllLabs(){
        List<Lab> allLabs = labRepo.findAll();
        log.info("[getAllLabs] - Getting all labs from the database... \n {}", allLabs);
        return allLabs;
    }

    public String deleteLab(String labName){
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)){
            log.warn("[deleteLab] There is no lab named {} in the database to be deleted", labName);
            return "There isn't a lab named "+labName+" found in the database to be deleted";
        }
        else {
            labRepo.delete(labToBeDeleted);
            log.info("[deleteLab] Successfully deleted lab named {}", labName);
            return "Lab named "+labName+" is successfully deleted from the database";

        }
    }

    public Lab findLabByName(String labName){
        log.info("[findLabByName] Lab found: {}",labName);
        return labRepo.findByLabName(labName);
    }
}
