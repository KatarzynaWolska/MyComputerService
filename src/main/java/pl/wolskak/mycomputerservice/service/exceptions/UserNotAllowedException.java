package pl.wolskak.mycomputerservice.service.exceptions;

public class UserNotAllowedException extends RuntimeException {
    public UserNotAllowedException() {
        super("User not allowed");
    }
}
