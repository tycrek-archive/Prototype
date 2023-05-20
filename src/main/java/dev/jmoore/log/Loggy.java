package dev.jmoore.log;

import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class Loggy {
    private static final int PAD_SIZE = 10;
    private final String name;

    private String buildMessage(Object... args) {
        return String.format(args[0].toString(), List.of(args).subList(1, args.length).toArray());
    }

    private Loggy log(PrintStream ps, String level, Object... args) {
        ps.printf("[%s] > %s: %s: %s%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                padRight(name.toUpperCase(), " ", Math.max(PAD_SIZE, name.length())),
                padRight(level.toUpperCase(), " ", Math.max(PAD_SIZE, level.length())),
                buildMessage(args));
        return this;
    }

    private Loggy out(String level, Object... args) {
        return log(System.out, level, args);
    }

    private Loggy err(String level, Object... args) {
        return log(System.err, level, args);
    }

    private void printError(Object... args) {
        for (Object arg : args) if (arg instanceof Throwable) ((Throwable) arg).printStackTrace();
    }

    public Loggy debug(Object... args) {
        return out("debug", args);
    }

    public Loggy info(Object... args) {
        return out("info", args);
    }

    public Loggy warn(Object... args) {
        // Don't log super spammy invalid entity warnings
        if (String.join("", Arrays.stream(args).map(Object::toString).toArray(String[]::new)).contains("invalid entity"))
            return this;
        return out("warn", args);
    }

    public Loggy success(Object... args) {
        return out("success", args);
    }

    public Loggy trace(Object... args) {
        return out("trace", args);
    }

    public Loggy error(Object... args) {
        printError(args);
        return err("error", args);
    }

    @SuppressWarnings("unused")
    public Loggy fatal(Object... args) {
        printError(args);
        return err("fatal", args);
    }

    public Loggy alert(Object... args) {
        printError(args);
        return err("alert", args);
    }

    public static String padLeft(String input, String pad, int size) {
        return pad.repeat(size - input.length()).concat(input);
    }

    public static String padRight(String input, String pad, int size) {
        return input.concat(pad.repeat(size - input.length()));
    }
}
