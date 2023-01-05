package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import tr.com.orioninc.laborant.app.model.Lab;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.exception.custom.NotConnectedException;
import tr.com.orioninc.laborant.exception.custom.NotFoundException;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class LabService {

    AdminService adminService;


    public String connectAndExecuteCommand(String username, String password,
                                           String host, int port) throws InterruptedException {

        log.debug("[connectAndExecuteCommand] called");

        Session session = null;
        ChannelExec channel = null;
        String responseString = null;
        if(adminService.isLabReachable(host,100)){
            try {
                session = new JSch().getSession(username, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setServerAliveInterval(100); // Check if server is alive every 200  miliseconds
                session.setServerAliveCountMax(1); // If server is not alive, try to reconnect once
                session.connect();
                if (session.isConnected()) {
                    log.info("[connectAndExecuteCommand] session connected for lab {}", host);

                }
                else {
                    log.info("[connectAndExecuteCommand] session not connected for lab {}", host);
                }
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand("sudo wae-status");
                log.info("[connectAndExecuteCommand] command to be executed: {} for lab {}", "sudo wae-status", host);
                ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                channel.setOutputStream(responseStream);
                channel.connect();

                while (channel.isConnected()) {
                    Thread.sleep(100);
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
            throw new NotConnectedException("Lab is not reachable");
        }
        log.info("For lab {} response is: {}", host, responseString);
        return responseString;
    }

    public String runStatusOnSelectedLab(String labName) {
        String outputString = null;
        log.debug("[runStatusOnSelectedLab] called");
        Lab labToExecute = adminService.findLabByName(labName);
        if (Objects.isNull(labToExecute)) {
            log.info("[runStatusOnSelectedLab] lab couldn't found in database");
            throw new NotFoundException("Lab couldn't found in database");
        }
        try {
            outputString = connectAndExecuteCommand(labToExecute.getUserName(), labToExecute.getPassword(),
                    labToExecute.getHost(), labToExecute.getPort());
            log.debug("[runStatusOnSelectedLab] out string: {}", outputString);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NotConnectedException("Couldn't connect to the lab");
        }
        return outputString;
    }
}
