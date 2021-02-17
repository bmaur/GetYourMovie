package com.finalproject.filmweb.user;

import com.finalproject.filmweb.user.exception.*;
import com.finalproject.filmweb.user.model.UserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController implements ErrorController {

    private final UserService userService;
    private final MailService mailService;

    @Value("${domain.url}")
    private String domainUrl = "";

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @PostMapping("/home")
    public String pHome() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/error")
    public String handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {

            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";
            }
        }
        return "errorPage";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerFormView(Model model) {
        model.addAttribute("userInput", new UserInput());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistrationForm(@ModelAttribute("userInput") @Valid UserInput userInput,
                                          BindingResult bindingResult,
                                          Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.addNewUser(userInput);
        } catch (UserAlreadyExistException e) {
            FieldError userNameObjectError = new FieldError("userName", "userName", e.getMessage());
            bindingResult.addError(userNameObjectError);
            return "register";
        } catch (UserNickAlreadyExistException e) {
            FieldError userNickObjectError = new FieldError("userNick", "userNick", e.getMessage());
            bindingResult.addError(userNickObjectError);
            return "register";

        } catch (PasswordNotConfirmedException e) {
            FieldError passwordObjectError = new FieldError("userPassword", "userPassword", e.getMessage());
            bindingResult.addError(passwordObjectError);
            return "register";
        }

        String confirmationToken = userService.findByEmail(userInput.getUserName()).get().getConfirmationToken();
        mailService.sendRegisterEmail(userInput.getUserName(), domainUrl, confirmationToken);
        model.addAttribute("afterRegisterPage", userInput.getUserName());


        return "afterRegisterPage";
    }

    @RequestMapping(value = "/firstTimeLogin", method = RequestMethod.GET)
    public String showConfirmationPage(Model model, @RequestParam("token") String token) {
        UserEntity user = userService.findByConfirmationToken(token).get();
        if (user == null) {
            model.addAttribute("invalidToken", "Oops!  This is an invalid confirmation link.");
        } else {
            model.addAttribute("confirmationToken", user.getConfirmationToken());
            userService.setTrueForUserEnabledAndSave(user);
        }
        return "login";
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public String forgotPasswordView(Model model) {
        model.addAttribute("userEmail", new UserInput());
        return "forgotPassword";
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public String forgotPassword(@ModelAttribute("userEmail") @Valid UserInput userInput, BindingResult bindingResult) {

        if (userInput.getUserName().equals("")) {
            FieldError userNameObjectError = new FieldError("userName", "userName", "Please enter your e-mail address");
            bindingResult.addError(userNameObjectError);
            return "forgotPassword";
        }

        try {
            System.out.println(userInput.getUserName());
            userService.newPassword(userInput.getUserName());
        } catch (UserNotFoundException e) {
            FieldError userNameObjectError = new FieldError("userName", "userName", e.getMessage());
            bindingResult.addError(userNameObjectError);
            return "forgotPassword";
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String passwordResetFormView(Model model) {
        model.addAttribute("userResetForm", new UserInput());
        return "reset";
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public String reset(@ModelAttribute("userResetForm") @Valid UserInput userResetForm, BindingResult bindingResult, Principal principal) {
        userResetForm.setUserName(principal.getName());
        try {
            userService.updateUserPassword(userResetForm);
        } catch (WrongUserPasswordException e) {
            FieldError userPasswordObjectError = new FieldError("userPassword", "userPassword", e.getMessage());
            bindingResult.addError(userPasswordObjectError);
            return "reset";
        }

        return "afterResetPasswordPage";
    }

    @RequestMapping(value = "/userNickReset", method = RequestMethod.GET)
    public String userNickResetFormView(Model model) {
        model.addAttribute("userNickResetForm", new UserInput());
        return "userNickReset";
    }

    @RequestMapping(value = "/userNickReset", method = RequestMethod.POST)
    public String userNickReset(@ModelAttribute("userNickResetForm") @Valid UserInput userNickResetForm, BindingResult bindingResult, Principal principal) {
        userNickResetForm.setUserName(principal.getName());
        try {
            userService.updateUserNick(userNickResetForm);
        } catch (UserNickAlreadyExistException e) {
            FieldError userNickObjectError = new FieldError("userNick", "userNick", e.getMessage());
            bindingResult.addError(userNickObjectError);
            return "userNickReset";
        }

        return "redirect:/settings";
    }

    @RequestMapping(value = "/accountInformation", method = RequestMethod.GET)
    public String showAccountInformation(Principal principal, Model model) {
        Optional<UserEntity> userInformation = userService.showUserInformation(principal.getName());
        model.addAttribute("accountInfo", userInformation.get());
        return "accountInformation";
    }

    @GetMapping("/settings")
    public String settings() {
        return "settings";
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
