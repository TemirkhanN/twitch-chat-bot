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
            chatBot.setLogger(new PrintWriter(System.getProperty("user.dir") + "/chat-bot-log.txt", charset));
        } catch (IOException e) {
            chatBot.setLogger(new BufferedWriter(new OutputStreamWriter(System.out, charset)));
        }

        // Register chat commands handlers
        chatBot.addChatHandler(new SoundReaction());
        chatBot.addChatHandler(new RussianRoulette());

        chatBot.addAnnouncement("Бот присоединяется к вечеринке");
        chatBot.addAnnouncement("Сообщения, вызывающие звуковые реакции на стриме, указаны в описании канала.", 20);
        chatBot.addAnnouncement("Поделитесь интересными и(ли) безумные идеи для бота. Топчик обязательно будет добавлен в функционал.", 35);

        chatBot.joinChannel(channel);
    }
}
