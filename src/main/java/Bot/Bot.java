package Bot;

import Bot.Command.*;
import Util.Logger.Logger;
import Util.Stopwatch;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Bot extends User implements OutputInterface {
    private static final String CHAT_COMMAND_PREFIX = "!";
    private String token;
    private Channel channel;
    private CommandBus commandBus;
    private ArrayList<Announcement> singleTimeAnnouncements;
    private ArrayList<Announcement> repeatingAnnouncements;
    private Question chatCommandHandler;

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Logger logger;

    private Stopwatch uptime;

    public Bot(String name, String token) {
        super(name, true);

        this.token = token;
        commandBus = new CommandBus();
        singleTimeAnnouncements = new ArrayList<>();
        repeatingAnnouncements = new ArrayList<>();
        chatCommandHandler = new Question();
        addChatHandler(chatCommandHandler);
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
        uptime = new Stopwatch();
        addChatCommand("uptime", () -> uptime.toString());
    }

    public void stop() {
        if (channel == null) {
            return;
        }

        for (Announcement announcement : repeatingAnnouncements) {
            announcement.cancel();
        }

        channel.leave();
        channel = null;
        if (logger != null) {
            logger.close();
        }
        uptime = null;
    }

    public void addAnnouncement(String text, int everyNMinitues) {
        if (everyNMinitues == 0) {
            singleTimeAnnouncements.add(new Announcement(text, this));
        } else {
            repeatingAnnouncements.add(new Announcement(text, this, everyNMinitues));
        }
    }

    public void addChatHandler(CommandHandler handler) {
        commandBus.registerHandler(handler);
    }

    public void addChatCommand(String command, String response) {
        chatCommandHandler.addAnswer(CHAT_COMMAND_PREFIX + command, response);
    }

    public void addChatCommand(String command, Supplier<String> response) {
        chatCommandHandler.addAnswer(CHAT_COMMAND_PREFIX + command, response);
    }

    @Override
    public void write(String message) {
        if (channel == null) {
            throw new RuntimeException("Bot can not write messages while not connected");
        }

        log(getName() + " PRIVMSG #"+ channel.getName() + " :" + message);
        channel.sendMessage(message);
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
            private OutputInterface outputWriter;

            private ChatCommandHandle(OutputInterface outputWriter) {
                this.outputWriter = outputWriter;
            }

            public void run() {
                try {
                    Message message;
                    String command;
                    while (channel != null && (message = channel.readMessage()) != null) {
                        log(message.toString());
                        command = message.getCommonPart();
                        if (command != null) {
                            commandBus.execute(new Command(command, message.getSender()), outputWriter);
                        }
                    }
                } catch (IOException|RuntimeException e) {
                    log(e.toString());
                    if (channel != null) {
                        joinChannel(channel.getName());
                    }
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
