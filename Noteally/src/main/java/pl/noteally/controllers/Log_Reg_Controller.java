package pl.noteally.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import pl.noteally.domain._User;
import pl.noteally.services.UserService;

@Controller
@AllArgsConstructor
public class Log_Reg_Controller {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(Log_Reg_Controller.class);

    @GetMapping("/login")
    public String redirectLogin(HttpSession session){
        if(session.getAttribute("userId") != null)
        {
            logger.info("Log_Reg_Controller.redirectLogin(): User is already logged in. Redirecting to catalogs page.");
            return "redirect:/catalogs";
        }
        logger.info("Log_Reg_Controller.redirectLogin(): Redirecting to login page.");
        return "login";
    }

    @GetMapping("/register")
    public String redirectRegister(Model model, HttpSession session){
        if(session.getAttribute("userId") != null)
        {
            logger.info("Log_Reg_Controller.redirectRegister(): User is already logged in. Redirecting to catalogs page.");
            return "redirect:/catalogs";
        }

        model.addAttribute("user", new _User());
        logger.info("Log_Reg_Controller.redirectRegister(): Redirecting to register page.");
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") _User user, BindingResult bindingResult, @RequestParam ("confirmPassword") String confirmPassword,
                           HttpServletResponse response, Model model) {
        try {
            boolean errors = false;
            model.addAttribute("user", user);
            model.addAttribute("email", user.getEmail());
            model.addAttribute("name", user.getName());
            model.addAttribute("surname", user.getSurname());
            model.addAttribute("age", user.getAge());
            model.addAttribute("password", user.getPassword());

            if (userService.userExists(user)) {
                model.addAttribute("emailTaken", "This email is already taken");
                errors = true;
                logger.warn("Log_Reg_Controller.register(): Registration failed for user with email '{}': Email already exists.", user.getEmail());
            }
            if (!user.getPassword().equals(confirmPassword)) {
                model.addAttribute("confirmError", "Passwords are different");
                errors = true;
                logger.warn("Log_Reg_Controller.register(): Registration failed for user with email '{}': Passwords do not match.", user.getEmail());
            }
            if (bindingResult.hasErrors()) {
                model.addAttribute("errors", bindingResult);
                errors = true;
                logger.warn("Log_Reg_Controller.register(): Registration failed for user with email '{}': Validation errors.", user.getEmail());
            }
            if (errors) return "register";

            userService.signUpUser(user);

            Cookie catalogCookie = new Cookie("catalogCookie" + user.getId(), "default");
            catalogCookie.setMaxAge(60 * 60 * 24 * 365 * 10);
            Cookie noteCookie = new Cookie("noteCookie" + user.getId(), "default");
            noteCookie.setMaxAge(60 * 60 * 24 * 365 * 10);
            response.addCookie(catalogCookie);
            response.addCookie(noteCookie);

            logger.info("Log_Reg_Controller.register(): User registered successfully with email: {}", user.getEmail());
            return "login";
        } catch (Exception e) {
            logger.error("Log_Reg_Controller.register(): Error registering user: {}", e.getMessage());
            return "error";
        }
    }
}
