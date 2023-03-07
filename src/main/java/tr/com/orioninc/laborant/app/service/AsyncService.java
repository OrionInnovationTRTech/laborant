package tr.com.orioninc.laborant.app.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.LabRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EnableAsync
@Service
@Log4j2
public class AsyncService {

    @Autowired
    private EmailService emailService;
    @Value("${REACT_APP_API_URL}")
    private String reactAppApiUrl;
    @Autowired
    private LabRepository labRepo;

    @Async
    public CompletableFuture<Void> sendPasswordResetEmail(String email, String code) {
        emailService.sendEmail(
                email,
                "Laborant - Password Reset",
                "Your password reset link is: " + reactAppApiUrl + "/forgot-password?code=" + code + ". You have 15 minutes to reset your password. You can click the link or enter it manually. Your password reset code is: " + code
        );
        return CompletableFuture.completedFuture(null);
    }

    @Async
    CompletableFuture<Void> sendMailToWaiters(Lab labToBeUnreserved) {
        if (labToBeUnreserved.getMailAwaitingUsers() != null) {
            List<User> awaitingUsers = labToBeUnreserved.getMailAwaitingUsers();
            log.info("[emaillabs awaiting user for lab {} is {}", labToBeUnreserved.getLabName(), awaitingUsers);
            if (!awaitingUsers.isEmpty()) {
                for (User user : awaitingUsers) {
                    emailService.sendEmail(user.getEmail(), labToBeUnreserved.getHost() + " lab is now free",
                            "Hi " + user.getUsername() + ",\n" + "\nThe lab " + labToBeUnreserved.getHost() + " is now free. \n" +
                                    "You can reserve it from the laborant web application. \n\n" +
                                    "Thank you for using laborant.");
                }
            }
        }
        labToBeUnreserved.setMailAwaitingUsers(null);
        labRepo.save(labToBeUnreserved);
        return CompletableFuture.completedFuture(null);
    }


}
