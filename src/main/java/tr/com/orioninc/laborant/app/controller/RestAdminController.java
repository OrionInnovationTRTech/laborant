package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.model.Lab;
import tr.com.orioninc.laborant.app.service.AdminService;
import tr.com.orioninc.laborant.exception.AlreadyExists;
import tr.com.orioninc.laborant.exception.NotFound;

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
        if (adminService.getAllLabs().isEmpty()) {
            log.warn("[getAllLabs] No lab found in database");
            throw new NotFound("There is no lab in database");
        }
        else {
            log.info("[getAllLabs] All labs are listed");
            return ResponseEntity.ok(adminService.getAllLabs());
        }
    }
    @GetMapping("/labs/{labName}")
    @ApiOperation(value = "Getting a lab by giving 'labName' as a path variable")
    public ResponseEntity<Lab> getLab(@PathVariable("labName") String labName) {
        if (adminService.getLab(labName) == null) {
            log.warn("[getLab] Lab with name {} not found in database", labName);
            throw new NotFound("Lab named " + labName + " not found");
        }
        else {
            log.info("[getLab] Lab with name {} is returned", labName);
            return ResponseEntity.ok(adminService.getLab(labName));
        }
    }
    @PostMapping("/labs/add/")
    @ApiOperation(value = "Adding a lab to database by giving Lab in body")
    public ResponseEntity<Lab> addNewLab(@RequestBody Lab lab) {
        lab.setLabName(lab.getLabName());
        Lab result = adminService.addNewLab(lab);
        if (result == null) {
            log.info("[addNewLab] Already exists name or host - username pair in database");
            throw new AlreadyExists("Lab with name " + lab.getLabName() + " already exists or there is already a lab with pair of username: " +
                    lab.getHost() +  " and host: " + lab.getHost());
        }
        else {
            log.info("[addNewLab] Lab with name {} is added to database", lab.getLabName());
            return ResponseEntity.ok(result);
        }
    }

    @DeleteMapping("/labs/{labName}")
    @ApiOperation(value = "Deleting a lab from database by giving 'labName' as a path variable")
    public ResponseEntity<Lab> deleteLab(@PathVariable("labName") String labName) {
        if (adminService.findLabByName(labName)!=null) {
            log.info("[deleteLab] Lab named {} deleted", labName);
            return ResponseEntity.ok(adminService.deleteLabByName(labName));
        }
        else {
            log.warn("[deleteLab] Lab named " + labName + " not found");
            throw new NotFound("Lab named " + labName + " not found");
        }
    }

    @PutMapping("/labs/{labName}")
    @ApiOperation(value = "Updating a lab in database by giving 'labName' as a path variable and Lab in body")
        public ResponseEntity<Lab> updateLabByName(@PathVariable("labName") String labName, @RequestBody Lab lab) {
        if (adminService.findLabByName(labName)!=null) {
            log.info("[updateLabByName] Lab named {} updated", labName);
            return ResponseEntity.ok(adminService.updateLabByName(labName, lab));
        }
        else {
            log.warn("[updateLabByName] Lab named " + labName + " not found");
            throw new NotFound("Lab named " + labName + " not found");
        }
    }
}
