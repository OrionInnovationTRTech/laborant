package com.example.internProject.Controller;

import com.example.internProject.Model.Lab;
import com.example.internProject.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @PostMapping("/admin/addNewLab")
    public String addNewLab(@RequestParam String labName,@RequestParam String userName,
    @RequestParam String password,@RequestParam String host,@RequestParam Integer port){
        return adminService.addNewLab(labName,userName,password,host,port);
    }

    @GetMapping("/admin/getAllLabs")
    public List<Lab> getAllLabs(){
        return adminService.getAllLabs();
    }

    @DeleteMapping("/admin/deleteLab")
    public String getAllLabs(@RequestParam String labNameToBeDeleted){
        return adminService.deleteLab(labNameToBeDeleted);
    }


}

