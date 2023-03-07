package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tr.com.orioninc.laborant.app.service.PasswordResetService;

@RestController
@RequestMapping("/pw")
@AllArgsConstructor
public class PasswordResetController {

    PasswordResetService passwordResetService;

    @PutMapping("/reset-password")
    @ApiOperation(value = "Password reset by taking code from email and new password from user")
    public ResponseEntity<String> resetPassword( @RequestParam String code, @RequestParam String newPassword){
        return ResponseEntity.ok(passwordResetService.resetPasswordByEmail(code, newPassword));
    }

    @PutMapping("/forgot-password")
    @ApiOperation(value = "Sending code to users email for resetting password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email){
        return ResponseEntity.ok(passwordResetService.forgotPasswordByEmail(email));
    }

}
