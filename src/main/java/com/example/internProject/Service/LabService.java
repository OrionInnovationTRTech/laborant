package com.example.internProject.Service;

import com.example.internProject.Model.Lab;
import com.example.internProject.Repository.LabRepository;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
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
            String outputString = "";
            for (Lab currentLab: allLabs
                 )
            {
                outputString+= currentLab.getLabName() +   "    "+ "Host: "+ currentLab.getHost()  +  "      " ;
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

                        System.out.println(currentLine);
                    }
                    System.out.println(outputArray);
                    if (outputArray.get(1).get(9).equals("FAI")){
                        outputString += "SIGNAL 3";
                    }
                    else {
                        boolean stopFound = false;
                        boolean failFound = false;
                        for (String value : outputArray.get(1)) {
                            System.out.println(value);
                            if (value.equals("STO")) {
                                stopFound = true;
                            }
                            if (value.equals("FAI")) {
                                failFound = true;
                            }
                        }
                        if (failFound){
                            outputString +="SIGNAL 2";
                        }
                        else if (!failFound && stopFound) {
                            outputString+="SIGNAL 1";
                        }
                        else {
                            outputString+="SIGNAL 0";
                        }
                    }
                    outputString+=  " \n";
                    outputString+=response;
                    outputString+="\n  \n";
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
            System.out.println(outputString);
            return outputString;

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

}
