package Bot.Command;

import Bot.User;

public class Command {
    private String command;
    private User initiator;

    public Command(String command, User initiator) {
        this.command   = command;
        this.initiator = initiator;
    }

    public String getCommand() {
        return command;
    }

    public boolean startsWith(String text) {
        return command.indexOf(text) == 0;
    }

    public String getInitiator() {
        return initiator.getName();
    }
    
    public boolean isInitiatedByAdmin() {
        return initiator.isAdmin();
    }
}
