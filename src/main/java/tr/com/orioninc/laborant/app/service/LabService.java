package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import tr.com.orioninc.laborant.app.model.Lab;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.exception.NotConnected;
import tr.com.orioninc.laborant.exception.NotFound;

import java.io.IOException;
import java.net.InetAddress;
import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class LabService {

    AdminService adminService;

    public String connectAndExecuteCommand(String username, String password,
                                           String host, int port, String command) throws InterruptedException {

        log.debug("[connectAndExecuteCommand] called");
        Session session = null;
        ChannelExec channel = null;
        String responseString = null;
        if(adminService.isLabReachable(host,200)){
            try {
                session = new JSch().getSession(username, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setServerAliveInterval(200); // Check if server is alive every 200  miliseconds
                session.setServerAliveCountMax(1); // If server is not alive, try to reconnect once
                session.connect();
                if (session.isConnected()) {
                    log.info("[connectAndExecuteCommand] session connected");

                }
                else {
                    log.info("[connectAndExecuteCommand] session not connected");
                }
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(command);
                log.info("[connectAndExecuteCommand] command to be executed: {}", command);
                ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                channel.setOutputStream(responseStream);
                channel.connect();

                while (channel.isConnected()) {
                    Thread.sleep(200);
                }

                responseString = responseStream.toString();
            } catch (JSchException e) {
                log.error("[connectAndExecuteCommand] ssh exception: {}", e.getMessage(), e);
                return "Error. " + e.getMessage();
            } finally {
                if (session != null) {
                    session.disconnect();
                }
                if (channel != null) {
                    channel.disconnect();
                }
            }
        }
        else {
            log.error("[connectAndExecuteCommand] Lab is not reachable");
            return "Couldn't connect to the lab. Check your internet or vpn connection.";
        }
        return responseString;
    }


    public String getAllLabsStatus() {
        log.info("[getAllLabsStatus] called");
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

    private String generateOutputString(String outputString, String response) {
        log.info("[generateOutputString] called");
        Scanner scanner = new Scanner(response);
        String currentLine = null;
        boolean last = false;
        List<List<String>> outputArray = new ArrayList<>();
        while (scanner.hasNextLine()) {
            List<String> words = new ArrayList<>();
            currentLine = scanner.nextLine();
            log.info("[generateOutputString] current line: {}", currentLine);
            if (currentLine.equals("Couldn't connect to the lab. Check your internet or vpn connection.")) {
                outputString += "COULDN'T CONNECT";
            }
            else {
                String[] curLineTokenized = currentLine.split(" ");
                Arrays.asList(curLineTokenized).forEach(words::add);
                outputArray.add(words);
                last = true;
            }
        }
        if (last) {
            try {
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
            } catch (IndexOutOfBoundsException e) {
                log.error("[generateOutputString] IndexOutOfBoundsException: {}", e.getMessage(), e);
                outputString += "COULDN'T CONNECT";

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
                    String[] outputStringTokenized = outputString.split(" ");
                    Arrays.asList(outputStringTokenized).forEach(tokens::add);
                                                                            // I just write outputStringTokenized.length-1 instead of 5
                    String currentVersion = tokens.get(5);    // TODO CHECK LATER AND FIX
                    labVersions.add(currentVersion);
            } catch (InterruptedException e) {
                log.error("[getAllLabVersions] InterruptedException: {}", e.getMessage(), e);
                labVersions.add("UNABLE TO CONNECT");
            }
        }
        return labVersions;
    }

    public String runCommandOnSelectedLab(String labName, String commandToBeExecuted) {
        String outputString = null;
        log.debug("[runCommandOnSelectedLab] called");
        Lab labToExecute = adminService.findLabByName(labName);
        if (Objects.isNull(labToExecute)) {
            log.info("[runCommandOnSelectedLab] lab couldn't found in database");
            throw new NotFound("Lab couldn't found in database");
        }
        try {
            outputString = connectAndExecuteCommand(labToExecute.getUserName(), labToExecute.getPassword(),
                    labToExecute.getHost(), labToExecute.getPort(), commandToBeExecuted);
            log.debug("[runCommandOnSelectedLab] out string: {}", outputString);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotConnected("Couldn't connect to the lab");
        }
        return outputString;
    }

    public String getLabStatus(String labName) {
        log.debug("[getLabStatus] called");
        String outputString = null;
        Lab labToExecute = adminService.findLabByName(labName);
        if (Objects.isNull(labToExecute)) {
            log.info("[getLabStatus] no lab to run command");
            return null;
        }
        try {
            outputString = connectAndExecuteCommand(labToExecute.getUserName(), labToExecute.getPassword(),
                    labToExecute.getHost(), labToExecute.getPort(), "sudo wae-status");
            log.info("[getLabStatus] out string: {}", outputString);
        } catch (InterruptedException e) {
            log.error("[getLabStatus] InterruptedException: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return outputString;
    }
}
