package dev.jmoore.log;

public class LogRoute {
    public Loggy channel(String name) {
        return new Loggy(name);
    }
}
