package com.example.internProject.Controller;

import com.example.internProject.Service.LabService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;





@RestController
public class LabController
{
    @Autowired
    LabService labService;

    @GetMapping("/lab/getAllLabsStatus")
    public String getAllLabs(){
        return labService.getALlLabsStatus();
    }

    @GetMapping("/lab/runCommand")
    public String getAllLabs(@RequestParam String labName, @RequestParam String command)
    {
        return labService.runCommandOnSelectedLab(labName,command);
    }




}