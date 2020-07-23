package Bot.Command;

import java.util.LinkedList;

// TODO factory
public class CommandBus {
    private static LinkedList<CommandHandler> handlers = new LinkedList<>();

    // TODO segregate handling and command
    public void registerHandler(CommandHandler handler) {
        handlers.addLast(handler);
    }

    public void execute(Command command, OutputInterface output) {
        for(CommandHandler handler: handlers) {
            if (handler.supports(command)) {
                handler.execute(command, output);

                return;
            }
        }
    }
}
