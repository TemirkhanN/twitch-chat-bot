package Bot;

import Connection.Request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message extends Request{
    private String[] parts;

    private User sender;

    private static final Pattern userNamePattern = Pattern.compile(":([^ ]+)?!");

    public Message(String rawMessage) {
        super(rawMessage);

        parts = rawMessage.split(" ");

        Matcher m = userNamePattern.matcher(toString());
        if (m.find()) {
            sender = new User(m.group(1));
        }
    }

    public boolean isCommon() {
        return parts[1].equals("PRIVMSG");
    }

    public String getCommonPart() {
        if (!isCommon()) {
            return null;
        }

        StringBuilder commonMessage = new StringBuilder();
        for (int index=3; index<parts.length; index++) {
            commonMessage.append(parts[index]).append(" ");
        }

        // removes colon and unnecessary space at the end
        return commonMessage.substring(1).trim();
    }

    public User getSender() {
        return sender;
    }
}
