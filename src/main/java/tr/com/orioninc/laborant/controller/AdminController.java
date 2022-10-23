package tr.com.orioninc.laborant.controller;

import tr.com.orioninc.laborant.model.Lab;
import tr.com.orioninc.laborant.service.AdminService;
import tr.com.orioninc.laborant.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    LabService labService;

    @GetMapping( value = {"/addNewLab"} )
    public String addNewLab(Model model)
    {
        Lab newLab = new Lab();
        model.addAttribute("newLab", newLab);

        return "add_Lab_Form";
    }


    @PostMapping( value = {"/addNewLab"} )
    public String submitNewLab(Model model,@ModelAttribute("newLab") Lab lab,RedirectAttributes redirAttrs)
    {
       try{
           String newLabResult = adminService.addNewLab(lab.getLabName(),lab.getUserName(),
                   lab.getPassword(),lab.getHost(),lab.getPort());

           model.addAttribute("success",newLabResult);
           model.addAttribute("responseMessage", newLabResult);
           return   "add_Lab_Form";
       }
       catch (Exception e){
           String errorMessage = e.getMessage();
           redirAttrs.addFlashAttribute("error", errorMessage);
           model.addAttribute("errorMessage", "Please fill" +
                   " all the fields to add a lab to the database");

           return "add_Lab_Form";
       }
    }

    @GetMapping( value = {"/allLabs"})
    public String getAllLabs(Model model){
        List<Lab> allLabs = adminService.getAllLabs();
        model.addAttribute("labs",allLabs);
        List<String> labVersions = labService.getAllLabVersions();
        model.addAttribute("labVersions",labVersions);
        return "all_Labs";

    }

    @GetMapping( value = {"/deleteLab/{labName}"})
    public String deleteLab(Model model,@PathVariable String labName){
        String response = adminService.deleteLab(labName);
        model.addAttribute("message",response);
        return "redirect:/allLabs";
    }

}

/*
@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @PostMapping("/admin/addNewLab")
    public String addNewLab(@RequestParam String labName,@RequestParam String userName,
    @RequestParam String password,@RequestParam String host,@RequestParam Integer port){
        return adminService.addNewLab(labName,userName,password,host,port);
    }

    @GetMapping("/admin/getAllLabs")
    public List<Lab> getAllLabs(){
        return adminService.getAllLabs();
    }

    @DeleteMapping("/admin/deleteLab")
    public String getAllLabs(@RequestParam String labNameToBeDeleted){
        return adminService.deleteLab(labNameToBeDeleted);
    }


}
 */
