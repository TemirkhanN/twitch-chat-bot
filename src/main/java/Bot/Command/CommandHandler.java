package Bot.Command;

abstract public class CommandHandler {
    public void execute(Command command) {
        if (!supports(command)) {
            throw new RuntimeException("Command " + command + " is invalid and can not be handled");
        }

        run(command);
    }

    abstract public boolean supports(Command command);

    abstract protected void run(Command command);

    protected String getDescription() {
        return "Описание команды отсутствует";
    }
}
