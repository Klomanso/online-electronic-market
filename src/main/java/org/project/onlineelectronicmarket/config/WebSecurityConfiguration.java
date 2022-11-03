package org.project.onlineelectronicmarket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@Configuration
class WebSecurityConfiguration {
        private final AccessDeniedHandler accessDeniedHandler;

        private final AuthenticationConfiguration configuration;

        private final UserDetailsService userDetailsService;

        @Autowired
        public WebSecurityConfiguration(AuthenticationConfiguration configuration, UserDetailsService userDetailsService,
                                 AccessDeniedHandler accessDeniedHandler) {
                this.configuration = configuration;
                this.accessDeniedHandler = accessDeniedHandler;
                this.userDetailsService = userDetailsService;
        }

        @Bean
        protected PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf().disable()
                        .authorizeHttpRequests()
                        .antMatchers("/", "/login", "/registration","/home",
                                "/goodsFound", "/goods", "/goodsFilter").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .formLogin()
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                        .and()
                        .logout()
                        .logoutUrl("/logout")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID");
                return http.build();
        }

        @Bean
        protected WebSecurityCustomizer webSecurityCustomizer() {
                return (web) -> web
                        .ignoring()
                        .antMatchers("/resources/**", "/static/**")
                        .antMatchers("/js/*")
                        .antMatchers("/images/*")
                        .antMatchers("/css/*");
        }

        @Bean
        protected AuthenticationManager authenticationManager() throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Autowired
        protected void configure(AuthenticationManagerBuilder builder) throws Exception {
                builder.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        }
}