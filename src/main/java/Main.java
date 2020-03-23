import Bot.Announcement;
import Bot.Bot;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        String channel = args[0];
        String botName = args[1];
        String authToken = args[2];

        Bot chatBot = new Bot(botName, authToken);
        chatBot.joinChannel(channel);

        try {
            addAnnouncements(chatBot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addAnnouncements(Bot bot) throws Exception {
        ArrayList<Announcement> announcements = new ArrayList<Announcement>(2);
        announcements.add(
                new Announcement(
                        "Бот присоединяется к вечеринке!",
                        bot
                )
        );
        announcements.add(
                new Announcement(
                        "Ищите звуковые реакции на стриме в описании канала. Хорошего настроения!",
                        bot,
                        15
                )
        );

        for (Announcement announcement : announcements) {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            int announcementFrequency = announcement.getFrequency();
            if (announcement.isRepetitive()) {
                executor.scheduleAtFixedRate(announcement, announcementFrequency, announcementFrequency, TimeUnit.MINUTES);
            } else {
                executor.execute(announcement);
            }
        }
    }
}
