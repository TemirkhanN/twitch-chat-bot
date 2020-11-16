package Bot.Command;

import DJ.Dj;
import DJ.Track;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DJControl extends CommandHandler {
    private static final String SKIP_COMMAND = "!skip";
    private static final String TRACK_INFO_COMMAND = "!music";

    private int votesRequiredForTrackSkip = 3;
    private String currentTrackName = "";

    private HashSet<String> votesForSkip = new HashSet<>();

    private List<Dj> deejays = new ArrayList<>();

    private OutputInterface output;

    public DJControl(OutputInterface output) {
        this.output = output;
    }

    public void addDj(Dj dj) {
        deejays.add(dj);
    }

    @Override
    public boolean supports(Command command) {
        if (deejays.isEmpty()) {
            return false;
        }

        String commandText = command.getCommand();
        if (commandText.equals(SKIP_COMMAND)) {
            return true;
        }

        if (commandText.equals(TRACK_INFO_COMMAND)) {
            return true;
        }

        return false;
    }

    @Override
    public void handle(Command command) {
        String fullCommand = command.getCommand();
        if (fullCommand.equals(SKIP_COMMAND)) {
            skipCurrentTrack(command);

            return;
        }

        if (fullCommand.equals(TRACK_INFO_COMMAND)) {
            Track track = getCurrentTrackInfo();
            if (track != null) {
                output.write("SingsNote Сейчас играет: " + track.getFullName());
            } else {
                output.write("Я не знаю, что сейчас играет. Может, LoFi hip-hop.");
            }
        }
    }

    private synchronized void skipCurrentTrack(Command command) {
        Track currentTrack = getCurrentTrackInfo();
        if (currentTrack == null) {
            output.write("Если сейчас что-то играет, это за пределами моей досягаемости");

            return;
        }

        if (!currentTrackName.equals(currentTrack.getFullName())) {
            currentTrackName = currentTrack.getFullName();
            votesForSkip.clear();
        }

        String initiator = command.getInitiator();
        votesForSkip.add(initiator);
        if (!command.isInitiatedByAdmin() && votesForSkip.size() < votesRequiredForTrackSkip) {
            output.write(initiator + " проголосовал за пропуск трека. Нужно еще " + (votesRequiredForTrackSkip - votesForSkip.size()));
            return;
        }

        for (Dj dj : deejays) {
            if (dj.isPlaying(currentTrack)) {
                dj.skipCurrentTrack();
                votesForSkip.clear();

                output.write("Демократия изгоняет трек из эфира. HSWP");

                return;
            }
        }

        output.write("Не удается пропустить трек.");
    }

    private Track getCurrentTrackInfo() {
        for (Dj dj : deejays) {
            Track track = dj.getCurrentTrack();
            if (track != null) {
                return track;
            }
        }

        return null;
    }
}
