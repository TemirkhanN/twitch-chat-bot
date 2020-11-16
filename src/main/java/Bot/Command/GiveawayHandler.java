package Bot.Command;

import Community.Giveaway.Giveaway;
import Community.Giveaway.LogicException;

import java.util.ArrayList;

public class GiveawayHandler extends CommandHandler {
    private String commandToParticipate;
    private Giveaway giveaway;
    private OutputInterface output;

    public GiveawayHandler(String commandToParticipate, Giveaway giveaway, OutputInterface output) {
        this.commandToParticipate = commandToParticipate;
        this.giveaway = giveaway;
        this.output = output;
    }

    @Override
    public boolean supports(Command command) {
        if (isParticipationCommand(command)) {
            return true;
        }

        if (isParticipantListCommand(command)) {
            return true;
        }

        return isGiveawayStartCommand(command);

    }

    @Override
    public void handle(Command command) {
        if (giveaway.isOver()) {
            output.write("Раздача завершена.");

            return;
        }

        if (isParticipationCommand(command)) {
            participate(command.getInitiator());
        }

        if (isParticipantListCommand(command)) {
            output.write("Список участников: " + giveaway.getParticipantsInfo());

            return;
        }

        if (isItemListCommand(command)) {
            output.write("Предметы на раздачу: " + giveaway.getItemsInfo());

            return;
        }

        if (isGiveawayStartCommand(command) && command.isInitiatedByAdmin()) {
            startGiveaway();

            return;
        }
    }

    private void startGiveaway() {
        try {
            giveaway.startGiveaway();
        } catch (LogicException e) {
            output.write(e.getMessage());

            return;
        }

        StringBuilder giveawayResult = new StringBuilder();
        giveaway.winners().forEach((String winner, ArrayList items) -> {
            giveawayResult.append(winner);
            giveawayResult.append(" получает ");
            giveawayResult.append(items.toString());
            giveawayResult.append("; ");
        });

        output.write(giveawayResult.toString());
    }

    private void participate(String participant) {
        if (giveaway.hasParticipant(participant)) {
            output.write(participant + " ты уже принимаешь участие в раздаче.");

            return;
        }

        giveaway.addParticipant(participant);
        output.write(participant + " вступает в раздачу.");
    }

    private boolean isParticipationCommand(Command command) {
        return command.getCommand().equals(commandToParticipate);
    }

    private boolean isItemListCommand(Command command) {
        return command.getCommand().equals("!giveaway items");
    }

    private boolean isParticipantListCommand(Command command) {
        return command.getCommand().equals("!giveaway participants");
    }

    private boolean isGiveawayStartCommand(Command command) {
        return command.getCommand().equals("!giveaway start");
    }
}
