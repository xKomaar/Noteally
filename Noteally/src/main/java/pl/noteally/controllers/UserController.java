package pl.noteally.controllers;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pl.noteally.data.User;
import pl.noteally.services.UserService;

import java.util.List;
@Controller
@AllArgsConstructor
public class UserController {
    final private UserService userService;

    @GetMapping("users")
    public List<User> getUsers() {
        return userService.getUsers();
    }

}

