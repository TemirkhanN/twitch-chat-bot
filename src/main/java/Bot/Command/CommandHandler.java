package Bot.Command;

abstract public class CommandHandler implements CommandHandlerInterface {

    abstract public boolean supports(Command command);

    public int handle(Command command) {
        if (!supports(command)) {
            throw new RuntimeException("Command " + command + " is invalid and can not be handled");
        }

        return RESULT_CODE_OK;
    }
}
