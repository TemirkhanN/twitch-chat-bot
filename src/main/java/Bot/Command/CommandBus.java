package Bot.Command;

import java.util.LinkedList;

// TODO factory
public class CommandBus {
    private static LinkedList<CommandHandler> handlers = new LinkedList<>();

    // TODO segregate handling and command
    public void registerHandler(CommandHandler handler) {
        handlers.addLast(handler);
    }

    public void execute(Command command) {
        for(CommandHandler handler: handlers) {
            if (handler.supports(command)) {
                handler.execute(command);

                return;
            }
        }
    }
}
