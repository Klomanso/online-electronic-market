package org.project.onlineelectronicmarket.controller;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserController {
        private final UserService userService;

        @Autowired
        public UserController(UserService userService) {
                this.userService = userService;
        }

        @GetMapping("/users")
        public ModelAndView getUsers(ModelAndView modelAndView) {
                List<User> users = userService.findAllByOrderByName();
                modelAndView.addObject("users", users);
                modelAndView.setViewName("user/users");
                return modelAndView;
        }

        @GetMapping("/user-info")
        public ModelAndView getUserInfo(@RequestParam(name = "user-id") Long id, ModelAndView modelAndView) {
                Optional<User> foundUser = userService.findById(id);

                if (foundUser.isPresent()) {
                        User user = foundUser.get();
                        List<Order> orders = userService.findUserOrdersById(id);

                        modelAndView.addObject("user", user);
                        modelAndView.addObject("orders", orders);
                        modelAndView.setViewName("user/user-info");
                } else {
                        modelAndView.addObject("error", new ErrorMsg("There is no user with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                }
                return modelAndView;
        }

        /*
         * If given user id is null then adds new user.
         * Otherwise, returns user info (to let then update it).
         */
        @GetMapping("/user-edit")
        public ModelAndView editUserInfo(@RequestParam(name = "user-id", required = false) Long id, ModelAndView modelAndView) {
                if (id == null) {
                        modelAndView.addObject("user", new User());
                        modelAndView.setViewName("user/user-edit");
                } else {
                        Optional<User> foundUser = userService.findById(id);

                        if (foundUser.isEmpty()) {
                                modelAndView.addObject("error", new ErrorMsg("There is no user with id=" + id));
                                modelAndView.setViewName("error/invalid-action");
                        } else {
                                modelAndView.addObject("user", foundUser.get());
                                modelAndView.setViewName("user/user-edit");
                        }
                }
                return modelAndView;
        }

        /*
         * If given id is null then saves new user with given characteristics.
         * Otherwise, updates user info.
         */
        @PostMapping("/user-save")
        public ModelAndView saveUserInfo(@RequestParam(name = "user-id", required = false) Long id,
                                   @Valid @RequestParam(name = "user-name") String name,
                                   @Valid @RequestParam(name = "user-address") String address,
                                   @Valid @RequestParam(name = "user-email") String email,
                                   @Valid @RequestParam(name = "user-number", required = false) String number,
                                   ModelAndView modelAndView) {

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
                                modelAndView.addObject("error", new ErrorMsg("There is no user with id=" + id));
                                modelAndView.setViewName("error/invalid-action");
                                return modelAndView;
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
                        modelAndView.setViewName("redirect:/user-info?user-id=" + savedUser.getId());
                } else {
                        modelAndView.addObject("error", new ErrorMsg("Cannot save user info"));
                        modelAndView.setViewName("error/invalid-action");
                }

                return modelAndView;
        }

        @PostMapping("/user-delete")
        public ModelAndView deleteUser(@RequestParam(name = "user-id") Long id, ModelAndView modelAndView) {
                Optional<User> foundUser = userService.findById(id);

                if (foundUser.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no user with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                } else {
                        userService.delete(foundUser.get());
                        modelAndView.setViewName("redirect:/users");
                }

                return modelAndView;
        }

}
