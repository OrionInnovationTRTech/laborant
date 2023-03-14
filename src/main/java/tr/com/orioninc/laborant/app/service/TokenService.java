package tr.com.orioninc.laborant.app.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tr.com.orioninc.laborant.app.model.Token;
import tr.com.orioninc.laborant.app.model.User;
import tr.com.orioninc.laborant.app.repository.TokenRepository;
import tr.com.orioninc.laborant.app.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
@Log4j2
@AllArgsConstructor
public class TokenService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private AsyncService asyncService;

    public TokenService() {
        // Default constructor
    }

    public TokenService(UserRepository userRepository, TokenRepository tokenRepository, EmailService emailService, @Value("${REACT_APP_API_URL}") String reactAppApiUrl) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }


    public String generateCode(int length, String type) {
        String code = "";
        Random random = new Random();
        if (type == "numeric") {
            for (int i = 0; i < length; i++) {
                code += random.nextInt(10);
            }
        } else if (type == "alphanumeric") {
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            for (int i = 0; i < length; i++) {
                code += characters.charAt(random.nextInt(characters.length()));
            }
        }
        if (tokenRepository != null) {
            while (tokenRepository.findByToken(code) != null) {
                code = generateCode(length, type);
            }
        }
        return code;
    }

    public String hash(String value) {
        String hashedValue = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            hashedValue = Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedValue;
    }


    @Scheduled(fixedRate = 3600000)
    public void flushTokens() {
        for (Token token : tokenRepository.findAll()) {
            if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
                tokenRepository.delete(token);
            }
        }
    }


    public Token findByUser(User user) {
        return tokenRepository.findByUser(user);
    }

    public void deleteByUserId(Integer id) {
        tokenRepository.deleteByUserId(id);
    }

    public void saveToken(Token passwordResetToken) {
        tokenRepository.save(passwordResetToken);
    }

    public Token findByToken(String hashedCode) {
        return tokenRepository.findByToken(hashedCode);
    }

    public void deleteToken(Token passwordResetToken) {
        tokenRepository.delete(passwordResetToken);
    }

    public Token findByEmail(String email) {
        return tokenRepository.findByEmail(email);
    }
}
