package Bot.Command;

abstract public class CommandHandler {
    public void execute(Command command, OutputInterface output) {
        if (!supports(command)) {
            throw new RuntimeException("Command " + command + " is invalid and can not be handled");
        }

        run(command, output);
    }

    abstract public boolean supports(Command command);

    abstract protected void run(Command command, OutputInterface output);

    protected String getDescription() {
        return "Описание команды отсутствует";
    }
}
