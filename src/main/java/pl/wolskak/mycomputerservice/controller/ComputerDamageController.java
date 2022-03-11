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
import pl.wolskak.mycomputerservice.controller.forms.ComputerDamageForm;
import pl.wolskak.mycomputerservice.model.Computer;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.model.ComputerDamageStatus;
import pl.wolskak.mycomputerservice.service.ComputerDamageService;
import pl.wolskak.mycomputerservice.service.ComputerService;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageExistsException;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageIsAcceptedException;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageNotFoundException;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerNotFoundException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class ComputerDamageController {

    private ComputerService computerService;
    private ComputerDamageService computerDamageService;
    private MessageSource messages;

    @GetMapping("/customer/reportComputerDamage")
    public String reportComputerDamage(Model model) {
        ComputerDamageForm computerDamageForm = new ComputerDamageForm();
        List<Computer> userComputers = computerService.findUserComputers(UserUtils.getLoggedInUserName());

        model.addAttribute("computerDamageForm", computerDamageForm);
        model.addAttribute("userComputers", userComputers);
        return "customer_report_computer_damage_form";
    }

    @PostMapping("/customer/reportComputerDamage")
    public String checkComputerDamageForm(Model model, @Valid @ModelAttribute("computerDamageForm") ComputerDamageForm computerDamageForm,
                                     BindingResult bindingResult) {
        List<Computer> userComputers = computerService.findUserComputers(UserUtils.getLoggedInUserName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("userComputers", userComputers);
            return "customer_report_computer_damage_form";
        }

        try {
            computerDamageService.reportComputerDamage(computerDamageForm);
        } catch (ComputerDamageExistsException alreadyExists) {
            model.addAttribute("userComputers", userComputers);
            model.addAttribute("computerDamageError", messages.getMessage("damage.already.exists", null, null));
            model.addAttribute("computerDamageForm", computerDamageForm);
            return "customer_report_computer_damage_form";
        }

        return "redirect:/customer";
    }

    @GetMapping("/customer/computerDamages")
    public String computerDamages(Model model, @RequestParam(value = "damage_id_error", required = false) Integer damageId) {
        if (damageId != null) {
            model.addAttribute("error", messages.getMessage("delete.damage.error", null, null));
            model.addAttribute("damage_id_error", damageId);
        }
        List<ComputerDamage> damages = computerDamageService.findUserActiveComputerDamages(UserUtils.getLoggedInUserName());
        List<ComputerDamage> pendingDamages = computerDamageService.findComputerDamagesByStatusAndCustomer(
                ComputerDamageStatus.PRICED, UserUtils.getLoggedInUserName());
        model.addAttribute("damages", damages);
        model.addAttribute("pendingDamages", pendingDamages);
        return "customer_computer_damages_list";
    }

    @GetMapping(value = "/customer/computerDamages/{did}")
    public String computerDamageDetails(@PathVariable("did") Integer damageId, Model model) {
        try {
            ComputerDamage computerDamage = computerDamageService.findComputerDamageById(damageId);
            model.addAttribute("damage", computerDamage);
            return "computer_damage_details";
        } catch (ComputerDamageNotFoundException computerDamageNotFound) {
            return "/error/404";
        } catch (UserNotAllowedException userNotAllowed) {
            return "/error/403";
        }
    }

    @GetMapping(value = "/customer/editComputerDamage/{did}")
    public String editComputerDamage(@PathVariable("did") Integer computerDamageId, Model model) {
        try {
            ComputerDamage computerDamage = computerDamageService.findComputerDamageById(computerDamageId);

            if (computerDamage.getStatus() != ComputerDamageStatus.REPORTED) {
                return "redirect:/customer/computerDamages?damage_id_error=" + computerDamage.getId();
            }

            ComputerDamageForm computerDamageForm = new ComputerDamageForm(
                    computerDamage.getId(),
                    computerDamage.getComputer().getId(),
                    computerDamage.getTopic(),
                    computerDamage.getDescription(),
                    null
            );

            model.addAttribute("userComputers", computerDamage.getComputer());
            model.addAttribute("computerDamage", computerDamageForm);
            return "edit_computer_damage";
        } catch (ComputerNotFoundException computerNotFound) {
            return "/error/404";
        } catch (UserNotAllowedException userNotAllowed) {
            return "/error/403";
        }
    }

    @PostMapping("/customer/editComputerDamage/{did}")
    public String checkEditComputerDamageForm(@PathVariable("did") Integer computerDamageId,
                                              @Valid @ModelAttribute("computerDamage") ComputerDamageForm computerDamageForm,
                                              BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            ComputerDamage computerDamage = computerDamageService.findComputerDamageById(computerDamageId);
            computerDamageForm.setId(computerDamageId);
            model.addAttribute("userComputers", computerDamage.getComputer());
            return "edit_computer_damage";
        }

        computerDamageService.editComputerDamage(computerDamageId, computerDamageForm);
        return "redirect:/customer";
    }

    @GetMapping("/customer/deleteComputerDamage/{did}")
    public String deleteComputerDamage(@PathVariable("did") Integer damageId) {
        try {
            computerDamageService.deleteComputerDamage(damageId, UserUtils.getLoggedInUserName());
        } catch (ComputerDamageIsAcceptedException exception) {
            return "redirect:/customer/computerDamages?damage_id_error=" + damageId;
        } catch (UserNotAllowedException exception) {
            return "/error/403";
        }
        return "redirect:/customer/computerDamages";
    }

    @GetMapping("/customer/acceptComputerDamage/{did}")
    public String acceptComputerDamage(@PathVariable("did") Integer damageId) {
        try {
            computerDamageService.acceptComputerDamage(damageId, UserUtils.getLoggedInUserName());
        } catch (ComputerDamageNotFoundException exception) {
            return "/error/404";
        } catch (UserNotAllowedException exception) {
            return "/error/403";
        }
        return "redirect:/customer/computerDamages";
    }

    @GetMapping("/customer/cancelComputerDamage/{did}")
    public String cancelComputerDamage(@PathVariable("did") Integer damageId) {
        try {
            computerDamageService.cancelComputerDamage(damageId, UserUtils.getLoggedInUserName());
        } catch (ComputerDamageNotFoundException exception) {
            return "/error/404";
        } catch (UserNotAllowedException exception) {
            return "/error/403";
        }
        return "redirect:/customer/computerDamages";
    }
}
