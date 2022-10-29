package org.project.onlineelectronicmarket.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.project.onlineelectronicmarket.model.Order;
import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class UserController {
        private final UserServiceImpl userServiceImpl;

        @Autowired
        public UserController(UserServiceImpl userServiceImpl) {
                this.userServiceImpl = userServiceImpl;
        }

        @GetMapping("/users")
        public ModelAndView getUsers(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                     @RequestParam(value = "size", required = false, defaultValue = "5") int size,
                                     ModelAndView modelAndView) {

                modelAndView.addObject("users", userServiceImpl.getPage(pageNumber, size));
                modelAndView.setViewName("user/users");
                return modelAndView;
        }

        @GetMapping("/userInfo/{id}")
        public ModelAndView getUserInfo(@PathVariable("id") Long id, ModelAndView modelAndView) {
                Optional<User> foundUser = userServiceImpl.findById(id);

                if (foundUser.isPresent()) {
                        User user = foundUser.get();
                        List<Order> orders = userServiceImpl.findUserOrdersById(id);

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
                Optional<User> foundUser = userServiceImpl.findById(id);

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
        public ModelAndView addUser(ModelAndView modelAndView, @Valid @ModelAttribute("user") User user,
                                    BindingResult result) {

                if (result.hasErrors()) {
                        modelAndView.addObject("add", true);
                        modelAndView.setViewName("user/userEdit");
                } else {
                        if (userServiceImpl.existsByName(user.getName())) {
                                modelAndView.addObject("existsByName", true);
                                modelAndView.addObject("message",
                                        "User with such name already exists");
                                modelAndView.addObject("add", true);
                                modelAndView.setViewName("user/userEdit");
                        } else {
                                User newUser = userServiceImpl.save(user);
                                modelAndView.addObject("user", newUser);
                                modelAndView.setViewName("redirect:/userInfo/" + newUser.getId());
                        }
                }

                return modelAndView;
        }

        @PostMapping("/user/save/{id}")
        public ModelAndView saveUserInfo(@Valid @ModelAttribute("user") User user, @PathVariable("id") Long id,
                                         ModelAndView modelAndView, BindingResult result) {

                if (result.hasErrors()) {
                        modelAndView.addObject("add", false);
                        modelAndView.setViewName("user/userEdit");
                } else {
                        if (userServiceImpl.existsByName(user.getName())
                                && !Objects.equals(userServiceImpl.findById(id).get().getName(), user.getName())) {
                                modelAndView.addObject("existsByName", true);
                                modelAndView.addObject("message",
                                        "User with such name already exists");
                                modelAndView.addObject("add", false);
                                modelAndView.setViewName("user/userEdit");
                        } else {
                                user.setId(id);
                                userServiceImpl.update(user);
                                modelAndView.setViewName("redirect:/userInfo/" + user.getId());
                        }
                }

                return modelAndView;
        }


        @PostMapping("/user/delete/{id}")
        public ModelAndView delete(@PathVariable("id") Long id, ModelAndView modelAndView) {
                Optional<User> foundUser = userServiceImpl.findById(id);

                if (foundUser.isEmpty()) {
                        modelAndView.addObject("error", new ErrorMsg("There is no user with id=" + id));
                        modelAndView.setViewName("error/invalid-action");
                } else {
                        userServiceImpl.delete(foundUser.get());
                        modelAndView.setViewName("redirect:/users");
                }

                return modelAndView;
        }
}
