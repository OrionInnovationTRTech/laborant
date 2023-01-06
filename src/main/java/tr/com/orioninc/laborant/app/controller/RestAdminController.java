package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.service.AdminService;
import tr.com.orioninc.laborant.exception.custom.NotAuthorizedException;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String sessionId = request.getSession().getId();
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", sessionId);
            log.info("[getSession] Session ID: {}", response);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/assign-user")
    public ResponseEntity<String> assignUserToLab(@RequestParam String username, @RequestParam String labName,Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.info("[assignUserToLab] Assigning user {} to lab {}", username, labName);
            adminService.assignLabToUser(username, labName);
            return ResponseEntity.ok("User " + username+ " assigned to lab " + labName + " successfully");
        } else {
            log.info("[assignUserToLab] User {} is not authorized to assign users to labs", auth.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/unassign-user")
    public ResponseEntity<String> unassignUserFromLab(@RequestParam String username, @RequestParam String labName, Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.info("[unassignUserFromLab] Unassigning user {} from lab {}", username, labName);
            adminService.unassignLabFromUser(username, labName);
            return ResponseEntity.ok("User " + username+ " unassigned from lab " +labName+" successfully");
        } else {
            log.info("[unassignUserFromLab] User {} is not authorized to unassign users from labs", auth.getName());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/lab-users/{labName}")
    public ResponseEntity<ArrayList<String>> getAssignedLabUsers(@PathVariable String labName) {
        return ResponseEntity.ok(adminService.getAssignedLabUsers(labName));
    }

    @GetMapping("/lab-teams/{labName}")
    public ResponseEntity<ArrayList<String>> getAssignedLabTeams(@PathVariable String labName) {
        return ResponseEntity.ok(adminService.getAssignedLabTeams(labName));
    }

    @GetMapping("/labs")
    @ApiOperation(value = "Getting all labs as a list of Lab objects")
    public ResponseEntity<List<Lab>> getAllLabs(Authentication authentication) {
        log.info("[getAllLabs] Getting all labs");
        List<Lab> labs = adminService.getAllLabs();

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
        Lab lab = adminService.getLab(labName);

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
    public ResponseEntity<Lab> deleteLabByName(@PathVariable("labName") String labName,Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.info("[deleteLabByName] Called with labName: {}", labName);
            return ResponseEntity.ok(adminService.deleteLabByName(labName));
        } else {
            Lab lab = adminService.getLab(labName);
            //if lab is not assigned to any user, then delete can be done by any user
            if (lab.getUsers().isEmpty()) {
                log.info("[deleteLabByName] Called with labName: {}", labName);
                return ResponseEntity.ok(adminService.deleteLabByName(labName));
            } else {
                throw new NotAuthorizedException("User is not authorized to delete this lab");
            }
        }
    }

    @PutMapping("/labs/{labName}")
    @ApiOperation(value = "Updating a lab in database by giving 'labName' as a path variable and Lab in body")
    public ResponseEntity<Lab> updateLabByNameByName(@PathVariable("labName") String labName, @RequestBody Lab lab,Authentication authentication) {

        String authenticatedUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        if (isAdmin) {
            log.info("[updateLabByName] Called with labName: {}", labName);
            return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
        } else {
            Lab lab1 = adminService.getLab(labName);
            boolean userAssignedToLab = lab1.getUsers().stream().anyMatch(user -> user.getUsername().equals(authenticatedUsername));
            if (userAssignedToLab) {
                log.info("[updateLabByName] Called with labName: {}", labName);
                return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
            } else {
                throw new NotAuthorizedException("User is not authorized to update this lab");
            }
        }
    }

    @PutMapping("/assign-team")
    @ApiOperation(value = "Assigning a team to a user by giving 'username' and 'teamName' in parameters")
    public ResponseEntity<String> assignLabToTeam(@RequestParam("labName") String labName, @RequestParam("teamName") String teamName, Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.info("[assignLabToTeam] Called with labName: {} and teamName: {}", labName, teamName);
            adminService.assignLabToTeam(labName, teamName);
            return ResponseEntity.ok("Lab " + labName + " assigned to team " + teamName);
        } else {
            throw new NotAuthorizedException("User is not authorized to assign a lab to a team");
        }
    }

    @PutMapping("/unassign-team")
    @ApiOperation(value = "Unassigning a team from a user by giving 'username' and 'teamName' in parameters")
    public ResponseEntity<String> unassignLabFromTeam(@RequestParam("labName") String labName, @RequestParam("teamName") String teamName, Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.info("[unassignLabFromTeam] Called with labName: {} and teamName: {}", labName, teamName);
            adminService.unassignLabFromTeam(labName, teamName);
            return ResponseEntity.ok("Lab " + labName + " unassigned from team " + teamName);
        } else {
            throw new NotAuthorizedException("User is not authorized to unassign a lab from a team");
        }
    }

    @PutMapping("/reserve-lab")
    @ApiOperation(value = "Reserving a lab by giving 'labName' in parameters")
    public ResponseEntity<Lab> reserveLab(@RequestParam String labName, Authentication authentication) {
        log.info("[reserveLab] Called with labName: {}", labName);
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return ResponseEntity.ok(adminService.reserveLab(labName));
        } else  {
            Lab lab = adminService.getLab(labName);
            if (lab.getUsers().isEmpty()) {
                return ResponseEntity.ok(adminService.reserveLab(labName));
            } else {
                boolean userAssignedToLab = lab.getUsers().stream().anyMatch(user -> user.getUsername().equals(authentication.getName()));
                if (userAssignedToLab) {
                    return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
                } else {
                    throw new NotAuthorizedException("User is not authorized to reserve this lab");
                }
            }
        }
    }

    @PutMapping("/unreserve-lab")
    @ApiOperation(value = "Unreserving a lab by giving 'labName' in parameters")
    public ResponseEntity<Lab> unreserveLab(@RequestParam String labName, Authentication authentication) {
        log.info("[unreserveLab] Called with labName: {}", labName);
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return ResponseEntity.ok(adminService.unreserveLab(labName));
        } else  {
            Lab lab = adminService.getLab(labName);
            if (lab.getUsers().isEmpty()) {
                return ResponseEntity.ok(adminService.unreserveLab(labName));
            } else {
                boolean userAssignedToLab = lab.getUsers().stream().anyMatch(user -> user.getUsername().equals(authentication.getName()));
                if (userAssignedToLab) {
                    return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
                } else {
                    throw new NotAuthorizedException("User is not authorized to unreserve this lab");
                }
            }
        }
    }



}
