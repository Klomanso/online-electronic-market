package org.project.onlineelectronicmarket.model;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "app_type")
public class AppType implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @Column(name = "app_type_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "app_type_name", nullable = false, length = 30)
        @NotBlank(message = "Appliance type is required")
        @Size(min = 1, max = 30, message = "size range: [1-30] symbols")
        private String name;

        public AppType() {
        }

        public AppType(String name) {
                this.name = name;
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

                AppType other = (AppType) obj;

                return (id != null || other.id == null) && (id == null || id.equals(other.id));
        }

        @Override
        public String toString() {
                return "AppType{id=" + id
                        + ", name='" + name
                        + "'}";
        }
}
