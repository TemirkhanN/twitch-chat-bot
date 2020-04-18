package Util.Logger;

import java.util.LinkedList;

public class AggregatedLogger implements Logger{
    private LinkedList<Logger> loggers;

    public AggregatedLogger(Logger... loggers) {
        this.loggers = new LinkedList<>();
        for (Logger logger:loggers) {
            this.loggers.add(logger);
        }
    }

    @Override
    public void log(String message) {
        loggers.forEach(msg -> msg.log(message));
    }
}
