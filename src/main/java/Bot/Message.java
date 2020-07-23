package Bot;

import Connection.Request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message extends Request{
    private String[] parts;

    private User sender;

    private static final Pattern userNamePattern = Pattern.compile(":([^ ]+)?!");
    private static final Pattern channelOwnerPattern = Pattern.compile("PRIVMSG #([^ ]+)?");

    public Message(String rawMessage) {
        super(rawMessage);

        parts = rawMessage.split(" ");

        Matcher nameMatcher         = userNamePattern.matcher(rawMessage);
        Matcher channelOwnerMatcher = channelOwnerPattern.matcher(rawMessage);
        if (nameMatcher.find()) {
            String userName = nameMatcher.group(1);
            boolean isAdmin = false;
            if (channelOwnerMatcher.find()) {
                isAdmin = userName.equals(channelOwnerMatcher.group(1));
            }

            sender = new User(userName, isAdmin);
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
