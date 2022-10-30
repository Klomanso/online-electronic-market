package org.project.onlineelectronicmarket.model;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.*;

@Entity
@Table(name = "good")
public class Good implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @Column(name = "good_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne()
        @JoinColumn(name = "app_type_id")
        private AppType appType;

        @Column(name = "good_name", nullable = false, length = 100)
        @NotBlank(message = "Appliance type is required")
        @Size(min = 1, max = 100, message = "size range: [1-100] symbols")
        private String name;

        @Min(value = 0)
        @Max(value = 100_000)
        @Column(name = "good_price", nullable = false)
        private Double price;

        @NotBlank(message = "Company is required")
        @Size(min = 1, max = 50, message = "size range: [1-50] symbols")
        @Column(name = "good_company", length = 50)
        private String company;

        @NotBlank(message = "assembly_place is required")
        @Size(min = 1, max = 50, message = "size range: [1-50] symbols")
        @Column(name = "good_assembly_place", length = 50)
        private String assemblyPlace;

        public void setId(Long id) {
                this.id = id;
        }

        @Min(value = 0)
        @Max(value = 100_000)
        @Column(name = "good_quantity", nullable = false)
        private Integer quantity = 0;

        @NotBlank(message = "description is required")
        @Size(min = 1, max = 400, message = "size range: [1-400] symbols")
        @Column(name = "good_description", length = 400)
        private String description;

        @NotBlank(message = "image is required")
        @Column(name = "image", length = 50)
        private String image;

        public Good() {
        }

        public Good(AppType appType, String name, Double price,
                    String company, String assemblyPlace,
                    Integer quantity, String description, String image) {
                this.appType = appType;
                this.name = name;
                this.price = price;
                this.company = company;
                this.assemblyPlace = assemblyPlace;
                this.quantity = quantity;
                this.description = description;
                this.image = image;
        }

        public Long getId() {
                return id;
        }

        public AppType getAppType() {
                return appType;
        }

        public void setAppType(AppType appType) {
                this.appType = appType;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Double getPrice() {
                return price;
        }

        public void setPrice(Double price) {
                this.price = price;
        }

        public String getImage() {
                return image;
        }

        public void setImage(String image) {
                this.image = image;
        }
        public String getCompany() {
                return company;
        }

        public void setCompany(String company) {
                this.company = company;
        }

        public String getAssemblyPlace() {
                return assemblyPlace;
        }

        public void setAssemblyPlace(String assemblyPlace) {
                this.assemblyPlace = assemblyPlace;
        }

        public Integer getQuantity() {
                return quantity;
        }

        public void setQuantity(Integer quantity) {
                this.quantity = quantity;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
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

                Good other = (Good) obj;

                return (id != null || other.id == null) && (id == null || id.equals(other.id));
        }

        @Override
        public String toString() {
                return "Good{id=" + id
                        + ", appType=" + appType
                        + ", name='" + name
                        + "', price=" + price
                        + ", company='" + company
                        + "', assemblyPlace='" + assemblyPlace
                        + "', quantity=" + quantity
                        + ", description='" + description
                        + "'}";
        }
}
