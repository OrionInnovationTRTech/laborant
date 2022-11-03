package tr.com.orioninc.laborant.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.service.AdminService;
import tr.com.orioninc.laborant.service.LabService;

import java.util.List;

@Controller
@Log4j2
@AllArgsConstructor
public class AdminController {

    AdminService adminService;
    LabService labService;

    @GetMapping(value = { "/addNewLab" })
    public String addNewLab(Model model) {
        log.debug("[addNewLab] @GetMapping /addNewLab method is called");
        Lab newLab = new Lab();
        model.addAttribute("newLab", newLab);
        return "add_Lab_Form";
    }

    @PostMapping(value = { "/addNewLab" })
    public String submitNewLab(Model model, @ModelAttribute("newLab") Lab lab, RedirectAttributes redirAttrs) {
        log.debug("[submitNewLab] @PostMapping /addNewLab method is called");
        try {
            String newLabResult = adminService.addNewLab(lab.getLabName(), lab.getUserName(),
                    lab.getPassword(), lab.getHost(), lab.getPort());

            model.addAttribute("success", newLabResult);
            model.addAttribute("responseMessage", newLabResult);
            return "add_Lab_Form";
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            log.error("[submitNewLab] @PostMapping error: {}", e.getMessage(), e);
            redirAttrs.addFlashAttribute("error", errorMessage);
            model.addAttribute("errorMessage", "Please fill" +
                    " all the fields to add a lab to the database");
            return "add_Lab_Form";
        }
    }

    @GetMapping(value = { "/allLabs" })
    public String getAllLabs(Model model) {
        log.debug("[getAllLabs] @GetMapping /allLabs method is called");
        List<Lab> allLabs = adminService.getAllLabs();
        model.addAttribute("labs", allLabs);
        List<String> labVersions = labService.getAllLabVersions();
        model.addAttribute("labVersions", labVersions);
        return "all_Labs";

    }

    @GetMapping(value = { "/deleteLab/{labName}" })
    public String deleteLab(Model model, @PathVariable String labName) {
        log.debug("[deleteLab] @GetMapping /deleteLab method is called");
        String response = adminService.deleteLab(labName);
        model.addAttribute("message", response);
        return "redirect:/allLabs";
    }

}
