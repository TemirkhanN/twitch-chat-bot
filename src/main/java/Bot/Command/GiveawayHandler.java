package Bot.Command;

import Bot.Bot;
import Bot.User;
import Community.Giveaway.Giveaway;
import Community.Giveaway.LogicException;

import java.util.ArrayList;

public class GiveawayHandler extends CommandHandler {
    private String commandToParticipate;
    private Giveaway giveaway;

    public GiveawayHandler(String commandToParticipate, Giveaway giveaway) {
        this.commandToParticipate = commandToParticipate;
        this.giveaway = giveaway;
    }

    @Override
    public boolean supports(Command command) {
        if (isParticipationCommand(command)) {
            return true;
        }

        if (isParticipantListCommand(command)) {
            return true;
        }

        if (isGiveawayStartCommand(command)) {
            return true;
        }

        return false;
    }

    @Override
    protected void run(Command command) {
        if (giveaway.isOver()) {
            command.getMediator().sendMessage("Раздача завершена.");

            return;
        }

        if (isParticipationCommand(command)) {
            participate(command.getInitiator(), command.getMediator());

            return;
        }

        if (isParticipantListCommand(command)) {
            command.getMediator().sendMessage("Список участников: " + giveaway.getParticipants().toString());

            return;
        }

        if (isGiveawayStartCommand(command) && command.isInitiatedByAdmin()) {
            startGiveaway(command);

            return;
        }
    }

    private void startGiveaway(Command command) {
        Bot mediator = command.getMediator();
        try {
            giveaway.startGiveaway();
        } catch (LogicException e) {
            mediator.sendMessage(e.getMessage());

            return;
        }

        StringBuilder giveawayResult = new StringBuilder();
        giveaway.winners().forEach((String winner, ArrayList items) -> {
            giveawayResult.append(winner);
            giveawayResult.append(" получает ");
            giveawayResult.append(items.toString());
            giveawayResult.append("; ");
        });

        mediator.sendMessage(giveawayResult.toString());
    }

    private void participate(User user, Bot mediator) {
        String participant = user.getName();
        if (giveaway.hasParticipant(participant)) {
            mediator.whisper(user, "ты уже принимаешь участи в раздаче.");

            return;
        }

        giveaway.addParticipant(participant);
        mediator.whisper(user, "вступает в раздачу.");
    }

    private boolean isParticipationCommand(Command command) {
        return command.getCommand().equals(commandToParticipate);
    }

    private boolean isParticipantListCommand(Command command) {
        return command.getCommand().equals("!giveaway participants");
    }

    private boolean isGiveawayStartCommand(Command command) {
        return command.getCommand().equals("!giveaway start");
    }
}
