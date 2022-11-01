package tr.com.orioninc.laborant.controller;


import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import tr.com.orioninc.laborant.model.CommandDTO;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.service.AdminService;
import tr.com.orioninc.laborant.service.LabService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Objects;



@Log4j2
@Controller
@AllArgsConstructor
public class LabController {
    LabService labService;

    AdminService adminService;

    @GetMapping(value = {"/lab/getAllLabsStatus"})
    public String getAllLabs(Model model) {
        String response = labService.getAllLabsStatus();
        List<List<String>> outputArray = new ArrayList<>();
        Scanner scanner = new Scanner(response);
        String currentLine = null;
        while (scanner.hasNextLine()) {
            List<String> words = new ArrayList<>();
            currentLine = scanner.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(currentLine);
            while (tokenizer.hasMoreElements())
                words.add(tokenizer.nextToken());
            outputArray.add(words);
        }
        model.addAttribute("success", outputArray);
        return "response_Message";
    }

    @GetMapping(value = {"/lab/runCommand/{labName}/{userName}/{host}/{port}"})
    public String runCommandPage(Model model, @PathVariable String labName, @PathVariable String userName, @PathVariable String host, @PathVariable Integer port) {
        try {
            Lab currentLab = new Lab();
            currentLab.setLabName(labName);
            currentLab.setUserName(userName);
            currentLab.setHost(host);
            currentLab.setPort(port);
            log.info("[runCommandPage] currentLab: {}", currentLab);
            model.addAttribute("currentLab", currentLab);
            CommandDTO currentCommand = new CommandDTO();
            log.info("[runCommandPage] currentCommand: {}", currentCommand);
            model.addAttribute("currentCommand", currentCommand);
            return "run_Command";
        } catch (Exception e) {
            log.error("[runCommandPage] Error while running command: " + e.getMessage());
            String errorMessage = e.getMessage();
            model.addAttribute("errorMessage", errorMessage);

            return "run_Command";
        }
    }

    @PostMapping(value = {"/lab/runCommand/{labName}"})
    public String runCommand(Model model, @PathVariable String labName,
                             @ModelAttribute("currentCommand") CommandDTO currentCommand) {
        log.info("[runCommand][LabController] Running command on lab named {}", labName);
        if (Objects.equals(currentCommand.getCommand(), "")) {
            Lab labFromDB = adminService.findLabByName(labName);
            Lab currentLab = new Lab();
            currentLab.setLabName(labName);
            currentLab.setUserName(labFromDB.getUserName());
            currentLab.setHost(labFromDB.getHost());
            currentLab.setPort(labFromDB.getPort());
            log.info("[runCommand][LabController] currentLab: {}", currentLab);
            log.warn("[runCommand][LabController] Command is empty");
            model.addAttribute("currentLab", currentLab);
            model.addAttribute("errorMessage", "Please enter a command");
            return "run_Command";
        } else {
            Lab labFromDB = adminService.findLabByName(labName);
            Lab currentLab = new Lab();
            currentLab.setLabName(labName);
            currentLab.setUserName(labFromDB.getUserName());
            currentLab.setHost(labFromDB.getHost());
            currentLab.setPort(labFromDB.getPort());
            log.info("[runCommand][LabController] currentLab: {}", currentLab);
            model.addAttribute("currentLab", currentLab);
            try {
                String commandResponse = labService.runCommandOnSelectedLab(labName, currentCommand.command);
                log.info("[runCommand][LabController] commandResponse: {}", commandResponse);
                List<List<String>> outputArray = new ArrayList<>();
                Scanner scanner = new Scanner(commandResponse);
                String currentLine = null;

                while (scanner.hasNextLine()) {
                    List<String> words = new ArrayList<>();
                    currentLine = scanner.nextLine();

                    if (currentLine.charAt(0) == ' ') {
                        words.add(" ");
                    }
                    StringTokenizer tokenizer = new StringTokenizer(currentLine);
                    while (tokenizer.hasMoreElements())
                        words.add(tokenizer.nextToken());
                    outputArray.add(words);
                }

                log.info("[runCommand][LabController] Success {}", outputArray);
                model.addAttribute("success", outputArray);
                log.info("[runCommand][LabController] Response message: {}", commandResponse);
                model.addAttribute("responseMessage", commandResponse);
                return "run_Command";
            } catch (Exception e) {
                log.error("[runCommand][LabController] Error while running command on lab named {}", labName);
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