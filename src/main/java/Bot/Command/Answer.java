package Bot.Command;

import java.util.HashMap;

public class Answer extends CommandHandler {
    public static final String PLACEHOLDER_SENDER_NAME = "%username%";

    private HashMap<String, String> answers;

    public Answer() {
        answers = new HashMap<>();
    }

    public void addAnswer(String question, String answer) {
        answers.put(question, answer);
    }

    @Override
    public boolean supports(Command command) {
        return answers.containsKey(command.getCommand());
    }

    @Override
    protected void run(Command command) {
        String response = answers.get(command.getCommand());

        if (response == null) {
            return;
        }

        if (response.contains(PLACEHOLDER_SENDER_NAME)) {
            response = response.replaceAll(PLACEHOLDER_SENDER_NAME, command.getInitiator().getName());
        }

        command.getMediator().sendMessage(response);
    }
}
