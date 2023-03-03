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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
            response.put("username", authentication.getName());
            log.info("[getSession] Session ID: {}", response);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/assign-user")
    public ResponseEntity<String> assignUserToLab(@RequestParam String username, @RequestParam String labName, Authentication auth) {
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

    @GetMapping("/team-labs/{teamName}")
    public ResponseEntity<ArrayList<String>> getAssignedTeamLabs(@PathVariable String teamName) {
        return ResponseEntity.ok(adminService.getAssignedTeamLabs(teamName));
    }

    @GetMapping("/user-labs/{username}")
    public ResponseEntity<ArrayList<String>> getAssignedUserLabs(@PathVariable String username) {
        return ResponseEntity.ok(adminService.getAssignedUserLabs(username));
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
}
