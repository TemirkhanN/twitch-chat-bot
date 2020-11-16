package Bot.Command;

abstract public class CommandHandler implements CommandHandlerInterface {

    abstract public boolean supports(Command command);

    public void handle(Command command) {
        if (!supports(command)) {
            throw new RuntimeException("Command " + command + " is invalid and can not be handled");
        }
    }
}
