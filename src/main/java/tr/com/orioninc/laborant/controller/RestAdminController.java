package tr.com.orioninc.laborant.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.service.AdminService;
import tr.com.orioninc.laborant.service.LabService;

import java.util.List;

@RequestMapping("/api")
@AllArgsConstructor
@RestController
public class RestAdminController {
    AdminService adminService;
    LabService labService;

    @GetMapping("/allLabs") // works fine
    public ResponseEntity<List<Lab>> getAllLabs() {
        return ResponseEntity.ok(adminService.getAllLabs());
    }
    @GetMapping("/addNewLab") //not sure yet, works fine with postman get request
    public ResponseEntity<Object> addNewLab(@RequestBody Lab lab) {
        return ResponseEntity.ok(adminService.addNew(lab));
    }

    @DeleteMapping("/deleteLab/{labName}")
    public ResponseEntity<String> deleteLab(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(adminService.deleteLab(labName));
    }

    @PutMapping("/updateLab/{labName}")
    public ResponseEntity<String> updateLab(@PathVariable("labName") String labName, @RequestBody Lab lab) {
        return ResponseEntity.ok(adminService.updateLab(labName, lab));
    }
    // deleteLabById can be added
    // updateLabById can be added
    // LabController likewise RestLabController can be added.
}
