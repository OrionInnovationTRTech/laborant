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

    @GetMapping("/allLabs")
    public ResponseEntity<List<Lab>> getAllLabs() {
        return ResponseEntity.ok(adminService.getAllLabs());
    }

    @PostMapping("/addNewLab")
    public ResponseEntity<String> addNewLab(Lab lab) {
        return ResponseEntity.ok(adminService.addNewLab(lab.getLabName(), lab.getUserName(),
                lab.getPassword(), lab.getHost(), lab.getPort()));
    }

    @DeleteMapping("/deleteLab")
    public ResponseEntity<String> deleteLab(@PathVariable("labName") String labName) {
        return ResponseEntity.ok(adminService.deleteLab(labName));
    }
}
