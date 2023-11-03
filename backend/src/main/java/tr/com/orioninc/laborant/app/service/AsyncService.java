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
                "Your password reset link is: " + reactAppApiUrl + "/forgot-password?code=" + code + ". \nYou have 15 minutes to reset your password. You can click the link or enter it manually. \nYour password reset code is: " + code +" .\n\n If you did not request a password reset, you can safely ignore this email."
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


    @Async
    public CompletableFuture<Void> sendApprovalEmail(String email, String code) {
        emailService.sendEmail(
                email,
                "Laborant - Email Approval",
                "Your email approval link is: " + reactAppApiUrl + "/approve-email?code=" + code + ". \nYou have 15 minutes to approve your account. \nYou can click the link or enter it manually. Your account approval code is: " + code +"." +
                        "\n\nIf you did not request this email, you can safely ignore that email."
        );
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendCredentialsWithEmail(String email, String username, String password) {
        emailService.sendEmail(
                email,
                "Laborant - Your Credentials",
                "Welcome to Laborant!\n\nThat application is for have total control over labs.\nApplication works on 47.168.150.38 machine and can be accessed from the link below:\n\n" +
                        reactAppApiUrl + "\n\n" +
                        "Your login credentials are: \n" +
                        "Username: " + username + "\n" +
                        "Password: " + password + "\n\n" +
                        "You can change your password from the web application - dashboard section.\n\nThank you for using laborant. \n\n\n" +
                        "Developed by Ergüncan Keçelioğlu under the design team as internship project.\n" +
                        "If you have any questions, requests or bug reports, please contact me via email or MS teams directly.\nerguncan.kecelioglu@orioninc.com"
        );
        return CompletableFuture.completedFuture(null);
    }
}
