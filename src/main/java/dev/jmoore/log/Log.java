package dev.jmoore.log;

public class Log {
    public static final LogRoute router = new LogRoute();
    public static final Loggy GENERAL = router.channel("GENERAL");
    public static final Loggy CORE = router.channel("CORE");
    public static final Loggy CLIENT = router.channel("CLIENT");
    public static final Loggy SERVER = router.channel("SERVER");
    public static final Loggy SESSION = router.channel("SESSION");
    public static final Loggy PACKMAN = router.channel("PACKMAN");

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> GENERAL.alert(String.format("Uncaught exception in thread \"%s\"!", thread), ex));
        GENERAL.debug("Logger initialized");
    }
}
