package pl.noteally.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.noteally.data.User;
import pl.noteally.services.RestNameService;
import pl.noteally.services.UserService;

@Controller
@AllArgsConstructor
public class Log_Reg_Controller {

    private final UserService userService;

    private final RestNameService restNameService;

    @GetMapping("/login")
    public String redirectLogin(HttpSession session){
        if(session.getAttribute("userId") != null)
        {
            return "redirect:/catalogs";
        }
        return "login";
    }

    @GetMapping("/register")
    public String redirectRegister(Model model, HttpSession session){
        if(session.getAttribute("userId") != null)
        {
            return "redirect:/catalogs";
        }

        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam ("confirmPassword") String confirmPassword,
                           HttpServletResponse response, Model model) {
        boolean errors = false;
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("surname", user.getSurname());
        model.addAttribute("age", user.getAge());
        model.addAttribute("password", user.getPassword());

        if(userService.userExists(user)) {
            model.addAttribute("usernameTaken", "This username is already taken");
            errors = true;
        }
        if(!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("confirmError", "Passwords are different");
            errors = true;
        }
        if(!restNameService.isNamePolish(user.getName())) {
            model.addAttribute("polishNameError", "Name must be Polish");
            errors = true;
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            errors = true;
        }
        if(errors) return "register";

        userService.signUpUser(user);

        Cookie catalogCookie = new Cookie("catalogCookie" + user.getId(), "default");
        catalogCookie.setMaxAge(60 * 60 * 24 * 365 * 10);
        Cookie noteCookie = new Cookie("noteCookie" + user.getId(), "default");
        noteCookie.setMaxAge(60 * 60 * 24 * 365 * 10);
        response.addCookie(catalogCookie);
        response.addCookie(noteCookie);

        return "login";
    }
}
