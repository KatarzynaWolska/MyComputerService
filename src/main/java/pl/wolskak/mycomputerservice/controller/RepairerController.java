package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.Repair;
import pl.wolskak.mycomputerservice.service.RepairerService;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageNotFoundException;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import java.util.List;

@Controller
@AllArgsConstructor
public class RepairerController {

    private RepairerService repairerService;

    @GetMapping("/repairer")
    public String homeRepairerPage(Model model) {
        List<Repair> cancelledRepairs = repairerService.findRepairsCancelledByCustomer(UserUtils.getLoggedInUserName());
        model.addAttribute("cancelledRepairs", cancelledRepairs);
        return "home";
    }

    @GetMapping("/repairer/computerDamages")
    public String repairersDamages(Model model) {
        List<ComputerDamage> computerDamages = repairerService.getDamagesAssignedToRepairer(UserUtils.getLoggedInUserName());
        List<ComputerDamage> pendingDamages = repairerService.getPendingDamagesAssignedToRepairer(UserUtils.getLoggedInUserName());
        List<ComputerDamage> acceptedComputerDamages = repairerService.getAcceptedDamagesAssignedToRepairer(UserUtils.getLoggedInUserName());
        model.addAttribute("computerDamages", computerDamages);
        model.addAttribute("pendingDamages", pendingDamages);
        model.addAttribute("acceptedComputerDamages", acceptedComputerDamages);
        return "repairer_computer_damages_list";
    }

    @GetMapping("/repairer/addRemarksToComputerDamage/{did}")
    public String addRemarksToComputerDamageForm(Model model, @PathVariable("did") Integer damageId) {
        model.addAttribute("damageId", damageId);
        model.addAttribute("repairerRemarks", "");
        model.addAttribute("price", "");
        return "repairer_add_remarks_form";
    }

    @PostMapping("/repairer/addRemarksToComputerDamage/{did}")
    public String addRemarksToComputerDamage(@RequestParam("repairerRemarks") String repairerRemarks,
                                             @RequestParam("price") String price,
                                             @PathVariable("did") Integer damageId) {
        repairerService.addRemarksToComputerDamage(damageId, repairerRemarks, price);
        return "redirect:/repairer/computerDamages";
    }

    @GetMapping("/repairer/reportFixOfComputerDamage/{did}")
    public String reportFixOfComputerDamage(@PathVariable("did") Integer damageId) {
        repairerService.createInvoice(damageId);
        repairerService.reportFixOfComputerDamage(damageId);
        return "redirect:/repairer/computerDamages";
    }

    @GetMapping("/repairer/reportImpossibleFixOfComputerDamage/{did}")
    public String reportImpossibleFixOfComputerDamage(@PathVariable("did") Integer damageId) {
        repairerService.reportImpossibleFixOfComputerDamage(damageId);
        return "redirect:/repairer/computerDamages";
    }

    @GetMapping(value = "/repairer/computerDamages/{did}")
    public String computerDamageDetails(@PathVariable("did") Integer damageId, Model model) {
        try {
            ComputerDamage computerDamage = repairerService.findComputerDamageById(damageId);
            model.addAttribute("damage", computerDamage);
            return "computer_damage_details";
        } catch (ComputerDamageNotFoundException computerDamageNotFound) {
            return "/error/404";
        }
    }

    @GetMapping("/repairer/confirmNotification/{rid}")
    public String confirmNotification(@PathVariable("rid") Integer repairId) {
        repairerService.confirmNotification(repairId);
        return "redirect:/repairer";
    }
}
