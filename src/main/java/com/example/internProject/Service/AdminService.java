
package com.example.internProject.Service;

import com.example.internProject.Model.Lab;
import com.example.internProject.Repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Random;
@Service
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
                return "Successfully added the new lab named "+  labToBeAdded.getLabName() + " to the database";
            }
            else{
                return "There is already a pair in the database with username: "+userName + " and host: " + host ;
            }
        }
        else
        {
            return "There is already a lab named " + labName + " in the database. Try again";
        }


    }

    public List<Lab> getAllLabs(){
        List<Lab> allLabs = labRepo.findAll();
        return allLabs;

    }

    public String deleteLab(String labName){
        Lab labToBeDeleted = labRepo.findByLabName(labName);
        if (Objects.isNull(labToBeDeleted)){
            return "There isn't a lab named "+labName+" found in the database to be deleted";
        }
        else {
            labRepo.delete(labToBeDeleted);

            return "Lab named "+labName+" is successfully deleted from the database";

        }
    }

    public Lab findLabByName(String labName){
        return labRepo.findByLabName(labName);
    }
}
