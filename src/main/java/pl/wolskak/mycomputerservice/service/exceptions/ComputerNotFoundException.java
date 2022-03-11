package pl.wolskak.mycomputerservice.service.exceptions;

public class ComputerNotFoundException extends RuntimeException {
    public ComputerNotFoundException(Integer id) {
        super("Computer (id= " + id + " ) not found");
    }
}
