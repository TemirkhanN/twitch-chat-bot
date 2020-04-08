import Bot.Bot;
import Bot.Command.RussianRoulette;
import Bot.Command.SoundReaction;

import java.io.*;
import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) {
        String channel = args[0];
        String botName = args[1];
        String authToken = args[2];

        Bot chatBot = new Bot(botName, authToken);
        Charset charset = Charset.forName("utf-8");
        try {
            chatBot.setLogger(
                    new PrintWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(System.getProperty("user.dir") + "/chat-bot-log.txt"),
                                    charset
                            )
                    )
            );
        } catch (IOException e) {
            chatBot.setLogger(new BufferedWriter(new OutputStreamWriter(System.out, charset)));
        }

        // Register chat commands handlers
        chatBot.addChatHandler(new SoundReaction());
        chatBot.addChatHandler(new RussianRoulette());

        chatBot.addAnnouncement("Звуковые реакции в описании канала.", 20);
        chatBot.addAnnouncement("Аудио-поток регулируется здесь: https://streamdj.ru/c/Project_Kaom", 31);
        chatBot.addAnnouncement("Новые реакции и идеи для бота можно предложить в https://discord.gg/tXu6Cze", 36);

        chatBot.joinChannel(channel);
    }
}
