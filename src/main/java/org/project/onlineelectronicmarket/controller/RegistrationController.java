package org.project.onlineelectronicmarket.controller;

import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.model.enums.Roles;
import org.project.onlineelectronicmarket.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

@Controller
public class RegistrationController {

        private final UserServiceImpl userServiceImpl;

        @Autowired
        public RegistrationController(UserServiceImpl userServiceImpl) {
                this.userServiceImpl = userServiceImpl;
        }

        @GetMapping("/registration")
        public ModelAndView registration() {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("registration");
                return modelAndView;
        }


        @PostMapping(value = "/registration")
        public ModelAndView addUser(ModelAndView modelAndView, @Valid @ModelAttribute("user") User user,
                                    BindingResult result) {

                if (result.hasErrors()) {
                        modelAndView.addObject("add", true);
                        modelAndView.setViewName("/registration");
                } else {
                        if (userServiceImpl.existsByName(user.getName())) {
                                modelAndView.addObject("existsByName", true);
                                modelAndView.addObject("message",
                                        "User with such name already exists");
                                modelAndView.addObject("add", true);
                                modelAndView.setViewName("/registration");
                        } else {
                                user.setRoles(new HashSet<>(List.of(Roles.USER)));
                                userServiceImpl.save(user);
                                modelAndView.setViewName("redirect:/login");
                        }
                }

                return modelAndView;
        }

        @ModelAttribute
        public void addAttributes(Model model) {
                model.addAttribute("user", new User());
        }
}
