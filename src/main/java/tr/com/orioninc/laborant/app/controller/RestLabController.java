package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.service.LabService;
import tr.com.orioninc.laborant.app.service.UserService;
import tr.com.orioninc.laborant.exception.custom.NotAuthorizedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
@Log4j2
public class RestLabController {

    private LabService labService;
    private UserService userService;

    @GetMapping("/labs")
    @ApiOperation(value = "Getting all labs as a list of Lab objects")
    public ResponseEntity<List<Lab>> getAllLabs(Authentication authentication) {
        log.info("[getAllLabs] Getting all labs");
        List<Lab> labs = labService.getAllLabs();

        String authenticatedUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        labs = labs.stream().map(lab -> {
            boolean userAssignedToLab = lab.getUsers().stream().anyMatch(user -> user.getUsername().equals(authenticatedUsername));
            if (!userAssignedToLab && !isAdmin) {
                lab.setPassword("hidden");
            }
            return lab;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(labs);
    }


    @GetMapping("/labs/{labName}")
    @ApiOperation(value = "Getting a lab by giving 'labName' as a path variable")
    public ResponseEntity<Lab> getLab(@PathVariable("labName") String labName, Authentication authentication) {
        log.info("[getLab] Getting lab with name {}", labName);
        Lab lab = labService.getLab(labName);

        String authenticatedUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        boolean userAssignedToLab = lab.getUsers().stream().anyMatch(user -> user.getUsername().equals(authenticatedUsername));
        if (!userAssignedToLab && !isAdmin) {
            lab.setPassword("hidden");
        }

        return ResponseEntity.ok(lab);
    }

    @PostMapping("/labs/add/")
    @ApiOperation(value = "Adding a lab to database by giving Lab in body")
    public ResponseEntity<Lab> addNewLab(@RequestBody Lab lab, Authentication authentication) {
        log.info("[addNewLab] Called with lab: {}", lab.toString());
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            Lab savedLab = labService.addNewLab(lab);
            return ResponseEntity.ok(savedLab);
        } else {
            throw new NotAuthorizedException("You are not authorized to add a lab");
        }
    }

    @PostMapping("/labs/bulk-add")
    @ApiOperation(value = "Adding multiple labs to the database by giving list of Labs in body")
    public ResponseEntity<String> addBulkLab(@RequestBody List<Lab> labs, Authentication authentication) {
        log.info("[addBulkLab] Called with labs: {}", labs.toString());
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            int a = 0;
            int b = 0;
            Set<String> failedLabs = new HashSet<>();

            for (Lab lab : labs) {
                try {
                    labService.addNewLab(lab);
                    a++;
                } catch (Exception e) {
                    log.warn("[addBulkLab] Lab {} could not be added", lab.toString());
                    b++;
                    failedLabs.add("Name: " + lab.getLabName() + "  Host: " + lab.getHost());
                }
            }
            int c = a + b;
            if (!failedLabs.isEmpty()) {
                StringBuilder failedLabNames = new StringBuilder();
                for (String failedLab : failedLabs) {
                    failedLabNames.append(failedLab).append("\n");
                }
                return ResponseEntity.ok("Requested to add with " + c + " labs. " + a + " of them were added successfully, " + b + " of them couldn't added due to duplicate lab credentials. Labs couldn't add: \n " + failedLabNames);
            } else {
                return ResponseEntity.ok("Requested to add with " + c + " labs. " + a + " of them were added successfully, " + b + " of them couldn't added due to duplicate lab credentials.");
            }
        } else {
            throw new NotAuthorizedException("You are not authorized to add labs");
        }

    }

    @DeleteMapping("/labs/{labName}")
    @ApiOperation(value = "Deleting a lab from database by giving 'labName' as a path variable")
    public ResponseEntity<Lab> deleteLabByName(@PathVariable("labName") String labName, Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.info("[deleteLabByName] Called with labName: {}", labName);
            return ResponseEntity.ok(labService.deleteLabByName(labName));
        } else {
            Lab lab = labService.getLab(labName);
            //if lab is not assigned to any user, then delete can be done by any user
            if (lab.getUsers().isEmpty()) {
                log.info("[deleteLabByName] Called with labName: {}", labName);
                return ResponseEntity.ok(labService.deleteLabByName(labName));
            } else {
                throw new NotAuthorizedException("User is not authorized to delete this lab");
            }
        }
    }

    @PutMapping("/labs/{labName}")
    @ApiOperation(value = "Updating a lab in database by giving 'labName' as a path variable and Lab in body")
    public ResponseEntity<Lab> updateLabByNameByName(@PathVariable("labName") String labName, @RequestBody Lab lab, Authentication authentication) {

        String authenticatedUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
        if (isAdmin) {
            log.info("[updateLabByName] Called with labName: {}", labName);
            return ResponseEntity.ok(labService.updateLabByName(labName, lab));
        } else {
            Lab lab1 = labService.getLab(labName);
            boolean userAssignedToLab = lab1.getUsers().stream().anyMatch(user -> user.getUsername().equals(authenticatedUsername));
            if (userAssignedToLab) {
                log.info("[updateLabByName] Called with labName: {}", labName);
                return ResponseEntity.ok(labService.updateLabByName(labName, lab));
            } else {
                throw new NotAuthorizedException("User is not authorized to update this lab");
            }
        }
    }

    @GetMapping("/labs/status/{labName}")
    @ApiOperation(value = "Running status command on a lab by giving 'labName' as a path variable")
    public ResponseEntity<String> runCommand(@PathVariable("labName") String labName) {
        log.info("[runCommand] Running status command on lab {}", labName);
        return ResponseEntity.ok(labService.runCommandOnSelectedLab(labName, "sudo wae-status"));
    }

    @PostMapping("/labs/command")
    @ApiOperation(value = "Running a command on a lab by giving 'labName' as a path variable and command as parameter")
    public ResponseEntity<String> runCommand(@RequestParam("labName") String labName, @RequestParam("command") String command) {
        log.info("[runCommand] Running command {} on lab {}", command, labName);
        return ResponseEntity.ok(labService.runCommandOnSelectedLab(labName, command));
    }

    @PutMapping("/reserve-lab")
    @ApiOperation(value = "Reserving a lab by giving 'labName' in parameters")
    public ResponseEntity<Lab> reserveLab(@RequestParam String labName, @RequestParam String date, Authentication authentication) {
        log.info("[reserveLab] Called with labName: {}", labName);
        String username = authentication.getName();
        log.info("[reserveLab] Called with username: {}", username);
        log.info("[reserveLab] Called with date: {}", date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date reservedUntil = null;
        try {
            reservedUntil = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Date format is not correct");
        }
        if (reservedUntil.before(new Date())) {
            throw new RuntimeException("Date can't be in the past");
        }
        log.info("[reserveLab] Called with reservedUntil: {}", reservedUntil);
        Lab lab = labService.getLab(labName);
        if (lab.getUsers().isEmpty()) {
            return ResponseEntity.ok(labService.reserveLab(labName, username, reservedUntil));
        } else {
            boolean userAssignedToLab = lab.getUsers().stream().anyMatch(user -> user.getUsername().equals(username));
            if (userAssignedToLab) {
                return ResponseEntity.ok(labService.reserveLab(labName, username, reservedUntil));
            } else {
                throw new NotAuthorizedException("User is not authorized to reserve this lab");
            }
        }
    }


    @PutMapping("/unreserve-lab")
    @ApiOperation(value = "Unreserving a lab by giving 'labName' in parameters")
    public ResponseEntity<Lab> unreserveLab(@RequestParam String labName, Authentication authentication) {
        log.info("[unreserveLab] Called with labName: {}", labName);
        String username = authentication.getName();
        Lab lab = labService.getLab(labName);
        boolean userWhoReservedLab = lab.getReservedBy().getUsername().equals(username);
        log.info("[unreserveLab] Called with username: {}", username);
        log.info("[unreserveLab] Lab reserved by: {}", lab.getReservedBy().getUsername());
        if (userWhoReservedLab) {
            log.info("[unreserveLab] Unreserved lab {}", labName);
            return ResponseEntity.ok(labService.unreserveLab(labName));
        } else {
            throw new NotAuthorizedException("User is not authorized to unreserve this lab");
        }
    }


    @PutMapping("/registerToWaitingList")
    @ApiOperation(value = "Registering a user to waiting list of a lab by giving 'labName' and 'username' in parameters")
    public ResponseEntity<String> registerToWaitingList(@RequestParam String labName, @RequestParam String username, Authentication authentication) {
        log.info("[registerToWaitingList] Called with labName: {}", labName);
        log.info("[registerToWaitingList] Called with username: {}", username);
        if (labService.getLab(labName).getReservedBy().getUsername().equals(username)) {
            throw new RuntimeException("You already reserved this lab");
        }
        if (labService.getLab(labName).getMailAwaitingUsers().contains(userService.getUserByUsername(username))) {
            throw new RuntimeException("You are already in waiting list of this lab");
        }
        return ResponseEntity.ok(labService.registerUserToWaitingList(labName, username));
    }

}

