package Bot;

import Connection.*;

import java.io.IOException;

public class Channel {
    private String name;
    private Connection connection;
    private boolean keepConnectionAlive;

    public Channel(String channelName) {
        name = channelName;
        keepConnectionAlive = false;

        try {
            connection = new Connection();
        } catch (IOException e) {
            // TODO
        }
    }

    public void keepConnectionAlive() {
        keepConnectionAlive = true;
    }

    public void join(String botName, String token) {
        Request[] joiningMessages = {
                new Request("PASS oauth:" + token),
                new Request("NICK " + botName),
                new Request("JOIN #" + name)
        };
        try {
            connection.send(joiningMessages);
        } catch (IOException e) {
            // TODO
        }
    }

    public void sendMessage(String message) {
        try {
            connection.send(new Request("PRIVMSG #" + name + " :" + message));
        } catch (IOException e) {
            // TODO
        }
    }

    public void leave() {
        try {
            connection.send(new Request("PART #" + name));
        } catch (IOException e) {
            // TODO
        }
        connection.close();
    }

    public Message readMessage() throws IOException {
        String data = connection.dataReceiver.readLine();
        Message message = new Message(data);
        if (message.isPing() && keepConnectionAlive) {
            connection.send(Request.acknowledgeAlive());
        }

        return message;
    }
}
