package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.wolskak.mycomputerservice.controller.forms.ChangePasswordForm;
import pl.wolskak.mycomputerservice.controller.forms.ChangePasswordFormValidator;
import pl.wolskak.mycomputerservice.service.UserService;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class ChangePasswordController {

    private UserService userService;
    private ChangePasswordFormValidator passwordFormValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(passwordFormValidator);
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("passwordForm", new ChangePasswordForm());
        return "change_password_form";
    }

    @PostMapping("/changePassword")
    public String checkChangePasswordForm(@Valid @ModelAttribute("passwordForm") ChangePasswordForm changePasswordForm,
                                          BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "change_password_form";
        }

        try {
            userService.updatePassword(changePasswordForm.getNewPassword());
        } catch (UserNotFoundException userNotFound) {
            return "error/403";
        }

        if (request.isUserInRole("ROLE_CUSTOMER")) {
            return "redirect:/customer";
        } else if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin";
        } else if (request.isUserInRole("ROLE_REPAIRER")) {
            return "redirect:/repairer";
        } else {
            return "error/403";
        }
    }
}
