package Bot.Command;

import DJ.Dj;
import DJ.Track;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DJControl extends CommandHandler {
    private static final String COMMAND_PREFIX = "!music";
    private static final String SKIP_COMMAND = COMMAND_PREFIX + " skip";
    private static final String TRACK_INFO_COMMAND = COMMAND_PREFIX + "";

    private int votesRequiredForTrackSkip = 3;
    private String currentTrackName = "";

    private HashSet<String> votesForSkip = new HashSet<>();

    private List<Dj> deejays = new ArrayList<>();

    public void addDj(Dj dj) {
        deejays.add(dj);
    }

    @Override
    public boolean supports(Command command) {
        if (deejays.isEmpty()) {
            return false;
        }

        return command.startsWith(COMMAND_PREFIX + " ") || command.getCommand().equals(COMMAND_PREFIX);
    }

    @Override
    protected void run(Command command, OutputInterface output) {
        String fullCommand = command.getCommand();
        if (fullCommand.equals(SKIP_COMMAND)) {
            skipCurrentTrack(command.getInitiator(), output);

            return;
        }

        if (fullCommand.equals(TRACK_INFO_COMMAND)) {
            Track track = getCurrentTrackInfo();
            if (track != null) {
                output.write("Сейчас играет: " + track.getFullName());
            } else {
                output.write("Я не знаю, что сейчас играет. Может, LoFi hip-hop.");
            }
        }
    }

    @Override
    protected String getDescription() {
        return "Управление музыкой на канале";
    }

    private void skipCurrentTrack(String initiator, OutputInterface output) {
        Track currentTrack = getCurrentTrackInfo();
        if (currentTrack == null) {
            output.write("Если сейчас что-то играет, это за пределами моей досягаемости");

            return;
        }

        if (!currentTrackName.equals(currentTrack.getFullName())) {
            currentTrackName = currentTrack.getFullName();
            votesForSkip.clear();
        }

        votesForSkip.add(initiator);
        if (votesForSkip.size() >= votesRequiredForTrackSkip) {
            for (Dj dj : deejays) {
                if (dj.isPlaying(currentTrack)) {
                    dj.skipCurrentTrack();

                    return;
                }
            }
            votesForSkip.clear();
        }

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
