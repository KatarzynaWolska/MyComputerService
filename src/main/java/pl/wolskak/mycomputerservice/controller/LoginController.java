package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@AllArgsConstructor
public class LoginController {

    private MessageSource messages;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/activateAccount")
    public String loginWithActivateAccountInfo(Model model) {
        model.addAttribute("activateAccountInfo",
                    messages.getMessage("activate.account", null, null));
        return "login";
    }

    @GetMapping("/loginError")
    public String loginError(Model model) {
        model.addAttribute("loginError", messages.getMessage("login.error", null, null));
        return "login";
    }

    @GetMapping("/default")
    public String redirectAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin";
        } else if (request.isUserInRole("ROLE_REPAIRER")) {
            return "redirect:/repairer";
        }

        return "redirect:/customer";
    }
}
