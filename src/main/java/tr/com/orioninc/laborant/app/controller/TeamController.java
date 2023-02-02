package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.model.Team;
import tr.com.orioninc.laborant.app.service.TeamService;
import tr.com.orioninc.laborant.exception.custom.NotAuthorizedException;

import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private TeamService teamService;


    @PostMapping("/add")
    @ApiOperation(value = "Adding a new team to database by giving Team in body")
    public ResponseEntity<Team> addTeam(@RequestBody Team team, Authentication authentication) {
        log.info("[addTeam] Called with team: {}", team.toString());
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return ResponseEntity.ok(teamService.addNewTeam(team));
        } else {
            throw new NotAuthorizedException("User is not authorized to add a new team");
        }
    }

    @DeleteMapping("/delete/{teamName}")
    @ApiOperation(value = "Deleting a team from database by giving 'teamName' as a path variable")
    public ResponseEntity<String> deleteTeamByName(@PathVariable("teamName") String teamName, Authentication auth) {
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            log.info("[deleteTeamByName] Called with teamName: {}", teamName);
            if (teamService.deleteTeamByName(teamName)) {
                return ResponseEntity.ok("Team deleted successfully");
            } else {
                return ResponseEntity.ok("Team not found");
            }
        } else {
            throw new NotAuthorizedException("User is not authorized to delete a team");
        }
    }

    @GetMapping("/")
    @ApiOperation(value = "Getting all teams from database")
    public ResponseEntity<List<Team>> getAllTeams() {
        log.info("[getAllTeams] Called");
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{teamName}")
    @ApiOperation(value = "Getting a team from database by giving 'teamName' as a path variable")
    public ResponseEntity<Team> getTeamByName(@PathVariable("teamName") String teamName) {
        log.info("[getTeamByName] Called with teamName: {}", teamName);
        return ResponseEntity.ok(teamService.getTeamByName(teamName));

    }
}
