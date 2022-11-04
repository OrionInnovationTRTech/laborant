package tr.com.orioninc.laborant.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.service.LabService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RestLabController {

    private LabService labService;

    @GetMapping("/lab/runCommand/{labName}")
    public ResponseEntity<String> runCommand(@PathVariable("labName") String labName, @RequestParam String command) {
        try {
            return ResponseEntity.ok(labService.runCommandOnSelectedLab(labName, command));
        } catch (Exception e) {             // not sure yet
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
@Test
