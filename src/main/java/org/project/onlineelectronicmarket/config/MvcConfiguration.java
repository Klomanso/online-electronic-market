package org.project.onlineelectronicmarket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
        public void addViewControllers(ViewControllerRegistry viewControllerRegistry){
                viewControllerRegistry.addViewController("/successLogin").setViewName("redirect:/");
                viewControllerRegistry.addViewController("/login").setViewName("/login");
                viewControllerRegistry.addViewController("/403").setViewName("error/403");
        }
}