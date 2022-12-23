package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.service.AdminService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/v1")
@AllArgsConstructor
@RestController
@Log4j2
@CrossOrigin
public class RestAdminController {
    private AdminService adminService;

    @GetMapping("/login")
    @ApiOperation(value = "Login")
    public ResponseEntity<Map<String, String>> getSession(HttpServletRequest request) {
        // Check if the user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Get the session ID
            String sessionId = request.getSession().getId();

            // Return the session ID as a response
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", sessionId);
            log.info("[getSession] Session ID: {}", response);
            return ResponseEntity.ok(response);
        } else {
            // Return an error if the user is not authenticated
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/labs")
    @ApiOperation(value = "Getting all labs as a list of Lab objects")
    public ResponseEntity<List<Lab>> getAllLabs(Authentication authentication) {
        log.info("[getAllLabs] Getting all labs");
        List<Lab> labs = adminService.getAllLabs();
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            // If the authenticated user is not an admin, set the password to "hidden"
            labs = labs.stream().map(lab -> {
                lab.setPassword("hidden");
                return lab;
            }).collect(Collectors.toList());
        }
        return ResponseEntity.ok(labs);
    }

    @GetMapping("/labs/{labName}")
    @ApiOperation(value = "Getting a lab by giving 'labName' as a path variable")
    public ResponseEntity<Lab> getLab(@PathVariable("labName") String labName) {
        log.info("[getLab] Getting lab with name {}", labName);
        return ResponseEntity.ok(adminService.getLab(labName));
    }

//    @PostMapping("/login")
//    @ApiOperation(value = "Logging in to the system")
//    public ResponseEntity<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {
//       Authentication authentication;
//        try{
//            authentication = new UsernamePasswordAuthenticationToken(username, password);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            log.info("[login] User {} logged in", username);
//            return ResponseEntity.ok("Successfully logged in");
//        } catch (BadCredentialsException e) {
//            log.warn("[login] User {} failed to log in", username);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong username or password");
//        }
//    }

    @PostMapping("/labs/add/")
    @ApiOperation(value = "Adding a lab to database by giving Lab in body")
    public ResponseEntity<Lab> addNewLab(@RequestBody Lab lab) {
        log.info("[addNewLab] Called with lab: {}", lab.toString());
        return ResponseEntity.ok(adminService.addNewLab(lab));
    }

    @PostMapping("/labs/bulk-add")
    @ApiOperation(value = "Adding multiple labs to the database by giving list of Labs in body")
    public ResponseEntity<String> addBulkLab (@RequestBody List<Lab> labs) {
        log.info("[addNewLab] Called with labs: {}", labs.toString());
        int a = 0;
        int b = 0;
        for (Lab lab : labs) {
           try {
               adminService.addNewLab(lab);
               a++;
           } catch (Exception e) {
               log.warn("[addNewLab] Lab {} could not be added", lab.toString());
               b++;
           }
        }
        int c = a+b;
        return ResponseEntity.ok("Requested to add with " + c + " labs. " + a + " of them were added successfully, " + b + " of them couldn't added due to duplicate lab credentials.");
    }

    @DeleteMapping("/labs/{labName}")
    @ApiOperation(value = "Deleting a lab from database by giving 'labName' as a path variable")
    public ResponseEntity<Lab> deleteLabByName(@PathVariable("labName") String labName) {
        log.info("[deleteLab] Called with labName: {}", labName);
        return ResponseEntity.ok(adminService.deleteLabByName(labName));
    }
    @PutMapping("/labs/{labName}")
    @ApiOperation(value = "Updating a lab in database by giving 'labName' as a path variable and Lab in body")
    public ResponseEntity<Lab> updateLabByNameByName(@PathVariable("labName") String labName, @RequestBody Lab lab) {
        log.info("[updateLabByName] Called with labName: {}", labName);
        return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
    }


}
