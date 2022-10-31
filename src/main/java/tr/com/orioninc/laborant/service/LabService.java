package tr.com.orioninc.laborant.service;

import lombok.extern.log4j.Log4j2;
import tr.com.orioninc.laborant.model.Lab;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
@Log4j2
public class LabService
{
    @Autowired
    AdminService adminService;
    

    public static String connectAndExecuteCommand(String username, String password,
                                           String host, int port, String command) throws Exception {

        Session session = null;
        ChannelExec channel = null;
        String responseString = null;

        try {
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            if(session.isConnected()){
                log.info("[connectAndExecuteCommand] Session connected");
            }

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(500);
            }

            responseString = new String(responseStream.toByteArray());
        }
        catch(Exception e)
        {
            log.error("[connectAndExecuteCommand] Error occured while connecting to lab: {}" , e.getMessage());
            e.printStackTrace();
        }
        finally {
            if (session != null) {
                session.disconnect();
                log.info("[connectAndExecuteCommand] Session disconnected");
            }
            if (channel != null) {
                log.info("[connectAndExecuteCommand] Channel disconnected");
                channel.disconnect();
            }
        }
        log.info("[connectAndExecuteCommand] Response string: {}", responseString);
        return responseString; // it was inside finally block
    }

    public String getALlLabsStatus(){
        List<Lab> allLabs = adminService.getAllLabs();
        if (allLabs.size() == 0){
            log.warn("[getALlLabsStatus] There is no lab in the database.");
            return "There aren't any labs in the database";
        }
        else{
            String response = "";
            StringBuilder outputString = new StringBuilder();
            for (Lab currentLab: allLabs) {
                outputString.append(currentLab.toString()); //toString instead of appending all parameters one by one.
                try {
                    response =
                    connectAndExecuteCommand(currentLab.getUserName(),
                            currentLab.getPassword(), currentLab.getHost(), currentLab.getPort(),
                            "sudo wae-status");
                    Scanner scanner = new Scanner(response);
                    String currentLine = null;
                    List<List<String>> outputArray = new ArrayList<>();
                    while (scanner.hasNextLine())
                    {
                        List<String> words = new ArrayList<>();
                        currentLine = scanner.nextLine();
                        StringTokenizer tokenizer = new StringTokenizer(currentLine);
                        while (tokenizer.hasMoreElements())
                            words.add(tokenizer.nextToken());
                        outputArray.add(words);

                        log.info(currentLine); // I don't know what this info prints. Will look at it while connected to a real lab
                    }
                    log.info(outputArray); // I don't know what this info prints. ****
                    if (outputArray.get(1).get(9).equals("FAI")){
                        outputString.append("SIGNAL 3");
                    }
                    else {
                        boolean stopFound = false;
                        boolean failFound = false;
                        for (String value : outputArray.get(1)) {
                            log.info(value);
                            if (value.equals("STO")) {
                                stopFound = true;
                            }
                            if (value.equals("FAI")) {
                                failFound = true;
                            }
                        }
                        if (failFound){
                            outputString.append("SIGNAL 2");
                        }
                        else if (!failFound && stopFound) {
                            outputString.append("SIGNAL 1");
                        }
                        else {
                            outputString.append("SIGNAL 0");
                        }
                    }
                    outputString.append(" \n");
                    outputString.append(response);
                    outputString.append("\n  \n");
                }
                catch(Exception e)
                {
                    log.error("[getALlLabsStatus] error while getting lab status",e);
                    e.printStackTrace();
                }

            }
            log.info("[getAllLabsStatus] {}", outputString);
            return outputString.toString();

        }



    }

    public List<String> getAllLabVersions(){
        List<String> labVersions = new ArrayList<>();
        List<Lab> allLabs = adminService.getAllLabs();
        if (allLabs.size() == 0)
        {
            log.warn("[getAllLabVersions] There is no lab in the database.");
            labVersions.add("There aren't any labs in the database");
            return labVersions;
        }
        else {
            String outputString = "";
            for (Lab currentLab : allLabs) {
                outputString = "";
                outputString += currentLab.getLabName() + " \n";
                try {
                    outputString +=
                            connectAndExecuteCommand(currentLab.getUserName(),
                                    currentLab.getPassword(), currentLab.getHost(), currentLab.getPort(),
                                    "sudo wae-status");
                    outputString += "\n  \n";
                    List<String> tokens = new ArrayList<>();
                    StringTokenizer tokenizer = new StringTokenizer(outputString);
                    while (tokenizer.hasMoreElements())
                        tokens.add(tokenizer.nextToken());
                    String currentVersion = tokens.get(15);
                    labVersions.add(currentVersion);
                } catch (Exception e) {
                    log.error("[getAllLabVersions] error while getting lab version on lab: {}", currentLab.getLabName(), e);
                    e.printStackTrace();
                    labVersions.add("UNABLE TO CONNECT");


                }

            }
            log.info("[LabService] {}", labVersions);
            return labVersions;
        }
    }

    public String runCommandOnSelectedLab(String labName,String commandToBeExecuted){

        Lab labToExecute = adminService.findLabByName(labName);
        if (Objects.isNull(labToExecute)){
            log.warn("[runCommandOnSelectedLab] There is no lab with the given name.");
            return "There is no lab found in the database named "+ labName;
        }
        else
        {
            String outputString = "";
            try {
                outputString+=
                connectAndExecuteCommand(labToExecute.getUserName(), labToExecute.getPassword(),
                        labToExecute.getHost(), labToExecute.getPort(), commandToBeExecuted);
            }
            catch(Exception e)
            {
                log.error("[runCommandOnSelectedLab] error while running command on lab: {}",labName, e);
                e.printStackTrace();
            }
            log.info("[runCommandOnSelectedLab] {}", outputString);
            return outputString;

        }

    }

}
