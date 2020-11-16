package Bot.Command;

import java.util.HashMap;
import java.util.function.Supplier;

public class Question extends CommandHandler {
    public static final String PLACEHOLDER_SENDER_NAME = "%username%";

    private HashMap<String, Supplier<String>> answers;

    private OutputInterface output;

    public Question(OutputInterface output) {
        answers = new HashMap<>();
        this.output = output;
    }

    public void addAnswer(String question, String answer) {
        answers.put(question, () -> answer);
    }

    public void addAnswer(String question, Supplier<String> answer) {
        answers.put(question, answer);
    }

    @Override
    public boolean supports(Command command) {
        return answers.containsKey(command.getCommand());
    }

    @Override
    public void handle(Command command) {
        String response = answers.get(command.getCommand()).get();
        if (response == null) {
            return;
        }

        if (response.contains(PLACEHOLDER_SENDER_NAME)) {
            response = response.replaceAll(PLACEHOLDER_SENDER_NAME, command.getInitiator());
        }

        output.write(response);
    }
}
