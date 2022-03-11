package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.wolskak.mycomputerservice.controller.forms.AssignComputerDamageForm;
import pl.wolskak.mycomputerservice.controller.forms.RepairerForm;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.service.AdminService;
import pl.wolskak.mycomputerservice.service.ComputerDamageService;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageNotFoundException;
import pl.wolskak.mycomputerservice.service.exceptions.RepairerHasActiveDamageException;
import pl.wolskak.mycomputerservice.service.exceptions.UserAlreadyExistsException;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class AdminController {

    private ComputerDamageService computerDamageService;
    private AdminService adminService;
    private MessageSource messages;

    @GetMapping("/admin")
    public String homeAdminPage() {
        return "home";
    }

    @GetMapping("/admin/addRepairer")
    public String addRepairer(Model model) {
        model.addAttribute("repairerForm", new RepairerForm());
        return "admin_add_repairer_form";
    }

    @PostMapping("/admin/addRepairer")
    public String checkAddRepairerForm(@Valid @ModelAttribute("repairerForm") RepairerForm repairerForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin_add_repairer_form";
        }

        try {
            adminService.createRepairerAccount(repairerForm);
        } catch (UserAlreadyExistsException userAlreadyExists) {
            model.addAttribute("addRepairerError", messages.getMessage("user.already.exists", null, null));
            return "add_repairer";
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/customers")
    public String customers(Model model) {
        model.addAttribute("customers", adminService.findAllCustomers());
        return "admin_customers_list";
    }

    @GetMapping("/admin/repairers")
    public String repairers(Model model, @RequestParam(value = "error", required = false) Boolean hasError) {
        if (hasError != null) {
            model.addAttribute("deleteRepairerError", messages.getMessage("delete.repairer.error", null, null));
        }

        model.addAttribute("repairers", adminService.findAllRepairers());
        return "admin_repairers_list";
    }

    @GetMapping("/admin/computerDamages")
    public String computerDamages(Model model) {
        List<ComputerDamage> repairingDamages = computerDamageService.findComputerDamagesByStatus(ComputerDamageStatus.ASSIGNED_TO_REPAIRER);
        List<ComputerDamage> notAcceptedDamages = computerDamageService.findComputerDamagesByStatus(ComputerDamageStatus.REPORTED);

        model.addAttribute("notAcceptedDamages", notAcceptedDamages);
        model.addAttribute("repairingDamages", repairingDamages);

        return "admin_computer_damages_list";
    }

    @GetMapping("/admin/assignComputerDamage/{did}")
    public String assignComputerDamage(@PathVariable("did") Integer damageId, Model model) {
        model.addAttribute("assignComputerDamageForm", new AssignComputerDamageForm(damageId));
        model.addAttribute("repairers", adminService.findAllRepairers());
        return "admin_assign_damage_form";
    }

    @PostMapping("/admin/assignComputerDamage/{did}")
    public String checkAssignComputerDamage(@PathVariable("did") Integer damageId,
                                       @Valid @ModelAttribute("assignComputerDamageForm") AssignComputerDamageForm assignComputerDamageForm,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin_assign_damage_form";
        }

        try {
            computerDamageService.assignComputerDamage(damageId, assignComputerDamageForm.getRepairerId());
        } catch (ComputerDamageNotFoundException computerDamageNotFound) {
            return "error/404";
        }

        return "redirect:/admin/computerDamages";
    }

    @GetMapping("/admin/deleteComputerDamage/{did}")
    public String deleteComputerDamage(@PathVariable("did") Integer damageId) {
        computerDamageService.adminDeleteComputerDamage(damageId);
        return "redirect:/admin/computerDamages";
    }

    @GetMapping("/admin/deleteCustomer/{uid}")
    public String deleteCustomer(@PathVariable("uid") Integer userId) {
        adminService.deleteUser(userId);
        return "redirect:/admin/customers";
    }

    @GetMapping("/admin/deleteRepairer/{uid}")
    public String deleteRepairer(@PathVariable("uid") Integer userId) {
        try {
            adminService.deleteRepairer(userId);
        } catch (RepairerHasActiveDamageException exception) {
            return "redirect:/admin/repairers?error=true";
        }
        return "redirect:/admin/repairers";
    }
}
