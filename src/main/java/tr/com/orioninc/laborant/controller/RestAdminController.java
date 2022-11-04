package tr.com.orioninc.laborant.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.service.AdminService;

import java.util.List;

@RequestMapping("/v1")
@AllArgsConstructor
@RestController
public class RestAdminController {
    private AdminService adminService;

    @GetMapping("/labs/")
    public ResponseEntity<List<Lab>> getAllLabs() {
        return ResponseEntity.ok(adminService.getAllLabs());
    }
    @GetMapping("/labs/{labName}")
    public ResponseEntity<Lab> getLab(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(adminService.getLab(labName));
    }
    @PostMapping("/labs/{labName}")
    public ResponseEntity<String> addNewLab(@PathVariable String labName, @RequestBody Lab lab) {
        return ResponseEntity.ok(adminService.addNewLab(labName, lab.getUserName(),
                lab.getPassword(), lab.getHost(), lab.getPort()));
    }

    @DeleteMapping("/labs/{labName}")
    public ResponseEntity<String> deleteLab(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(adminService.deleteLab(labName));
    }

    @PutMapping("/labs/{labName}")
    public ResponseEntity<String> updateLabByName(@PathVariable("labName") String labName, @RequestBody Lab lab) {
        return ResponseEntity.ok(adminService.updateLabByName(labName, lab.getUserName(), lab.getPassword(), lab.getHost(), lab.getPort()));
    }
    // deleteLabById can be added
    // updateLabById can be added
    // LabController likewise RestLabController can be added.
}
