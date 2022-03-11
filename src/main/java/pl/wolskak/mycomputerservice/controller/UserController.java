package pl.wolskak.mycomputerservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.wolskak.mycomputerservice.controller.forms.EditUserForm;
import pl.wolskak.mycomputerservice.model.User;
import pl.wolskak.mycomputerservice.service.UserService;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotAllowedException;
import pl.wolskak.mycomputerservice.service.exceptions.UserNotFoundException;
import pl.wolskak.mycomputerservice.utils.UserUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/userInfo")
    public String userInfo(Model model) {
        try {
            User user = userService.findCurrentLoggedInUserInfo();
            model.addAttribute("userInfo", user);
            return "user_info";
        } catch (UserNotAllowedException userNotAllowedException) {
            return "/error/403";
        } catch (UserNotFoundException userNotFoundException) {
            return "/error/404";
        }
    }

    @GetMapping("/editUser")
    public String editUser(Model model) {
        Optional<User> optionalUser = userService.findUserByEmail(UserUtils.getLoggedInUserName());

        if (!optionalUser.isPresent()) {
            return "error/404";
        } else {
            User user = optionalUser.get();

            EditUserForm editUserForm = new EditUserForm(
                    user.getName(),
                    user.getSurname(),
                    user.getEmail(),
                    user.getPhoneNumber()
            );

            model.addAttribute("userForm", editUserForm);
            return "edit_user_info";
        }
    }

    @PostMapping("/editUser")
    public String checkEditUser(@Valid @ModelAttribute("userForm") EditUserForm editUserForm,
                                BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "edit_user_info";
        }

        try {
            userService.updateUser(UserUtils.getLoggedInUserName(), editUserForm);
        } catch (UserNotAllowedException userNotAllowed) {
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
