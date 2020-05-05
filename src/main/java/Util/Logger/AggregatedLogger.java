package Util.Logger;

import java.util.Arrays;
import java.util.LinkedList;

public class AggregatedLogger implements Logger{
    private LinkedList<Logger> loggers;

    public AggregatedLogger(Logger... loggers) {
        this.loggers = new LinkedList<>();
        this.loggers.addAll(Arrays.asList(loggers));
    }

    @Override
    public void log(String message) {
        loggers.forEach(logger -> logger.log(message));
    }

    @Override
    public void close() {
        loggers.forEach(Logger::close);
    }
}
