package Bot;

import Bot.Command.Command;
import Bot.Command.CommandBus;
import Bot.Command.CommandHandler;
import Util.Logger.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot  extends User {
    private String token;
    private Channel channel;
    private CommandBus commandBus;
    private ArrayList<Announcement> singleTimeAnnouncements;
    private ArrayList<Announcement> repeatingAnnouncements;

    // TODO separate into full logger
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private Logger logger;

    public Bot(String name, String token) {
        super(name);

        this.token = token;
        commandBus = new CommandBus();
        singleTimeAnnouncements = new ArrayList<>();
        repeatingAnnouncements = new ArrayList<>();
    }

    public void setLogger(Logger logWriter) {
        if (logWriter == null) {
            return;
        }

        logger = logWriter;
    }

    String getToken() {
        return token;
    }

    public void joinChannel(String channelName) {
        channel = new Channel(channelName);
        channel.join(this);
        channel.keepConnectionAlive();

        handleAnnouncements();
        listenToChat();
    }

    public void stop() {
        if (channel == null) {
            return;
        }

        channel.leave();
        logger = null;
    }

    public void addAnnouncement(String text) {
        singleTimeAnnouncements.add(new Announcement(text, this));
    }

    public void addAnnouncement(String text, int everyNMinitues) {
        try {
            repeatingAnnouncements.add(new Announcement(text, this, everyNMinitues));
        } catch (Exception e) {
            // TODO
        }
    }

    public void sendMessage(String message) {
        channel.sendMessage(message);
    }

    public void whisper(User to, String message) {
        channel.sendMessage("@" + to.getName() + ", " + message);
    }

    public void whisper(String to, String message) {
        channel.sendMessage("@" + to + ", " + message);
    }

    public void addChatHandler(CommandHandler handler) {
        commandBus.registerHandler(handler);
    }

    private void handleAnnouncements() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(repeatingAnnouncements.size());
        for (Announcement announcement : singleTimeAnnouncements) {
            executor.execute(announcement);
        }

        for (Announcement announcement : repeatingAnnouncements) {
            int announcementFrequency = announcement.getFrequency();
            executor.scheduleAtFixedRate(announcement, announcementFrequency, announcementFrequency, TimeUnit.MINUTES);
        }
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
                    String command;
                    while ((message = channel.readMessage()) != null) {
                        log(message.toString());
                        // We don't handle non common messages received in chat
                        command = message.getCommonPart();
                        if (command != null) {
                            commandBus.execute(new Command(command, message.getSender(), handler));
                        }
                    }
                } catch (IOException e) {
                    // TODO
                    System.err.println(e.toString());
                } finally {
                    stop();
                }
            }
        }

        new Thread(new ChatCommandHandle(this)).start();
    }

    private void log(String data) {
        String formattedMessage = dateFormatter.format(new Date()) + ": " + data;

        if (logger == null) {
            System.out.println(formattedMessage);

            return;
        }

        logger.log(formattedMessage);
    }
}
