package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

public class SiLog {

    public static final String LOG_AND_PRINT = "both";
    public static final String LOG_ONLY = "log";
    public static final String PRINT_ONLY = "print";

    private static Logger logger;

    private static void ensureLogger() {
        if (logger == null) {
            logger = Logger.getLogger(SiLog.class.getName());
            logger.setUseParentHandlers(false); // Disable default console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format("%s\n", lr.getMessage());
                }
            });
            logger.addHandler(consoleHandler);
        }
    }

    public static void Configure(String logDir, String logFile, String mode) {
        try {
            // Ensure the logs directory exists
            Files.createDirectories(Paths.get(logDir));

            // Setup logging handlers
            String logPath = Paths.get(logDir, logFile).toString();
            Handler[] handlers = new Handler[2];
            int index = 0;

            if (LOG_AND_PRINT.equals(mode) || LOG_ONLY.equals(mode)) {
                FileHandler fileHandler = new FileHandler(logPath, true);
                fileHandler.setFormatter(new SimpleFormatter() {
                    @Override
                    public synchronized String format(LogRecord lr) {
                        return String.format("%s\n", lr.getMessage());
                    }
                });
                handlers[index++] = fileHandler;
            }

            if (LOG_AND_PRINT.equals(mode) || PRINT_ONLY.equals(mode)) {
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new SimpleFormatter() {
                    @Override
                    public synchronized String format(LogRecord lr) {
                        return String.format("%s\n", lr.getMessage());
                    }
                });
                handlers[index++] = consoleHandler;
            }

            ensureLogger();
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false);
            for (Handler handler : handlers) {
                if (handler != null) {
                    logger.addHandler(handler);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void NewLine() {
        Message("");
    }

    public static void Message(Object msg) {
        ensureLogger();
        logger.info(String.valueOf(msg));
    }

    public static void Error(Object err) {
        ensureLogger();
        logger.severe("ERROR [" + err + "]");
        System.exit(1);
    }

    public static void Unsupported(Object val) {
        ensureLogger();
        logger.severe("unsupported [" + val + "]");
        throw new UnsupportedOperationException("unsupported [" + val + "]");
    }

    public static void KeyVal(Object key, Object val) {
        ensureLogger();
        logger.info(key + " [" + val + "]");
    }

}
