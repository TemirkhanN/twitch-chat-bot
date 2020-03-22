package Bot;

import Connection.Request;

public class Message extends Request{
    private String[] parts;

    public Message(String rawMessage) {
        super(rawMessage);

        parts = rawMessage.split(" ");
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
}
