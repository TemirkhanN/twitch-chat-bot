package Bot.Command;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class CommandRpmLimit implements CommandHandlerInterface {
    private CommandHandlerInterface handler;

    private long cooldownDelay;

    private Timer timer;

    private volatile HashSet<String> initiatorsLog;

    public CommandRpmLimit(int cooldownInSeconds, CommandHandlerInterface handler) {
        cooldownDelay = cooldownInSeconds * 1000L;
        this.handler = handler;
        timer = new Timer();
        initiatorsLog = new HashSet<>();
    }

    @Override
    public int handle(Command command) {
        String initiator = command.getInitiator();
        if (initiatorsLog.contains(initiator)) {
            return RESULT_CODE_OK;
        }

        int result = handler.handle(command);
        if (result == RESULT_CODE_OK) {
            initiatorsLog.add(initiator);
            TimerTask task = new TimerTask() {
                public void run() {
                    initiatorsLog.remove(initiator);
                }
            };
            timer.schedule(task, cooldownDelay);
        }

        return result;
    }
}
