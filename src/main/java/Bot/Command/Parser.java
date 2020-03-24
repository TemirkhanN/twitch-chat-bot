package Bot.Command;

import Bot.Message;

public class Parser {
    public static CommandHandler parseCommand(Message message) {
        CommandHandler commandHandler = new SoundReaction(message.getCommonPart());
        if (commandHandler.isValidCommand()) {
            return commandHandler;
        }

        return null;
    }
}
