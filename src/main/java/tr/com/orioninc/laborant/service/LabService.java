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
            e.printStackTrace();
        }
        finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
            return responseString;
        }

    }

    public String getALlLabsStatus(){
        List<Lab> allLabs = adminService.getAllLabs();
        if (allLabs.size() == 0){
            return "There aren't any labs in the database";
        }
        else{
            String response = "";
            StringBuilder outputString = new StringBuilder();
            for (Lab currentLab: allLabs) {
                outputString.append("Lab: ").append(currentLab.getLabName()).append("  ").append("Host: ").append(currentLab.getHost()).append("----");
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

                        log.info(currentLine); // I dont know what this info prints.
                    }
                    log.info(outputArray); // I dont know what this info prints.
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
                    e.printStackTrace();
                }

            }
            log.info("[LABSERVICE] " + outputString);
            return outputString.toString();

        }



    }

    public List<String> getAllLabVersions(){
        List<String> labVersions = new ArrayList<>();
        List<Lab> allLabs = adminService.getAllLabs();
        if (allLabs.size() == 0)
        {
            labVersions.add("There aren't any labs in the database");
            return labVersions;
        }
        else {
            String outputString = "";
            for (Lab currentLab : allLabs
            ) {
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
                    e.printStackTrace();
                    labVersions.add("UNABLE TO CONNECT");


                }

            }
            return labVersions;
        }
    }

    public String runCommandOnSelectedLab(String labName,String commandToBeExecuted){

        Lab labToExecute = adminService.findLabByName(labName);
        if (Objects.isNull(labToExecute)){
            return "There isn't a lab found in the database named "+ labName;
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
                e.printStackTrace();
            }
            return outputString;

        }

    }

}
