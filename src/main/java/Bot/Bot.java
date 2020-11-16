package Bot;

import Bot.Command.*;
import Util.Logger.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends User implements OutputInterface {
    private String token;
    private Channel channel;
    private CommandHandlerInterface chatHandler;
    private ArrayList<Announcement> singleTimeAnnouncements;
    private ArrayList<Announcement> repeatingAnnouncements;
    private ScheduledExecutorService announcer;

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Logger logger;

    public Bot(String name, String token) {
        super(name, true);

        this.token = token;
        singleTimeAnnouncements = new ArrayList<>();
        repeatingAnnouncements = new ArrayList<>();
    }

    public void setLogger(Logger logWriter) {
        if (logWriter == null) {
            return;
        }

        logger = logWriter;
    }

    public void setChatHandler(CommandHandlerInterface handler) {
        this.chatHandler = handler;
    }

    String getToken() {
        return token;
    }

    public void joinChannel(String channelName) {
        channel = new Channel(channelName);
        channel.join(this);
        channel.keepConnectionAlive();

        scheduleAnnouncements();
        listenToChat();
    }

    public void stop() {
        if (channel == null) {
            return;
        }

        stopAnnouncements();

        channel.leave();
        channel = null;
        if (logger != null) {
            logger.close();
        }
    }

    public void addAnnouncement(String text, int everyNMinitues) {
        if (everyNMinitues == 0) {
            singleTimeAnnouncements.add(new Announcement(text, this));
        } else {
            repeatingAnnouncements.add(new Announcement(text, this, everyNMinitues));
        }
    }

    @Override
    public void write(String message) {
        if (channel == null) {
            throw new RuntimeException("Bot can not write messages while not connected");
        }

        log(getName() + " PRIVMSG #" + channel.getName() + " :" + message);
        channel.sendMessage(message);
    }

    private void scheduleAnnouncements() {
        announcer = Executors.newScheduledThreadPool(repeatingAnnouncements.size());
        for (Announcement announcement : singleTimeAnnouncements) {
            announcer.execute(announcement);
        }

        for (Announcement announcement : repeatingAnnouncements) {
            int announcementFrequency = announcement.getFrequency();
            announcer.scheduleAtFixedRate(announcement, announcementFrequency, announcementFrequency, TimeUnit.MINUTES);
        }
    }

    private void stopAnnouncements() {
        announcer.shutdownNow();
        announcer = null;
    }

    private void listenToChat() {
        if (chatHandler == null) {
            return;
        }

        class ChatCommandHandle implements Runnable {
            public void run() {
                try {
                    Message message;
                    String command;
                    while (channel != null && (message = channel.readMessage()) != null) {
                        log(message.toString());
                        command = message.getCommonPart();
                        if (command != null) {
                            chatHandler.handle(new Command(command, message.getSender()));
                        }
                    }
                } catch (IOException | RuntimeException e) {
                    log(e.toString());
                    if (channel != null) {
                        joinChannel(channel.getName());
                    }
                }
            }
        }

        new Thread(new ChatCommandHandle()).start();
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
