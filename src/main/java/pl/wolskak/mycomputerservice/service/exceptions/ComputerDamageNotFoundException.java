package pl.wolskak.mycomputerservice.service.exceptions;

public class ComputerDamageNotFoundException extends RuntimeException {
    public ComputerDamageNotFoundException() {
        super("Computer damage not found");
    }
}
