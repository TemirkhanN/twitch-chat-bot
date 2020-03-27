package Bot.Command;

import Bot.Message;

// TODO factory
public class Parser {
    public static CommandHandler parseCommand(Message message) {
        CommandHandler commandHandler;

        commandHandler = new SoundReaction(message.getCommonPart());
        if (commandHandler.isValidCommand()) {
            return commandHandler;
        }

        commandHandler = new RussianRoulette(message.getCommonPart(), message.getSender());
        if (commandHandler.isValidCommand()) {
            return commandHandler;
        }

        return null;
    }
}
