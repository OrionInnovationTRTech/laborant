package tr.com.orioninc.laborant.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.service.AdminService;

import java.util.List;

@RequestMapping("/api")
@AllArgsConstructor
@RestController
public class RestAdminController {
    private AdminService adminService;

    @GetMapping("/allLabs") // works fine
    public ResponseEntity<List<Lab>> getAllLabs() {
        return ResponseEntity.ok(adminService.getAllLabs());
    }
    @PostMapping("/addNewLab") //not sure yet, works fine with postman get request, Requestbody
    public ResponseEntity<String> addNewLab(@RequestBody Lab lab) {
        return ResponseEntity.ok(adminService.addNewLab(lab.getLabName(), lab.getUserName(),
                lab.getPassword(), lab.getHost(), lab.getPort()));
    }

    @DeleteMapping("/deleteLab/{labName}")
    public ResponseEntity<String> deleteLab(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(adminService.deleteLab(labName));
    }

    @PutMapping("/updateLab/{labName}")
    public ResponseEntity<String> updateLabByName(@PathVariable("labName") String labName, @RequestBody Lab lab) {
        return ResponseEntity.ok(adminService.updateLabByName(labName, lab.getUserName(), lab.getPassword(), lab.getHost(), lab.getPort()));
    }
    // deleteLabById can be added
    // updateLabById can be added
    // LabController likewise RestLabController can be added.
}
