package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.service.LabService;
import tr.com.orioninc.laborant.exception.NotConnected;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class RestLabController {

    private LabService labService;

    @GetMapping("/labs/status")
    @ApiOperation(value = "Getting status of all labs in database")
    public ResponseEntity<String> getAllLabsStatus() {
        return ResponseEntity.ok(labService.getAllLabsStatus());
    }

    @GetMapping("/labs/runCommand/{labName}")
    @ApiOperation(value = "Running a command on a lab by giving 'labName' as a path variable and command as parameter")
    public ResponseEntity<String> runCommand(@PathVariable("labName") String labName, @RequestParam String command) {
        if (labService.runCommandOnSelectedLab(labName, command) == null) {
            throw new NotConnected("Couldn't connect to the lab " + labName);
        }
        else {
            return ResponseEntity.ok(labService.runCommandOnSelectedLab(labName, command));
        }
    }

    @GetMapping("/labs/status/{labName}")
    @ApiOperation(value = "Getting status of a lab by giving 'labName' as a path variable")
    public ResponseEntity<String> getLabStatus(@PathVariable("labName") String labName) {
        if (labService.getLabStatus(labName) == null) {
            throw new NotConnected("Couldn't connect to the lab");
        }
        else {
            return ResponseEntity.ok(labService.getLabStatus(labName));
        }
    }
}

