package pl.noteally.controllers;

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

import java.util.Comparator;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("")
    public String getAllUser(Model model, HttpSession session) {
        try {
            List<_User> userList = userService.getUserListWithoutSelf(session);
            model.addAttribute("userList", userList);
            logger.info("AdminController.getAllUser(): Fetched all users successfully. User count: {}", userList.size());
            return "admin";
        } catch (Exception e) {
            logger.error("AdminController.getAllUser(): Error fetching all users: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/ASC")
    public String sortUsersASC(Model model, HttpSession session) {
        try {
            List<_User> userList = userService.getUserListWithoutSelf(session);
            userList.sort(Comparator.comparing(_User::getName));
            model.addAttribute("userList", userList);
            logger.info("AdminController.sortUsersASC(): Sorted users in ascending order. User count: {}", userList.size());
            return "admin";
        } catch (Exception e) {
            logger.error("AdminController.sortUsersASC(): Error sorting users in ascending order: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/DESC")
    public String sortUsersDESC(Model model, HttpSession session) {
        try {
            List<_User> userList = userService.getUserListWithoutSelf(session);
            userList.sort(Comparator.comparing(_User::getName).reversed());
            model.addAttribute("userList", userList);
            logger.info("AdminController.sortUsersDESC(): Sorted users in descending order. User count: {}", userList.size());
            return "admin";
        } catch (Exception e) {
            logger.error("AdminController.sortUsersDESC(): Error sorting users in descending order: {}", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/deleteUser/{userId}")
    public String delete(Model model, @PathVariable("userId") Long userId, HttpSession session) {
        try {
            if ((session.getAttribute("userId")).equals(userId)) {
                logger.warn("AdminController.delete(): User attempted to delete their own account. User ID: {}", userId);
                return "redirect:/admin";
            }
            userService.deleteUserById(userId);
            logger.info("AdminController.delete(): Deleted user with ID: {}", userId);
            return "redirect:/admin";
        } catch (Exception e) {
            logger.error("AdminController.delete(): Error deleting user with ID {}: {}", userId, e.getMessage());
            return "error";
        }
    }

    @GetMapping("/editUser/{userId}")
    public String redirectEdit(Model model, @PathVariable("userId") Long userId, HttpSession session) {
        try {
            if ((session.getAttribute("userId")).equals(userId)) {
                logger.warn("AdminController.redirectEdit(): User attempted to edit their own account. User ID: {}", userId);
                return "redirect:/admin";
            }
            _User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            logger.info("AdminController.redirectEdit(): Fetched user data for editing: {}", userId);
            return "editUser";
        } catch (Exception e) {
            logger.error("AdminController.redirectEdit(): Error fetching user data for editing: {}", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/editUser/{userId}")
    public String editUser(@Valid @ModelAttribute("user") _User user, BindingResult bindingResult, @PathVariable("userId") Long userId, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                user.setId(userId);
                model.addAttribute("user", user);
                model.addAttribute("errors", bindingResult);
                logger.warn("AdminController.editUser(): Validation errors encountered when editing user with ID: {}", userId);
                return "editUser";
            }
            userService.updateUser(user, userId);
            logger.info("AdminController.editUser(): Updated user with ID: {}", userId);
            return "redirect:/admin";
        } catch (Exception e) {
            logger.error("AdminController.editUser(): Error updating user with ID {}: {}", userId, e.getMessage());
            return "error";
        }
    }
}
