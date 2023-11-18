package pl.noteally.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.noteally.data.User;
import pl.noteally.services.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @GetMapping("")
    public String getAllUser(Model model, HttpSession session) {
        List<User> userList=userService.getUsers();
        List<User> filteredUserList = new ArrayList<>();
        for (User user : userList) {
            if (!user.getId().equals(session.getAttribute("userId"))) {
                filteredUserList.add(user);
            }
        }
        model.addAttribute("userList", filteredUserList);
        return "admin";
    }

    @GetMapping("/ASC")
    public String sortUsersASC(Model model, HttpSession session) {
        List<User> userList=userService.getUsers();
        List<User> filteredUserList = new ArrayList<>();
        for (User user : userList) {
            if (!user.getId().equals(session.getAttribute("userId"))) {
                filteredUserList.add(user);
            }
        }
        filteredUserList.sort(Comparator.comparing(User::getName));
        model.addAttribute("userList", filteredUserList);
        return "admin";
    }
    @GetMapping("/DESC")
    public String sortUsersDESC(Model model, HttpSession session) {
        List<User> userList=userService.getUsers();
        List<User> filteredUserList = new ArrayList<>();
        for (User user : userList) {
            if (!user.getId().equals(session.getAttribute("userId"))) {
                filteredUserList.add(user);
            }
        }
        filteredUserList.sort(Comparator.comparing(User::getName).reversed());
        model.addAttribute("userList", filteredUserList);
        return "admin";
    }

    @GetMapping("/deleteUser/{userId}")
    public String delete(Model model, @PathVariable("userId") Integer userId, HttpSession session) {
        if((session.getAttribute("userId")).equals(userId))
        {
            return "redirect:/admin";
        }
        userService.deleteUserById(userId);
        return "redirect:/admin";
    }
    @GetMapping("/editUser/{userId}")
    public String redirectEdit(Model model, @PathVariable("userId") Integer userId, HttpSession session){
        if((session.getAttribute("userId")).equals(userId))
        {
            return "redirect:/admin";
        }
        Optional<User> user = userService.getUserById(userId);
        model.addAttribute("user", user.get());
        return "editUser";
    }

    @PostMapping("/editUser/{userId}")
    public String editUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,  @PathVariable("userId") Integer userId, Model model){

        if (bindingResult.hasErrors()) {
            user.setId(userId);
            model.addAttribute("user", user);
            model.addAttribute("errors",bindingResult);
            return "editUser";
        }
        userService.updateUser(user, userId);
        return "redirect:/admin";
    }
}
