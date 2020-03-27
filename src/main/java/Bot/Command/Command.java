package Bot.Command;

import Bot.Bot;
import Bot.User;

public class Command {
    private String command;
    private User initiator;
    private Bot mediator;

    public Command(String command, User initiator, Bot mediator) {
        this.command = command;
        this.initiator = initiator;
        this.mediator = mediator;
    }

    public String getCommand() {
        return command;
    }

    public boolean startsWith(String text) {
        return command.indexOf(text) == 0;
    }

    public User getInitiator() {
        return initiator;
    }


    public Bot getMediator() {
        return mediator;
    }
}
