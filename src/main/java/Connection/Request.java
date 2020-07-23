package Connection;

public class Request {
    public static final String LINE_BREAK = "\r\n";

    private String raw;

    public Request(String data) {
        raw = data + LINE_BREAK;
    }

    public static Request acknowledgeAlive() {
        return new Request("PONG :tmi.twitch.tv");
    }

    public boolean isPing() {
        return raw.equals("PING :tmi.twitch.tv" + LINE_BREAK);

    }

    public String toString() {
        return raw;
    }
}
