package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.wolskak.mycomputerservice.controller.forms.ComputerForm;
import pl.wolskak.mycomputerservice.controller.forms.ComputerFormValidator;
import pl.wolskak.mycomputerservice.model.Computer;
import pl.wolskak.mycomputerservice.model.ComputerDamage;
import pl.wolskak.mycomputerservice.service.ComputerService;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerDamageExistsException;
import pl.wolskak.mycomputerservice.service.exceptions.ComputerNotFoundException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor
public class ComputerController {

    private ComputerFormValidator computerFormValidator;
    private ComputerService computerService;
    private MessageSource messages;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(computerFormValidator);
    }

    @GetMapping("/customer/addComputer")
    public String addComputer(Model model) {
        ComputerForm computerForm = new ComputerForm();
        model.addAttribute("computerForm", computerForm);
        return "customer_add_computer_form";
    }

    @PostMapping("/customer/addComputer")
    public String checkAddComputerForm(@Valid @ModelAttribute("computerForm") ComputerForm computerForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "customer_add_computer_form";
        }

        computerService.createComputer(computerForm, UserUtils.getLoggedInUserName());
        return "redirect:/customer";
    }

    @GetMapping("/customer/myComputers")
    public String myComputers(Model model, @RequestParam(value = "comp_id_error", required = false) Integer computerId) {
        if (computerId != null) {
            model.addAttribute("error", messages.getMessage("delete.computer.error", null, null));
            model.addAttribute("comp_id_error", computerId);
        }

        List<Computer> computers = computerService.findUserComputers(UserUtils.getLoggedInUserName());
        model.addAttribute("computers", computers);
        return "customer_computers_list";
    }

    @GetMapping(value = "/customer/myComputers/{cid}")
    public String computerDetails(@PathVariable("cid") Integer computerId, Model model) {
        try {
            Computer computer = computerService.findComputerById(computerId);
            List<ComputerDamage> historyOfComputerDamages = computerService.findHistoryOfComputerDamages(computerId);
            model.addAttribute("historicalDamages", historyOfComputerDamages);
            model.addAttribute("computer", computer);
            return "computer_details";
        } catch (ComputerNotFoundException computerNotFound) {
            return "/error/404";
        } catch (UserNotAllowedException userNotAllowed) {
            return "/error/403";
        }
    }

    @GetMapping(value = "/customer/editComputer/{cid}")
    public String editComputer(@PathVariable("cid") Integer computerId, Model model) {
        try {
            Computer computer = computerService.findComputerById(computerId);
            ComputerForm computerForm = new ComputerForm(
                    computer.getId(),
                    computer.getType(),
                    computer.getBrand(),
                    computer.getModel(),
                    computer.getYearOfProduction(),
                    computer.getOperatingSystem(),
                    computer.getProcessor(),
                    computer.getGraphicsCard(),
                    computer.getRamMemory(),
                    null
            );
            model.addAttribute("computer", computerForm);
            return "customer_edit_computer_form";
        } catch (ComputerNotFoundException computerNotFound) {
            return "/error/404";
        } catch (UserNotAllowedException userNotAllowed) {
            return "/error/403";
        }
    }

    @PostMapping("/customer/editComputer/{cid}")
    public String checkEditComputerForm(@PathVariable("cid") Integer computerId, @Valid @ModelAttribute("computer") ComputerForm computerForm,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            computerForm.setId(computerId);
            return "customer_edit_computer_form";
        }

        computerService.updateComputer(computerId, computerForm, UserUtils.getLoggedInUserName());
        return "redirect:/customer";
    }

    @GetMapping("/customer/deleteComputer/{cid}")
    public String deleteComputer(@PathVariable("cid") Integer computerId) {
        try {
            computerService.deleteComputer(computerId);
            return "redirect:/customer";
        } catch (ComputerDamageExistsException computerDamageExistsException) {
            return "redirect:/customer/myComputers?comp_id_error=" + computerId;
        }
    }
}
