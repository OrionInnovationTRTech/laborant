package tr.com.orioninc.laborant.app.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.service.AdminService;

import java.util.List;

@RequestMapping("/v1")
@AllArgsConstructor
@RestController
@Log4j2
public class RestAdminController {
    private AdminService adminService;

    @GetMapping("/labs")
    public ResponseEntity<List<Lab>> getAllLabs() {
        return ResponseEntity.ok(adminService.getAllLabs());
    }
    @GetMapping("/labs/{labName}")
    public ResponseEntity<Lab> getLab(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(adminService.getLab(labName));
    }
    @PostMapping("/labs/{labName}")
    public ResponseEntity<String> addNewLab(@PathVariable String labName, @RequestBody Lab lab) {
        lab.setLabName(labName);
        Lab result = adminService.addNewLab(lab);
        if (result == null) {
            return ResponseEntity.badRequest().body("Lab already exists with name: " + labName + "\n" +
                    "Or there is already a pair in the database with username: " + lab.getUserName() + " and host: " + lab.getHost());
        }
        else {
            return ResponseEntity.ok("Lab: " + result.toString() + " added successfully");
        }
    }

    @DeleteMapping("/labs/{labName}")
    public ResponseEntity<String> deleteLab(@PathVariable("labName") String labName) {
        if (adminService.deleteLabByName(labName)) {
            log.info("Lab deleted");
            return ResponseEntity.ok("Lab named " + labName + " deleted");
        }
        else {
            log.info("Lab not found");
            return ResponseEntity.badRequest().body("Lab " + labName + " not found");
        }
    }

    @PutMapping("/labs/{labName}")
    public ResponseEntity<Lab> updateLabByName(@PathVariable("labName") String labName, @RequestBody Lab lab) {
        return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
    }
    // deleteLabById can be added
    // updateLabById can be added
}
