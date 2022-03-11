package pl.wolskak.mycomputerservice.service.exceptions;

public class ComputerDamageIsAcceptedException extends RuntimeException {
    public ComputerDamageIsAcceptedException() {
        super("Computer damage is accepted");
    }
}
