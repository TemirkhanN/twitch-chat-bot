package DJ;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class Spotify implements Dj {
    private static final String PLATFORM_NAME = "spotify";
    private static final String AUTH_URL = "https://accounts.spotify.com/api/token";
    private static final String API_CURRENT_TRACK = "https://api.spotify.com/v1/me/player/currently-playing";
    private static final String API_PLAY_NEXT_TRACK = "https://api.spotify.com/v1/me/player/next";

    private String credentials;
    private AuthToken auth;
    private String refreshToken;

    private class AuthToken {
        volatile String access_token;
        volatile String token_type;
        volatile int expires_in;
        volatile long instantiatedAtTime = 0;

        public String toString() {
            return token_type.substring(0, 1).toUpperCase() + token_type.substring(1) + " " + access_token;
        }

        void touch() {
            instantiatedAtTime = Instant.now().getEpochSecond();
        }

        boolean isValid() {
            // Delta to be sure that token is valid and usable for amount of delta time
            int delta = 10;
            long timePassedFromCreation = (Instant.now().getEpochSecond() - instantiatedAtTime);

            return timePassedFromCreation < (expires_in - delta);
        }

    }

    private class Track {
        boolean is_playing;
        Item item;

        public String toString() {
            StringBuilder trackFullName = new StringBuilder();
            trackFullName.append(item.getArtist());
            trackFullName.append(" - ");
            trackFullName.append(item.name);
            trackFullName.append(" ");
            String link = item.getLink();
            if (link != null) {
                trackFullName.append(link);
            }

            return trackFullName.toString();
        }

        class Item {
            String name;
            List<Artist> artists;
            TrackUrl external_urls;

            String getArtist() {
                return artists.stream()
                        .map(artist -> artist.name)
                        .collect(Collectors.joining(" feat. "));
            }

            String getLink() {
                return external_urls.spotify;
            }
        }

        class Artist {
            String name;
        }

        class TrackUrl {
            String spotify;
        }
    }

    public Spotify(String clientId, String clientSecret, String refreshToken) {
        credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        this.refreshToken = refreshToken;
    }

    @Override
    public DJ.Track getCurrentTrack() {
        if (auth == null || !auth.isValid()) {
            authorize();
        }

        HttpsURLConnection connection = createConnection(API_CURRENT_TRACK, "GET");
        String token = auth.toString();
        connection.setRequestProperty("Authorization", token);

        try {
            try {
                connection.connect();
                if (connection.getResponseCode() == 404) {
                    return null;
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Track track = (new Gson()).fromJson(rd, Track.class);
                if (!track.is_playing) {
                    return null;
                }

                return new DJ.Track(track.toString(), PLATFORM_NAME);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't read from server", e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Server responded with invalid json", e);
        } finally {
            connection.disconnect();
        }
    }

    @Override
    public void skipCurrentTrack() {
        if (auth == null || !auth.isValid()) {
            authorize();
        }

        HttpsURLConnection connection = createConnection(API_PLAY_NEXT_TRACK, "POST");
        String token = auth.toString();
        connection.setRequestProperty("Authorization", token);
        connection.setFixedLengthStreamingMode(0);

        try {
            connection.connect();
            if (connection.getResponseCode() != 204) {
                throw new RuntimeException("Couldn't skip track. Is it really playing?");
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read from server", e);
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

    private void authorize() {
        try {
            HttpsURLConnection connection = createConnection(AUTH_URL, "POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            OutputStream outputStream = connection.getOutputStream();

            String body = "grant_type=refresh_token&refresh_token=" + refreshToken;
            outputStream.write(body.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
            connection.connect();

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            auth = (new Gson()).fromJson(rd, AuthToken.class);
            auth.touch();
        } catch (Exception e) {
            throw new RuntimeException("Error while authorizing", e);
        }
    }

    private HttpsURLConnection createConnection(String endpoint, String method) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) (new URL(endpoint)).openConnection();
            connection.setRequestMethod(method);
            if (method.equals("POST")) {
                connection.setDoOutput(true);
            }
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/json");

            return connection;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create connection", e);
        }
    }
}
