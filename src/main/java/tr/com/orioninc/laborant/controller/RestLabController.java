package tr.com.orioninc.laborant.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.service.LabService;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class RestLabController {

    private LabService labService;

    @GetMapping("/labs/status")
    public ResponseEntity<String> getAllLabsStatus() {
        return ResponseEntity.ok(labService.getAllLabsStatus());
    }
    @GetMapping("/labs/runCommand/{labName}/{command}")
    public ResponseEntity<String> runCommand(@PathVariable("labName") String labName, @RequestParam String command) {
        try {
            return ResponseEntity.ok(labService.runCommandOnSelectedLab(labName, command));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/labs/status/{labName}")
    public ResponseEntity<String> getLabStatus(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(labService.getLabStatus(labName));
    }
}

