package Bot.Command;

import java.util.LinkedList;

public class CommandBus implements CommandHandlerInterface {
    private static LinkedList<CommandHandler> handlers = new LinkedList<>();

    public void registerHandler(CommandHandler handler) {
        handlers.addLast(handler);
    }

    @Override
    public void handle(Command command) {
        for(CommandHandler handler: handlers) {
            if (handler.supports(command)) {
                handler.handle(command);

                return;
            }
        }
    }
}
