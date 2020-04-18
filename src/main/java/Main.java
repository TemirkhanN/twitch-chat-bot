import Bot.Bot;
import Bot.Command.*;
import Util.Logger.AggregatedLogger;
import Util.Logger.FileLogger;
import Util.Logger.Logger;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class Main extends Application {
    public class Config {
        String channelName;
        String botName;
        String authToken;
        int djChannel = -1;
        String djToken = "";
        ArrayList<String> modules = new ArrayList<>();
    }

    private Config config = new Config();

    private Bot chatBot;

    final class SetupPane extends GridPane {
        SetupPane(Stage window) {
            super();

            setAlignment(Pos.CENTER);
            setHgap(10);
            setVgap(10);
            setPadding(new Insets(25, 25, 25, 25));

            add(new Label("Bot name"), 0, 1);
            TextField botNameInput =  new TextField();
            add(botNameInput, 1, 1);

            add(new Label("Token"), 0, 2);
            PasswordField tokenInput = new PasswordField();
            add(tokenInput, 1, 2);

            add(new Label("Channel name"), 0, 3);
            TextField channelNameInput = new TextField();
            add(channelNameInput, 1, 3);

            Text errorText = new Text();
            errorText.setWrappingWidth(150);
            errorText.setFill(Color.FIREBRICK);
            add(errorText, 1, 5);

            createConfigLoadButton(window, channelNameInput, botNameInput, tokenInput, errorText);

            createModuleSelectors();

            Button btn = new Button();
            btn.setText("Connect");
            btn.setOnAction(event -> {
                config.botName = botNameInput.getText();
                config.authToken = tokenInput.getText();
                config.channelName = channelNameInput.getText();

                ArrayList<String> errors = new ArrayList<>();
                if (config.botName.length() < 4) {
                    errors.add("Bot name is invalid");
                }

                if (config.authToken.length() != 30) {
                    errors.add("Auth token is invalid");
                }

                if (config.channelName.length() < 4) {
                    errors.add("Channel name is invalid");
                }

                if (errors.size() != 0) {
                    String errorMessage = "";
                    for (String error: errors) {
                        errorMessage += "\r\n" + error;
                    }

                    errorText.setText(errorMessage);

                    return;
                }
                errorText.setText("");

                try {
                    if (chatBot == null) {
                        LogPane logPane = new LogPane(500, 400);
                        window.setScene(new Scene(new FlowPane(Orientation.VERTICAL, 10, 10, logPane), 800, 600));
                        Logger fileLogger = new FileLogger(System.getProperty("user.dir") + "/chat-bot-log.txt");

                        connect(new AggregatedLogger(logPane, fileLogger), config);
                    } else {
                        errorText.setText("Already connected");
                    }
                } catch (Throwable e) {
                    errorText.setText(e.getMessage());
                }
            });
            add(btn, 0, 9);
        }

        private void createConfigLoadButton(Stage window, TextField channel, TextField bot, TextField authToken, Text errorField) {
            FileChooser configLoader = new FileChooser();
            configLoader.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ext", "*.json"));
            final Button openButton = new Button("Load config from file");
            openButton.setOnAction(
                    event -> {
                        File file = configLoader.showOpenDialog(window);
                        if (file == null) {
                            return;
                        }

                        try {
                            BufferedReader freader = new BufferedReader(new FileReader(file));
                            Config loadedConfig = (new Gson()).fromJson(freader, Config.class);
                            channel.setText(loadedConfig.channelName);
                            bot.setText(loadedConfig.botName);
                            authToken.setText(loadedConfig.authToken);
                        } catch (FileNotFoundException e) {
                            errorField.setText("File not found");
                        }
                    }
            );

            add(openButton, 0, 4);
        }

        private void createModuleSelectors() {
            // TODO Duplicates and literals should be replaced
            CheckBox soundReaction = new CheckBox("Sound reactions");
            soundReaction.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (new_val && !old_val) {
                    config.modules.add("SoundReaction");
                } else {
                    config.modules.remove("SoundReaction");
                }
            });

            CheckBox rouletteGame = new CheckBox("Roulette game");
            rouletteGame.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (new_val && !old_val) {
                    config.modules.add("RussianRoulette");
                } else {
                    config.modules.remove("RussianRoulette");
                }
            });

            CheckBox twitchDj = new CheckBox("TwitchDJ");
            twitchDj.selectedProperty().addListener((ov, old_val, new_val) -> {
                if (new_val && !old_val) {
                    config.modules.add("TwitchDJ");
                } else {
                    config.modules.remove("TwitchDJ");
                }
            });

            soundReaction.setSelected(true);
            rouletteGame.setSelected(true);
            twitchDj.setSelected(true);
            add(soundReaction, 0, 6);
            add(rouletteGame, 0, 7);
            add(twitchDj, 0, 8);
        }
    }

    final class LogPane extends ScrollPane implements Logger {
        private Label logBox;

        LogPane(double width, double height) {
            super();

            setPadding(new Insets(25, 25, 25, 25));
            setWidth(width);
            setPrefViewportHeight(height);

            logBox = new Label();
            setContent(logBox);
        }

        @Override
        public void log(String message) {
            Platform.runLater(() -> logBox.setText(logBox.getText() + "\r\n" + message));
        }
    }

    @Override
    public void start(Stage window) {
        window.setTitle("Twitch Bot");

        window.setScene(new Scene(new SetupPane(window), 400, 500));
        window.setOnCloseRequest(event -> disconnect());
        window.show();
    }

    private void connect(Logger logger, Config config) {
        chatBot = new Bot(config.botName, config.authToken);
        chatBot.setLogger(logger);

        // Register chat commands handlers
        if (config.modules.contains("SoundReaction")) {
            chatBot.addChatHandler(new SoundReaction());
        }

        if (config.modules.contains("RussianRoulette")) {
            chatBot.addChatHandler(new RussianRoulette());
        }

        if (config.modules.contains("TwitchDJ")) {
            chatBot.addChatHandler(new DJ(config.djChannel, config.djToken));
        }

        Question question = new Question();
        question.addAnswer("!tg stickers", "Стикеры в telegram https://t.me/addstickers/corgioncrack");
        question.addAnswer("!vk", "Паблик https://vk.com/project_kaom");
        question.addAnswer("!youtube", "Канал https://www.youtube.com/channel/UC3NAFCI_cje-X5gF6woyADg");
        question.addAnswer("!whoami", "https://github.com/Project-Kaom/twitch-community-awards/blob/master/achievements.md#" + Question.PLACEHOLDER_SENDER_NAME);
        chatBot.addChatHandler(question);

        chatBot.addAnnouncement("Новые звуковые реакции: медик, ретард, бадум", 40);
        chatBot.addAnnouncement("Чуваки, греки воевали, но демократии нам не досталось, поэтому давайте без грязи - забаню без суда и следствия.", 60);
        chatBot.addAnnouncement("Аудио-поток регулируется здесь: https://streamdj.ru/c/Project_Kaom", 31);
        chatBot.addAnnouncement("Новые реакции и идеи для бота можно предложить в https://discord.gg/tXu6Cze", 60);

        chatBot.joinChannel(config.channelName);
    }

    private void disconnect() {
        if (chatBot != null) {
            chatBot.stop();
            chatBot = null;
        }
    }
}
