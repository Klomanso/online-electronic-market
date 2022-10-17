package org.project.onlineelectronicmarket.controller;

import java.util.List;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


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

        @GetMapping("/userInfo/{id}")
        public ModelAndView getUserInfo(@PathVariable("id") Long id, ModelAndView modelAndView) {
                Optional<User> foundUser = userService.findById(id);

                if (foundUser.isPresent()) {
                        User user = foundUser.get();
                        List<Order> orders = userService.findUserOrdersById(id);

                        modelAndView.addObject("user", user);
                        modelAndView.addObject("orders", orders);
                        modelAndView.setViewName("user/userInfo");
                } else {
                        modelAndView.addObject("error", new ErrorMsg("There is no user with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                }
                return modelAndView;
        }

        @GetMapping("/userEdit/{id}")
        public ModelAndView editUserInfo(@PathVariable("id") Long id, ModelAndView modelAndView) {
                Optional<User> foundUser = userService.findById(id);

                if (foundUser.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no user with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                } else {
                        modelAndView.addObject("add", false);
                        modelAndView.addObject("user", foundUser.get());
                        modelAndView.setViewName("user/userEdit");
                }
                return modelAndView;
        }

        @GetMapping("/user/add")
        public ModelAndView showAddUser(ModelAndView modelAndView) {

                modelAndView.addObject("add", true);
                modelAndView.addObject("user", new User());
                modelAndView.setViewName("user/userEdit");

                return modelAndView;
        }

        @PostMapping(value = "/user/add")
        public ModelAndView addUser(ModelAndView modelAndView, @ModelAttribute("user") User user) {
                User newUser = userService.save(user);

                modelAndView.addObject("user", newUser);
                modelAndView.setViewName("redirect:/userInfo/" + newUser.getId());

                return modelAndView;
        }

        @PostMapping("/user/save/{id}")
        public ModelAndView saveUserInfo(@ModelAttribute("user") User user, @PathVariable("id") Long id, ModelAndView modelAndView) {
                user.setId(id);
                userService.update(user);
                modelAndView.setViewName("redirect:/userInfo/" + user.getId());

                return modelAndView;
        }


        @PostMapping("/user/delete/{id}")
        public ModelAndView delete(@PathVariable("id") Long id, ModelAndView modelAndView) {
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
