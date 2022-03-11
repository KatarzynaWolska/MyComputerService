package pl.wolskak.mycomputerservice.service.exceptions;

public class ComputerDamageExistsException extends RuntimeException {
    public ComputerDamageExistsException() {
        super("This computer already has active damage");
    }
}
