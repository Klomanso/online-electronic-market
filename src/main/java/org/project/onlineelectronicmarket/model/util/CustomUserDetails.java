package org.project.onlineelectronicmarket.model.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.project.onlineelectronicmarket.model.enums.Roles;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;


@Getter
@Setter
@ToString
public class CustomUserDetails extends User {

        private Long id;
        private String name;
        private Set<Roles> roles;

        public CustomUserDetails(UserDetails details, org.project.onlineelectronicmarket.model.User user) {
                super(details.getUsername(), details.getPassword(), details.getAuthorities());
                this.id = user.getId();
                this.name = user.getName();
                this.roles = user.getRoles();
        }

}