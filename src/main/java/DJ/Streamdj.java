package DJ;

import DJ.Exception.ServerError;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Streamdj implements Dj {
    private static final String PLATFORM_NAME = "streamdj";

    private static final String API_URL = "https://streamdj.ru/api/";

    private String apiKey;
    private int channelId;


    private HttpClient transport;

    public class Track {
        String title;
        String yid;

        @Override
        public String toString() {
            return title + " https://www.youtube.com/watch?v=" + yid;
        }
    }

    public class Result {
        int success;
    }

    public Streamdj(int channelId, String apiKey) {
        this.apiKey = apiKey;
        this.channelId = channelId;
        this.transport = HttpClientBuilder.create().build();
    }

    @Override
    public DJ.Track getCurrentTrack() {
        try {
            HttpResponse response = transport.execute(new HttpGet(API_URL + "get_track/" + channelId));
            if (response.getStatusLine().getStatusCode() != 200) {
                return null;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            try {
                String responseContent = rd.lines().collect(Collectors.joining(System.lineSeparator()));
                rd.close();
                // Handler for super shitty API from streamDJ
                if (responseContent.isEmpty() || responseContent.equals("null")) {
                    return null;
                }

                Track track = (new Gson()).fromJson(responseContent, Track.class);

                return new DJ.Track(track.toString(), PLATFORM_NAME);
            } catch (RuntimeException e) {
                throw new ServerError(getName(), "Server responded with invalid json", e);
            }
        } catch (IOException e) {
            throw new ServerError(getName(), "Couldn't read response from server", e);
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
        try {
            HttpResponse response = transport.execute(new HttpGet(API_URL + "request_skip/" + channelId + "/" + apiKey));
            if (response.getStatusLine().getStatusCode() != 200) {
                return;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            Result result = (new Gson()).fromJson(rd, Result.class);
            if (result.success != 1) {
                throw new ServerError(getName(), "Server couldn't skip current track");
            }
        } catch (IOException e) {
            throw new ServerError(getName(), "Couldn't read response from server");
        }
    }

    @Override
    public String getName() {
        return "StreamDJ";
    }
}
