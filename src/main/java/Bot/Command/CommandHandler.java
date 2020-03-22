package Bot.Command;

import Bot.Bot;

abstract public class CommandHandler {
    final String command;

    CommandHandler(String passedCommand) {
        command = passedCommand;
    }

    public void handleBy(Bot executor) {
        if (!isValidCommand()) {
            throw new RuntimeException("Command " + command + " is invalid and can not be handled");
        }

        run(command, executor);
    }

    abstract public boolean isValidCommand();

    abstract protected void run(String Command, Bot executor);
}
