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
@Table(name = "\"status\"")
public class Status implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @Column(name = "status_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "status_name", nullable = false, length = 20)
        private String name;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "status_id", nullable = false, insertable = false, updatable = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        private final List<Order> orders = new ArrayList<>();

        public Status() {
        }

        public Status(String name) {
                this.name = name;
        }

        public void addOrder(Order order) {
                orders.add(order);
        }

        public void removeOrder(Order order) {
                orders.remove(order);
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

                Status other = (Status) obj;

                return (id != null || other.id == null) && (id == null || id.equals(other.id));
        }

        @Override
        public String toString() {
                return "Status{id=" + id
                        + ", name='" + name
                        + "'}";
        }
}
