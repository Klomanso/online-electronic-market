package org.project.onlineelectronicmarket.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.project.onlineelectronicmarket.model.enums.Roles;

@Entity
@Table(name = "\"user\"")
public class User implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @Column(name = "user_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Size(min = 1, max = 70, message = "size range: [1-70] symbols")
        @NotBlank(message = "Name is required")
        @Column(name = "user_name", nullable = false, length = 70)
        private String name;

        @Column(name = "user_password")
        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$",
                message = "password must contain at least 1 uppercase, 1 lowercase, 1 spec symbol and 1 digit")
        private String password;

        @ElementCollection(targetClass = Roles.class, fetch = FetchType.EAGER)
        @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
        @Enumerated(EnumType.STRING)
        private Set<Roles> roles;

        @Size(min = 1, max = 50, message = "size range: [1-50] symbols")
        @NotBlank(message = "Address is required")
        @Column(name = "user_address", nullable = false, length = 50)
        private String address;

        @Email
        @NotBlank(message = "Email is required")
        @Size(min = 1, max = 30, message = "size range: [1-30] symbols")
        @Column(name = "user_email", nullable = false, length = 30)
        private String email;

        @NotBlank(message = "Number is required")
        @Size(min = 1, max = 30, message = "size range: [1-20] symbols")
        @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number")
        @Column(name = "user_number", length = 20)
        private String number;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "user_id",
                nullable = false,
                insertable = false,
                updatable = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        private final List<Order> orders = new ArrayList<>();

        public User() {
        }

        public User(String name, String address, String email, String number) {
                this.name = name;
                this.address = address;
                this.email = email;
                this.number = number;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public void addOrder(Order order) {
                orders.add(order);
        }

        public Long getId() {
                return id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getNumber() {
                return number;
        }

        public void setNumber(String number) {
                this.number = number;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public Set<Roles> getRoles() {
                return roles;
        }

        public void setRoles(Set<Roles> roles) {
                this.roles = roles;
        }
        public List<Order> getOrders() {
                return orders;
        }

        @Override
        public int hashCode() {
                final int prime = 31;

                int result = 1;
                result = prime * result + ((id == null) ? 0 : id.hashCode());

                return result;
        }

        @Override
        public boolean equals(Object obj) {
                if (obj == null || getClass() != obj.getClass()) {
                        return false;
                }
                if (this == obj) {
                        return true;
                }

                User other = (User) obj;

                return (id != null || other.id == null) && (id == null || id.equals(other.id));
        }

        @Override
        public String toString() {
                return "User{id=" + id
                        + ", name='" + name
                        + "', address='" + address
                        + "', email=" + email
                        + ", number='" + number
                        + "'}";
        }
}
