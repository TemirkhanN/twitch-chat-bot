package Bot.Command;

public interface CommandHandlerInterface {
    public static int RESULT_CODE_OK = 0;
    public static int RESULT_CODE_ERROR = 1;

    public int handle(Command command);
}
