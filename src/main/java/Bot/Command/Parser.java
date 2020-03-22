package Bot.Command;

public class Parser {
    public static CommandHandler parseCommand(String command) {
        CommandHandler commandHandler = new SoundReaction(command);
        if (commandHandler.isValidCommand()) {
            return commandHandler;
        }

        return null;
    }
}
