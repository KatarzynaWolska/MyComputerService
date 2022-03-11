package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.wolskak.mycomputerservice.controller.forms.RegistrationForm;
import pl.wolskak.mycomputerservice.controller.forms.RegistrationFormValidator;
import pl.wolskak.mycomputerservice.model.Customer;
import pl.wolskak.mycomputerservice.model.User;
import pl.wolskak.mycomputerservice.model.VerificationToken;
import pl.wolskak.mycomputerservice.security.OnRegistrationCompleteEvent;
import pl.wolskak.mycomputerservice.service.UserService;
import pl.wolskak.mycomputerservice.service.exceptions.UserAlreadyExistsException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@AllArgsConstructor
public class RegisterController {

    private RegistrationFormValidator validator;
    private UserService userService;
    private ApplicationEventPublisher eventPublisher;
    private MessageSource messages;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(validator);
    }

    @GetMapping("/register")
    public String register(Model model) {
        RegistrationForm registrationForm = new RegistrationForm();
        model.addAttribute("registrationForm", registrationForm);
        return "register";
    }

    @PostMapping("/register")
    public String checkRegistrationForm(Model model, @Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                                        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            Customer registered = (Customer) userService.createCustomerAccount(registrationForm);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
        } catch (UserAlreadyExistsException e) {
            return redirectToRegisterWithError(messages.getMessage("user.already.exists", null, null), model);
        }

        return "redirect:/activateAccount";
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(Model model, @RequestParam("token") String token) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            return redirectToLoginWithError(messages.getMessage("invalid.token", null, null), model);
        }

        User user = verificationToken.getUser();

        if (!verificationToken.checkTokenValidity()) {
            return redirectToLoginWithError(messages.getMessage("token.expired", null, null), model);
        }

        user.setActive(true);
        userService.saveRegisteredUser(user);
        return "redirect:/login";
    }

    private String redirectToRegisterWithError(String error, Model model) {
        model.addAttribute("registerError", error);
        return "register";
    }

    private String redirectToLoginWithError(String error, Model model) {
        model.addAttribute("loginError", error);
        return "login";
    }
}
