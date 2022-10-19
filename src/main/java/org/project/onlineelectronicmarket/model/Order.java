package org.project.onlineelectronicmarket.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "\"order\"")
public class Order implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @Column(name = "order_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "order_ordered_at", nullable = false)
        private LocalDateTime orderedAt;

        @ManyToOne()
        @JoinColumn(name = "status_id")
        private Status status = new Status("processing");

        @NotBlank(message = "Delivery address is required")
        @Size(min = 1, max = 50, message = "size range: [1-50] symbols")
        @Column(name = "order_delivery_address", nullable = false, length = 50)
        private String deliveryAddress;

        @Column(name = "order_deliver_on")
        @FutureOrPresent
        private LocalDate deliverOn;

        @ManyToOne()
        @JoinColumn(name = "user_id")
        private User user;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinColumn(name = "order_id", nullable = false, insertable = false, updatable = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        private final List<OrderGood> goodItems = new ArrayList<>();

        public Order() {
        }

        public Order(LocalDateTime orderedAt, Status status, String deliveryAddress,
                     LocalDate deliverOn, User user) {

                this.orderedAt = orderedAt;
                this.status = status;
                this.deliveryAddress = deliveryAddress;
                this.deliverOn = deliverOn;
                this.user = user;
                status.addOrder(this);
                user.addOrder(this);
        }

        public Long getId() {
                return id;
        }

        public LocalDateTime getOrderedAt() {
                return orderedAt;
        }

        public Status getStatus() {
                return status;
        }

        public void setStatus(Status status) {
                this.status.removeOrder(this);
                this.status = status;
                status.addOrder(this);
        }

        public String getDeliveryAddress() {
                return deliveryAddress;
        }

        public LocalDate getDeliverOn() {
                return deliverOn;
        }

        public User getUser() {
                return user;
        }

        public List<OrderGood> getGoodItems() {
                return goodItems;
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

                Order other = (Order) obj;

                return (id != null || other.id == null) && (id == null || id.equals(other.id));
        }

        @Override
        public String toString() {
                return "Order{id=" + id
                        + ", orderedAt='" + orderedAt
                        + "', status=" + status
                        + ", deliveryAddress='" + deliveryAddress
                        + "', deliverOn=" + deliverOn
                        + ", user=" + user.toString()
                        + "}";
        }
}
