package tr.com.orioninc.laborant.app.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.orioninc.laborant.app.service.LabService;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
@Log4j2
public class RestLabController {

    private LabService labService;

    @GetMapping("/labs/status/{labName}")
    @ApiOperation(value = "Running a command on a lab by giving 'labName' as a path variable")
    public ResponseEntity<String> runCommand(@PathVariable("labName") String labName) {
            log.info("[runCommand] Running status command on lab {}", labName);
            return ResponseEntity.ok(labService.runStatusOnSelectedLab(labName));
    }

}

