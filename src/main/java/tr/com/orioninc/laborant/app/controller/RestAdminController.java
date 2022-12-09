package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "Getting all labs as a list of Lab objects")
    public ResponseEntity<List<Lab>> getAllLabs() {
        log.info("[getAllLabs] Getting all labs");
        return ResponseEntity.ok(adminService.getAllLabs());
    }

    @GetMapping("/labs/{labName}")
    @ApiOperation(value = "Getting a lab by giving 'labName' as a path variable")
    public ResponseEntity<Lab> getLab(@PathVariable("labName") String labName) {
        log.info("[getLab] Getting lab with name {}", labName);
        return ResponseEntity.ok(adminService.getLab(labName));
    }

    @PostMapping("/labs/add/")
    @ApiOperation(value = "Adding a lab to database by giving Lab in body")
    public ResponseEntity<Lab> addNewLab(@RequestBody Lab lab) {
        log.info("[addNewLab] Called with labName: {}", lab.getLabName());
        return ResponseEntity.ok(adminService.addNewLab(lab));
    }

    @DeleteMapping("/labs/{labName}")
    @ApiOperation(value = "Deleting a lab from database by giving 'labName' as a path variable")
    public ResponseEntity<Lab> deleteLab(@PathVariable("labName") String labName) {
        log.info("[deleteLab] Called with labName: {}", labName);
        return ResponseEntity.ok(adminService.deleteLabByName(labName));
    }

    @PutMapping("/labs/{labName}")
    @ApiOperation(value = "Updating a lab in database by giving 'labName' as a path variable and Lab in body")
    public ResponseEntity<Lab> updateLabByName(@PathVariable("labName") String labName, @RequestBody Lab lab) {
        log.info("[updateLabByName] Called with labName: {}", labName);
        return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
    }
}
