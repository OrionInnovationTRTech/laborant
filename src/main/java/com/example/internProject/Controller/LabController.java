package com.example.internProject.Controller;


import com.example.internProject.Model.CommandDTO;
import com.example.internProject.Model.Lab;
import com.example.internProject.Service.AdminService;
import com.example.internProject.Service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;


@Controller
public class LabController
{
    @Autowired
    LabService labService;

    @Autowired
    AdminService adminService;

    @GetMapping(value = {"/lab/getAllLabsStatus"})
    public String getAllLabs(Model model){
        String response = labService.getALlLabsStatus();
        List<List<String>> outputArray = new ArrayList<>();
        Scanner scanner = new Scanner(response);
        String currentLine = null;
        while (scanner.hasNextLine())
        {
            List<String> words = new ArrayList<>();
            currentLine = scanner.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(currentLine);
            while (tokenizer.hasMoreElements())
                words.add(tokenizer.nextToken());
            outputArray.add(words);
        }
        model.addAttribute("success",outputArray);
        return "response_Message";
    }

    @GetMapping(value = {"/lab/runCommand/{labName}/{userName}/{host}/{port}"})
    public String runCommandPage(Model model,@PathVariable String labName,@PathVariable String userName,@PathVariable String host,@PathVariable Integer port)
    {
        try {
            Lab currentLab = new Lab();
            currentLab.setLabName(labName);
            currentLab.setUserName(userName);
            currentLab.setHost(host);
            currentLab.setPort(port);
            model.addAttribute("currentLab", currentLab);
            CommandDTO currentCommand = new CommandDTO();
            model.addAttribute("currentCommand", currentCommand);
            return "run_Command";
        }
        catch (Exception e){
            String errorMessage = e.getMessage();
            model.addAttribute("errorMessage", errorMessage);

            return "run_Command";
        }
    }

    @PostMapping(value = {"/lab/runCommand/{labName}"})
    public String runCommand(Model model, @PathVariable String labName,
                                 @ModelAttribute("currentCommand") CommandDTO currentCommand)
    {
        if (currentCommand.getCommand() == "") {
            System.out.println("EMPTY COMMAND");
            Lab labFromDB = adminService.findLabByName(labName);
            Lab currentLab = new Lab();
            currentLab.setLabName(labName);
            currentLab.setUserName(labFromDB.getUserName());
            currentLab.setHost(labFromDB.getHost());
            currentLab.setPort(labFromDB.getPort());
            model.addAttribute("currentLab", currentLab);
            model.addAttribute("errorMessage", "Please enter a command");
            return "run_Command";
        }
        else {
            Lab labFromDB = adminService.findLabByName(labName);
            Lab currentLab = new Lab();
            currentLab.setLabName(labName);
            currentLab.setUserName(labFromDB.getUserName());
            currentLab.setHost(labFromDB.getHost());
            currentLab.setPort(labFromDB.getPort());
            model.addAttribute("currentLab", currentLab);
            try {
                String commandResponse = labService.runCommandOnSelectedLab(labName, currentCommand.command);
                System.out.println("INSIDE CONTROLLER" + commandResponse);
                List<List<String>> outputArray = new ArrayList<>();
                Scanner scanner = new Scanner(commandResponse);
                String currentLine = null;

                while (scanner.hasNextLine())
                {
                    List<String> words = new ArrayList<>();
                    currentLine = scanner.nextLine();

                    if (currentLine.substring(0,1) == " "){
                        words.add(" ");
                    }
                    StringTokenizer tokenizer = new StringTokenizer(currentLine);
                    while (tokenizer.hasMoreElements())
                        words.add(tokenizer.nextToken());
                    outputArray.add(words);
                }

                model.addAttribute("success", outputArray);
                model.addAttribute("responseMessage", commandResponse);
                return "run_Command";
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                model.addAttribute("errorMessage", errorMessage);

                return "run_Command";
            }
        }
    }
    
}

/*
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
 */