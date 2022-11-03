package tr.com.orioninc.laborant.service;

import tr.com.orioninc.laborant.model.Lab;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
@Log4j2
public class LabService {
    @Autowired
    AdminService adminService;

    public static String connectAndExecuteCommand(String username, String password,
            String host, int port, String command) throws InterruptedException {

        log.debug("[connectAndExecuteCommand] called");
        Session session = null;
        ChannelExec channel = null;
        String responseString = null;

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            log.debug("[connectAndExecuteCommand] command to be executed: {}", command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(500);
            }

            responseString = new String(responseStream.toByteArray());
        } catch (JSchException e) {
            log.error("[connectAndExecuteCommand] ssh exception: {}", e.getMessage(), e);
            return null;
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
        return responseString;
    }

    // TODO: What is the output? What does this method do??
    public String getAllLabsStatus() {
        log.debug("[getAllLabsStatus] called");
        List<Lab> allLabs = adminService.getAllLabs();
        if (allLabs.isEmpty()) {
            log.info("[getAllLabsStatus] no labs in the database");
            return "There aren't any labs in the database";
        }
        String response = "";
        String outputString = "";
        for (Lab currentLab : allLabs) {
            outputString += currentLab.getLabName() + "    " + "Host: " + currentLab.getHost() + "      ";
            try {
                response = connectAndExecuteCommand(currentLab.getUserName(),
                        currentLab.getPassword(), currentLab.getHost(), currentLab.getPort(),
                        "sudo wae-status");
            } catch (InterruptedException e) {
                log.error("[getAllLabsStatus] InterruptedException: {}", e.getMessage(), e);
                return "";
            }
            outputString = generateOutputString(outputString, response);
        }
        log.debug("[getAllLabsStatus] the output: {}", outputString);
        return outputString;
    }

    // TODO: What does this method do?? Refactor the method.
    private String generateOutputString(String outputString, String response) {
        log.debug("[generateOutputString] called");
        Scanner scanner = new Scanner(response);
        String currentLine = null;
        List<List<String>> outputArray = new ArrayList<>();
        while (scanner.hasNextLine()) {
            List<String> words = new ArrayList<>();
            currentLine = scanner.nextLine();
            log.debug("[generateOutputString] current line: {}", currentLine);
            // StringTokenizer tokenizer = new StringTokenizer(currentLine);
            // while (tokenizer.hasMoreElements())
            // words.add(tokenizer.nextToken());
            // TODO: Not tested, old impl above.
            String[] curLineTokenized = currentLine.split(" ");
            Arrays.asList(curLineTokenized).forEach(words::add);
            outputArray.add(words);
        }
        log.debug("[generateOutputString] current line: {}", currentLine);
        if (outputArray.get(1).get(9).equals("FAI")) {
            outputString += "SIGNAL 3";
        } else {
            boolean stopFound = false;
            boolean failFound = false;
            for (String value : outputArray.get(1)) {
                if (value.equals("STO")) {
                    stopFound = true;
                }
                if (value.equals("FAI")) {
                    failFound = true;
                }
            }
            if (failFound) {
                outputString += "SIGNAL 2";
            } else if (!failFound && stopFound) {
                outputString += "SIGNAL 1";
            } else {
                outputString += "SIGNAL 0";
            }
        }
        outputString += " \n";
        outputString += response;
        outputString += "\n  \n";
        scanner.close();
        return outputString;
    }

    public List<String> getAllLabVersions() {
        log.debug("[getAllLabVersions] called");
        List<String> labVersions = new ArrayList<>();
        List<Lab> allLabs = adminService.getAllLabs();
        if (allLabs.isEmpty()) {
            log.info("[getAllLabVersions] no labs in the database");
            labVersions.add("There aren't any labs in the database");
            return labVersions;
        }
        String outputString = "";
        for (Lab currentLab : allLabs) {
            outputString = "";
            outputString += currentLab.getLabName() + " \n";
            try {
                outputString += connectAndExecuteCommand(currentLab.getUserName(),
                        currentLab.getPassword(), currentLab.getHost(), currentLab.getPort(),
                        "sudo wae-status");
                outputString += "\n  \n";
                List<String> tokens = new ArrayList<>();
                // StringTokenizer tokenizer = new StringTokenizer(outputString);
                // while (tokenizer.hasMoreElements())
                // tokens.add(tokenizer.nextToken());
                // TODO: Not tested, old impl above.
                String[] outputStringTokenized = outputString.split(" ");
                Arrays.asList(outputStringTokenized).forEach(tokens::add);
                String currentVersion = tokens.get(15);
                labVersions.add(currentVersion);
            } catch (InterruptedException e) {
                log.error("[getAllLabVersions] InterruptedException: {}", e.getMessage(), e);
                labVersions.add("UNABLE TO CONNECT");
            }
        }
        return labVersions;
    }

    public String runCommandOnSelectedLab(String labName, String commandToBeExecuted) {
        log.debug("[runCommandOnSelectedLab] called");
        Lab labToExecute = adminService.findLabByName(labName);
        if (Objects.isNull(labToExecute)) {
            log.info("[runCommandOnSelectedLab] no lab to run command");
            return "There isn't a lab found in the database named " + labName;
        }
        String outputString = "";
        try {
            outputString += connectAndExecuteCommand(labToExecute.getUserName(), labToExecute.getPassword(),
                    labToExecute.getHost(), labToExecute.getPort(), commandToBeExecuted);
            log.debug("[runCommandOnSelectedLab] out string: {}", outputString);
        } catch (InterruptedException e) {
            log.error("[runCommandOnSelectedLab] InterruptedException: {}", e.getMessage(), e);
        }
        return outputString;
    }
}
