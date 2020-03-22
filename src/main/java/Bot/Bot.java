package Bot;

import Bot.Command.CommandHandler;
import Bot.Command.Parser;

import java.io.IOException;

public class Bot {
    private String name;
    private String token;

    private Channel channel;

    public Bot(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public void joinChannel(String channelName) {
        channel = new Channel(channelName);
        channel.join(name, token);
        channel.keepConnectionAlive();

        listenToChat();
    }

    public void sendMessage(String message) {
        channel.sendMessage(message);
    }

    private void listenToChat() {
        class ChatCommandHandle implements Runnable {
            private Bot handler;

            private ChatCommandHandle(Bot handler) {
                this.handler = handler;
            }

            public void run() {
                try {
                    Message message;
                    while ((message = channel.readMessage()) != null) {
                        System.out.println(message.toString());
                        if (!message.isCommon()) {
                            continue;
                        }

                        CommandHandler commandHandler = Parser.parseCommand(message.getCommonPart());
                        if (commandHandler != null) {
                            commandHandler.handleBy(handler);
                        }
                    }
                    channel.leave();
                } catch (IOException e) {
                    // TODO
                    System.out.println(e.toString());
                    channel.leave();
                }
            }
        }

        new Thread(new ChatCommandHandle(this)).start();
    }
}
