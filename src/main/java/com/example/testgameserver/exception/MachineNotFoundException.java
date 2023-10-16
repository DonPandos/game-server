package com.example.testgameserver.exception;

public class MachineNotFoundException extends RuntimeException {
    public MachineNotFoundException(String mac) {
        super("Machine with MAC=" + mac + " not found.");
    }
}
