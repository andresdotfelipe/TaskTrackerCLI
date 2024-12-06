package config;

import java.util.logging.*;

public class LoggerConfigurator {
    private static final String PATTERN = "[%1$tF %1$tT] - %2$s%n%3$s%n"; // Date format, log level and message only

    public static void configure() {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();

        // Customize the existing console handler (if present)
        if (handlers.length > 0 && handlers[0] instanceof ConsoleHandler consoleHandler) {
            consoleHandler.setFormatter(new SimpleFormatter() {

                @Override
                public synchronized String format(LogRecord record) {
                    String formattedMessage = java.text.MessageFormat.format(record.getMessage(), record.getParameters());
                    return String.format(PATTERN, record.getMillis(), record.getLevel(), formattedMessage);
                }
            });
        }
    }
}
