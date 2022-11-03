package org.project.onlineelectronicmarket.model.enums;

public enum Roles {
        USER("User"),
        ADMIN("Admin"),
        MANAGER("Manager");

        private final String value;

        Roles(String value) {
                this.value = value;
        }

        public static Roles fromString(String value) {
                if (value != null) {
                        for (Roles pt : Roles.values()) {
                                if (value.equalsIgnoreCase(pt.value)) {
                                        return pt;
                                }
                        }
                }
                throw new IllegalArgumentException("No such value");
        }

        public String getValue() {
                return value;
        }
}