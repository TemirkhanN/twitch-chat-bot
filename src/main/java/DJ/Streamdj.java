package DJ;

import DJ.Exception.ServerError;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Streamdj implements Dj {
    private static final String PLATFORM_NAME = "streamdj";

    private static final String API_URL = "https://streamdj.ru/api/";

    private String apiKey;
    private int channelId;

    public class Track {
        String title;
    }

    public class Result {
        int success;
    }

    public Streamdj(int channelId, String apiKey) {
        this.apiKey = apiKey;
        this.channelId = channelId;
    }

    @Override
    public DJ.Track getCurrentTrack() {
        HttpURLConnection connection = createConnection(API_URL + "get_track/" + channelId);
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            try {
                Track track = (new Gson()).fromJson(rd, Track.class);
                rd.close();

                return new DJ.Track(track.title, PLATFORM_NAME);
            } catch (RuntimeException e) {
                throw new ServerError(getName(), "Server responded with invalid json", e);
            }
        } catch (IOException e) {
            throw new ServerError(getName(), "Couldn't read response from server", e);
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public boolean isPlaying(DJ.Track track) {
        DJ.Track currentTrack = getCurrentTrack();
        if (currentTrack == null) {
            return false;
        }

        return track.equals(currentTrack);
    }

    @Override
    public void skipCurrentTrack() {
        HttpURLConnection connection = createConnection(API_URL + "request_skip/" + channelId + "/" + apiKey);
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Result result = (new Gson()).fromJson(rd, Result.class);
            if (result.success != 1) {
                throw new ServerError(getName(), "Server couldn't skip current track");
            }
        } catch (IOException e) {
            throw new ServerError(getName(), "Couldn't read response from server");
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public String getName() {
        return "StreamDJ";
    }

    private HttpURLConnection createConnection(String endpoint) {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(endpoint)).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);

            return connection;
        } catch (IOException e) {
            throw new RuntimeException(getName() + ": couldn't create connection", e);
        }
    }
}
