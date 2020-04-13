import Bot.Bot;
import Bot.Command.*;

import java.io.*;
import java.nio.charset.Charset;

public class Main {
    public static void main(String[] args) {
        String channel = args[0];
        String botName = args[1];
        String authToken = args[2];
        String twitchDjKey = args[3];
        int twitchDjChannel = Integer.parseInt(args[4]);

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
        chatBot.addChatHandler(new DJ(twitchDjChannel, twitchDjKey));

        Question question = new Question();
        question.addAnswer("!tg stickers", "Стикеры в telegram https://t.me/addstickers/corgioncrack");
        question.addAnswer("!vk", "Паблик https://vk.com/project_kaom");
        question.addAnswer("!youtube", "Канал https://www.youtube.com/channel/UC3NAFCI_cje-X5gF6woyADg");
        question.addAnswer("!whoami", "https://github.com/Project-Kaom/twitch-community-awards/blob/master/achievements.md#" + Question.PLACEHOLDER_SENDER_NAME);
        chatBot.addChatHandler(question);

        chatBot.addAnnouncement("Звуковые реакции и команды для бота в описании канала.", 20);
        chatBot.addAnnouncement("Аудио-поток регулируется здесь: https://streamdj.ru/c/Project_Kaom", 31);
        chatBot.addAnnouncement("Новые реакции и идеи для бота можно предложить в https://discord.gg/tXu6Cze", 60);

        chatBot.joinChannel(channel);
    }
}
