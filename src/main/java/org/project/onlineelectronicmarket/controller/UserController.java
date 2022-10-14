package org.project.onlineelectronicmarket.controller;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class UserController {
        private final UserService userService;

        @Autowired
        public UserController(UserService userService) {
                this.userService = userService;
        }

        @GetMapping("/users")
        public String getUsers(Model model) {
                List<User> users = userService.findAllByOrderByName();
                model.addAttribute("users", users);
                return "user/users";
        }

        @GetMapping("/user-info")
        public String getUserInfo(@RequestParam(name = "user-id") Long id,
                                  Model model) {
                Optional<User> foundUser = userService.findById(id);
                if (foundUser.isPresent()) {
                        User user = foundUser.get();
                        List<Order> orders = userService.findUserOrdersById(id);
                        model.addAttribute("user", user);
                        model.addAttribute("orders", orders);
                        return "user/user-info";
                } else {
                        model.addAttribute("error", new ErrorMsg("There is no user with id=" + id));
                        return "error/invalid-action";
                }
        }

        /*
         * If given user id is null then adds new user.
         * Otherwise, returns user info (to let then update it).
         */
        @GetMapping("/user-edit")
        public String editUserInfo(@RequestParam(name = "user-id", required = false) Long id,
                                   Model model) {
                if (id == null) {
                        model.addAttribute("user", new User());
                        return "user/user-edit";
                } else {
                        Optional<User> foundUser = userService.findById(id);
                        if (foundUser.isEmpty()) {
                                model.addAttribute("error", new ErrorMsg("There is no user with id=" + id));
                                return "error/invalid-action";
                        } else {
                                model.addAttribute("user", foundUser.get());
                                return "user/user-edit";
                        }
                }
        }

        /*
         * If given id is null then saves new user with given characteristics.
         * Otherwise, updates user info.
         */
        @PostMapping("/user-save")
        public String saveUserInfo(@RequestParam(name = "user-id", required = false) Long id,
                                   @Valid @RequestParam(name = "user-name") String name,
                                   @Valid @RequestParam(name = "user-address") String address,
                                   @Valid @RequestParam(name = "user-email") String email,
                                   @Valid @RequestParam(name = "user-number", required = false) String number,
                                   Model model) {

                boolean successfullySaved;
                User savedUser = null;

                if (id == null) {
                        User user = new User(name, address, email, number);
                        User newUser = userService.save(user);
                        successfullySaved = (newUser.getId() != null);
                        if (successfullySaved) {
                                savedUser = newUser;
                        }
                } else {
                        Optional<User> foundUser = userService.findById(id);
                        if (foundUser.isEmpty()) {
                                model.addAttribute("error", new ErrorMsg("There is no user with id=" + id));
                                return "error/invalid-action";
                        } else {
                                User user = foundUser.get();
                                user.setName(name);
                                user.setAddress(address);
                                user.setEmail(email);
                                user.setNumber(number);
                                Optional<User> updatedUser = userService.update(user);
                                successfullySaved = updatedUser.isPresent();
                                if (successfullySaved) {
                                        savedUser = updatedUser.get();
                                }
                        }
                }

                if (successfullySaved) {
                        return "redirect:/user-info?user-id=" + savedUser.getId();
                } else {
                        model.addAttribute("error", new ErrorMsg("Cannot save user info"));
                        return "error/invalid-action";
                }
        }

        @PostMapping("/user-delete")
        public String deleteUser(@RequestParam(name = "user-id") Long id,
                                 Model model) {
                Optional<User> foundUser = userService.findById(id);
                if (foundUser.isEmpty()) {
                        model.addAttribute("error", new ErrorMsg("There is no user with id=" + id));
                        return "error/invalid-action";
                } else {
                        userService.delete(foundUser.get());
                        return "redirect:/users";
                }
        }

}
