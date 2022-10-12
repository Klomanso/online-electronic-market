package org.project.onlineelectronicmarket.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "\"user\"")
public class User implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @Column(name = "user_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "user_name", nullable = false, length = 70)
        private String name;

        @Column(name = "user_address", nullable = false, length = 50)
        private String address;

        @Column(name = "user_email", nullable = false, length = 30)
        private String email;

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
