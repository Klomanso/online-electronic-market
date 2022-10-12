package org.project.onlineelectronicmarket.controller;

public class ErrorMsg {
        private final String message;

        public ErrorMsg(String message) {
                this.message = message;
        }

        public String getMessage() {
                return message;
        }
}
