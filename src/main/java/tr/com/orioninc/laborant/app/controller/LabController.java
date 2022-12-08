package tr.com.orioninc.laborant.app.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import springfox.documentation.annotations.ApiIgnore;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.service.AdminService;
import tr.com.orioninc.laborant.app.service.LabService;
import tr.com.orioninc.laborant.app.model.CommandDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Controller
@Log4j2
@AllArgsConstructor
public class LabController {

    private LabService labService;
    private AdminService adminService;

    @GetMapping(value = { "/lab/getAllLabsStatus" })
    public String getAllLabs(Model model) {
        log.debug("[getAllLabs] @GetMapping /getAllLabsStatus method is called");
        String response = labService.getAllLabsStatus();
        List<List<String>> outputArray = new ArrayList<>();
        Scanner scanner = new Scanner(response);
        String currentLine = null;
        while (scanner.hasNextLine()) {
            List<String> words = new ArrayList<>();
            currentLine = scanner.nextLine();
            // StringTokenizer tokenizer = new StringTokenizer(currentLine);
            // while (tokenizer.hasMoreElements())
            //     words.add(tokenizer.nextToken());
            // TODO: Not tested, old impl above.
            String[] outputStringTokenized = currentLine.split(" ");
            Arrays.asList(outputStringTokenized).forEach(words::add);
            outputArray.add(words);
        }
        model.addAttribute("success", outputArray);
        log.debug("[getAllLabs] @GetMapping /getAllLabsStatus success.");
        scanner.close();
        return "response_Message";
    }

    @GetMapping(value = { "/lab/runCommand/{labName}/{userName}/{host}/{port}" })
    public String runCommandPage(Model model, @PathVariable String labName, @PathVariable String userName,
            @PathVariable String host, @PathVariable Integer port) {
        log.debug("[runCommandPage] @GetMapping /runCommand method is called");
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
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("[runCommandPage] @GetMapping exception: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", errorMessage);

            return "run_Command";
        }
    }

    @PostMapping(value = { "/lab/runCommand/{labName}" })
    public String runCommand(Model model, @PathVariable String labName,
            @ModelAttribute("currentCommand") CommandDTO currentCommand) {
        log.debug("[runCommand] @PostMapping /runCommand method is called");
        if (currentCommand.getCommand().isEmpty()) {
            log.info("[runCommand] Empty command.");
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
        Lab labFromDB = adminService.findLabByName(labName);
        Lab currentLab = new Lab();
        currentLab.setLabName(labName);
        currentLab.setUserName(labFromDB.getUserName());
        currentLab.setHost(labFromDB.getHost());
        currentLab.setPort(labFromDB.getPort());
        model.addAttribute("currentLab", currentLab);
        try {
            String commandResponse = labService.runCommandOnSelectedLab(labName, currentCommand.getCommand());
            log.info("[runCommand] Inside controller: {}", commandResponse);
            List<List<String>> outputArray = new ArrayList<>();
            Scanner scanner = new Scanner(commandResponse);
            String currentLine = null;

            while (scanner.hasNextLine()) {
                List<String> words = new ArrayList<>();
                currentLine = scanner.nextLine();

                if (" ".equals(currentLine.substring(0, 1))) {
                    words.add(" ");
                }
                // StringTokenizer tokenizer = new StringTokenizer(currentLine);
                // while (tokenizer.hasMoreElements())
                //     words.add(tokenizer.nextToken());
                // TODO: Not tested, old impl above.
                String[] outputStringTokenized = currentLine.split(" ");
                Arrays.asList(outputStringTokenized).forEach(words::add);
                outputArray.add(words);
            }

            log.info("[runCommand] Output array: {}", outputArray);
            model.addAttribute("success", outputArray);
            model.addAttribute("responseMessage", commandResponse);
            scanner.close();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("[runCommand] @PostMapping exception: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", errorMessage);
        }
        return "run_Command";

    }

}