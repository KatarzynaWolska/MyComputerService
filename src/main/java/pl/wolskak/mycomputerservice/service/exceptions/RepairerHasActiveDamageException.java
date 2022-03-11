package pl.wolskak.mycomputerservice.service.exceptions;

public class RepairerHasActiveDamageException extends RuntimeException {
    public RepairerHasActiveDamageException() {
        super("Repairer has active damage");
    }
}
