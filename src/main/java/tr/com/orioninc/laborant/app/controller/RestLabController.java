package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.service.LabService;
import tr.com.orioninc.laborant.exception.NotConnected;
import tr.com.orioninc.laborant.exception.NotFound;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
@Log4j2
public class RestLabController {

    private LabService labService;

    @GetMapping("/labs/status")
    @ApiOperation(value = "Getting status of all labs in database")
    public ResponseEntity<String> getAllLabsStatus() {
        return ResponseEntity.ok(labService.getAllLabsStatus());
    }

    @GetMapping("/labs/runCommand/{labName}")
    @ApiOperation(value = "Running a command on a lab by giving 'labName' as a path variable and command as parameter")
    public ResponseEntity<String> runCommand(@PathVariable("labName") String labName, @RequestParam(value = "command",required = false) String command) {
            log.info("[runCommand] Running command {} on lab {}", command, labName);
            return ResponseEntity.ok(labService.runCommandOnSelectedLab(labName, command));
    }

    @GetMapping("/labs/status/{labName}")
    @ApiOperation(value = "Getting status of a lab by giving 'labName' as a path variable")
    public ResponseEntity<String> getLabStatus(@PathVariable("labName") String labName) {
        if (labService.getLabStatus(labName) == null) {
            log.warn("[getLabStatus] Lab with name {} not found in database", labName);
            throw new NotFound("Lab named " + labName + " not found");
        }
        else {
            return ResponseEntity.ok(labService.getLabStatus(labName));
        }
    }
}

