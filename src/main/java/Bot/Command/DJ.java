package Bot.Command;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class DJ extends CommandHandler {
    private static final String COMMAND_PREFIX = "!music";
    private static final String SKIP_COMMAND = COMMAND_PREFIX + " skip";
    private static final String TRACK_INFO_COMMAND = COMMAND_PREFIX + "";

    private int votesRequiredForTrackSkip = 3;
    private String currentTrackName = "";

    private HashSet<String> votesForSkip = new HashSet<>();

    public class Track {
        int id;
        String yid;
        String title;
        int track_time;
        String add_time;
        String start_time;
        String author;
    }

    public class Result {
        int success;
    }

    private static final String API_URL = "https://streamdj.ru/api/";

    private String apiKey;
    private int channelId;

    public DJ(int channelId, String apiKey) {
        this.apiKey = apiKey;
        this.channelId = channelId;
    }

    public DJ(int channelId, String apiKey, int votesForTrackSkip) {
        this.apiKey = apiKey;
        this.channelId = channelId;
        this.votesRequiredForTrackSkip = votesForTrackSkip;
    }

    @Override
    public boolean supports(Command command) {
        return command.startsWith(COMMAND_PREFIX + " ") || command.getCommand().equals(COMMAND_PREFIX);
    }

    @Override
    protected void run(Command command) {
        String fullCommand = command.getCommand();
        if (fullCommand.equals(SKIP_COMMAND)) {
            try {
                Track currentTrack = getCurrentTrackInfo();
                if (!currentTrackName.equals(currentTrack.title)) {
                    currentTrackName = currentTrack.title;
                    votesForSkip.clear();
                }

                votesForSkip.add(command.getInitiator().getName());
                if (votesForSkip.size() >= votesRequiredForTrackSkip) {
                    skipCurrentTrack();
                    votesForSkip.clear();
                }
            } catch (CommandFailure e) {
                command.getMediator().sendMessage("Не удалось пропустить трек");
            }

            return;
        }

        if (fullCommand.equals(TRACK_INFO_COMMAND)) {
            try {
                Track track = getCurrentTrackInfo();
                if (track != null) {
                    command.getMediator().sendMessage("Сейчас играет: " + track.title);
                } else {
                    command.getMediator().sendMessage("Я не знаю, что сейчас играет");
                }
            } catch (CommandFailure e) {
                command.getMediator().sendMessage("Все пропало шеф! Все, что нажито непосильным трудом: запросы, эхсепшоны, тян в высоких чулках. И музыка, кажется, тоже.");
            }
        }
    }

    @Override
    protected String getDescription() {
        return "Управление музыкой на канале посредством https://streamdj.ru/";
    }

    private void skipCurrentTrack() throws CommandFailure {
        HttpURLConnection connection = createConnection(API_URL + "request_skip/" + channelId + "/" + apiKey);
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Result result = (new Gson()).fromJson(rd, Result.class);
            if (result.success != 1) {
                throw new CommandFailure("Server couldn't skip current track");
            }
        } catch (IOException e) {
            throw new CommandFailure("Couldn't read response from server");
        } finally {
            connection.disconnect();
        }
    }

    private Track getCurrentTrackInfo() throws CommandFailure {
        HttpURLConnection connection = createConnection(API_URL + "get_track/" + channelId);
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            try {
                return (new Gson()).fromJson(rd, Track.class);
            } catch (RuntimeException e) {
                throw new CommandFailure("Server responded with invalid json");
            }
        } catch (IOException e) {
            throw new CommandFailure("Couldn't read response from server");
        } finally {
            connection.disconnect();
        }
    }

    private HttpURLConnection createConnection(String endpoint) {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(endpoint)).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            return connection;
        } catch (IOException e) {
            // TODO
            throw new RuntimeException("Couldn't create connection", e);
        }
    }
}
