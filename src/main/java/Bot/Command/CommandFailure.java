package Bot.Command;

class CommandFailure extends Exception{
    CommandFailure(String message) {
        super(message);
    }

    CommandFailure(String message, Exception prev) {
        super(message, prev);
    }
}
