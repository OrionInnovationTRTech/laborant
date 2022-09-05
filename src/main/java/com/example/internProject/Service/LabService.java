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

}
