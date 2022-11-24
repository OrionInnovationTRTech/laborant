package tr.com.orioninc.laborant.app.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.service.LabService;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class RestLabController {

    private LabService labService;

    @GetMapping("/labs/status")
    public ResponseEntity<String> getAllLabsStatus() {
        return ResponseEntity.ok(labService.getAllLabsStatus());
    }
    @GetMapping("/labs/runCommand/{labName}")
    public ResponseEntity<String> runCommand(@PathVariable("labName") String labName, @RequestParam String command) {
        return ResponseEntity.ok(labService.runCommandOnSelectedLab(labName, command));
    }
    @GetMapping("/labs/status/{labName}")
    public ResponseEntity<String> getLabStatus(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(labService.getLabStatus(labName));
    }
}

