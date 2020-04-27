import Bot.Bot;
import Bot.Command.*;
import GUI.AnnouncementText;
import Util.Logger.AggregatedLogger;
import Util.Logger.FileLogger;
import Util.Logger.Logger;
import Util.Stopwatch;
import com.google.gson.Gson;
import com.sun.javafx.scene.control.IntegerField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
    private Stopwatch uptime;

    public class Config {
        String channelName;
        String botName;
        String authToken;
        int djChannel = -1;
        String djToken = "";
        ArrayList<String> modules = new ArrayList<>();
        HashMap<Integer, AnnouncementText> announcements = new HashMap<>();
    }

    private Config config = new Config();

    private Bot chatBot;

    final class SetupPane extends GridPane {
        SetupPane(Stage window) {
            super();

            AtomicInteger row = new AtomicInteger(1);
            setAlignment(Pos.CENTER);
            setHgap(10);
            setVgap(10);
            setPadding(new Insets(25, 25, 25, 25));

            add(new Label("Bot name"), 0, row.get());
            TextField botNameInput =  new TextField();
            botNameInput.setPromptText("TwitchName");
            add(botNameInput, 1, row.getAndIncrement());

            add(new Label("Token"), 0, row.get());
            PasswordField tokenInput = new PasswordField();
            tokenInput.setPromptText("2zxgw4n3cfqwerty33ytrewqqwerty");
            add(tokenInput, 1, row.getAndIncrement());

            add(new Label("Channel name"), 0, row.get());
            TextField channelNameInput = new TextField();
            channelNameInput.setPromptText("TwitchChannelName");
            add(channelNameInput, 1, row.getAndIncrement());

            Text errorText = new Text();
            errorText.setWrappingWidth(150);
            errorText.setFill(Color.FIREBRICK);
            add(errorText, 1, row.getAndIncrement());

            add(createAnnouncementPane(), 0, row.getAndIncrement(), 2, 1);
            createModuleSelectors().forEach(moduleSelector -> add(moduleSelector, 0, row.getAndIncrement()));
            add(
                    createConfigLoadButton(
                        window,
                        channelNameInput,
                        botNameInput,
                        tokenInput,
                        errorText
                    ),
                    0,
                    row.getAndIncrement()
            );

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
                        Logger fileLogger = new FileLogger(
                                System.getProperty("user.dir") + "/" +  LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "chat-bot-log.txt"
                        );

                        connect(new AggregatedLogger(logPane, fileLogger), config);
                        window.setScene(new Scene(new FlowPane(Orientation.VERTICAL, 10, 10, logPane), 800, 600));
                    } else {
                        errorText.setText("Already connected");
                    }
                } catch (Throwable e) {
                    errorText.setText(e.getMessage());
                }
            });
            add(btn, 0, row.getAndIncrement());
        }

        private Button createConfigLoadButton(Stage window, TextField channel, TextField bot, TextField authToken, Text errorField) {
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
                            FileReader freader = new FileReader(file, Charset.forName("UTF-8"));
                            config = (new Gson()).fromJson(freader, Config.class);
                            normalizeConfig(config);
                            freader.close();
                            channel.setText(config.channelName);
                            bot.setText(config.botName);
                            authToken.setText(config.authToken);
                        } catch (FileNotFoundException e) {
                            errorField.setText("File not found");
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    }
            );

            return openButton;
        }

        private Collection<CheckBox> createModuleSelectors() {
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

            soundReaction.setSelected(false);
            rouletteGame.setSelected(false);
            twitchDj.setSelected(false);

            return Arrays.asList(soundReaction, rouletteGame, twitchDj);
        }

        private GridPane createAnnouncementPane() {
            AtomicInteger row = new AtomicInteger(0);

            GridPane announcementPane = new GridPane();
            announcementPane.setVgap(2);
            announcementPane.setHgap(2);
            final Insets padding = new Insets(0, 5, 25, 10);
            announcementPane.setPadding(padding);
            announcementPane.setStyle(
                    "-fx-border-color: gray;\n" +
                    "-fx-border-width: 1;\n" +
                    "-fx-border-style: dashed;\n"
            );

            ColumnConstraints col1Constraints = new ColumnConstraints();
            col1Constraints.setPercentWidth(80);
            ColumnConstraints col2Constraints = new ColumnConstraints();
            col2Constraints.setPercentWidth(10);
            ColumnConstraints col3Constraints = new ColumnConstraints();
            col3Constraints.setPercentWidth(10);
            announcementPane.getColumnConstraints().addAll(col1Constraints, col2Constraints, col3Constraints);

            int headersRow = row.getAndIncrement();
            announcementPane.add(new Label("Text"), 0, headersRow);
            announcementPane.add(new Label("Freq."), 1, headersRow);

            final Button newAnnouncementButton = new Button("New");
            newAnnouncementButton.setOnAction(
                    event -> {
                        int rowPosition = row.getAndIncrement();
                        TextField announcementInput = new TextField();
                        announcementInput.setPromptText("Write announcement text");
                        IntegerField frequencyInput = new IntegerField();

                        announcementInput.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue.isEmpty()) {
                                config.announcements.remove(rowPosition);
                            } else {
                                config.announcements.put(rowPosition, new AnnouncementText(newValue, frequencyInput.getValue()));
                            }
                        });

                        frequencyInput.valueProperty().addListener((observable, oldValue, newValue) -> {
                            config.announcements.put(rowPosition, new AnnouncementText(announcementInput.getText(), newValue.intValue()));
                        });

                        announcementPane.add(announcementInput, 0, rowPosition);
                        announcementPane.add(frequencyInput, 1, rowPosition);
                        final Button removalButton = new Button("-");
                        removalButton.setOnAction(
                                e -> {
                                    announcementPane.getChildren().removeAll(announcementInput, removalButton, frequencyInput);
                                    config.announcements.remove(rowPosition);
                                }
                        );
                        announcementPane.add(removalButton, 2, rowPosition);

                        GridPane.setRowIndex(newAnnouncementButton, row.getAndIncrement());
                    }
            );

            Label label = new Label("Announcements");
            label.setStyle("-fx-padding: 5 0 25 0;");
            announcementPane.add(label, 0, row.getAndIncrement());
            announcementPane.add(newAnnouncementButton, 0, row.getAndIncrement());

            return announcementPane;
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
            Platform.runLater(() -> logBox.setText(message + "\r\n" + logBox.getText()));
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

        chatBot.addChatCommand("tg stickers", "Стикеры в telegram https://t.me/addstickers/corgioncrack");
        chatBot.addChatCommand("vk", "Паблик https://vk.com/project_kaom");
        chatBot.addChatCommand("ds", "Дискорд https://discord.gg/tXu6Cze");
        chatBot.addChatCommand("youtube", "Тытруб https://www.youtube.com/channel/UC3NAFCI_cje-X5gF6woyADg");
        chatBot.addChatCommand("me", "https://github.com/Project-Kaom/twitch-community-awards/blob/master/achievements.md#" + Question.PLACEHOLDER_SENDER_NAME);
        chatBot.addChatCommand("uptime", () -> uptime.toString());

        config.announcements.forEach((pos, announcementText) -> chatBot.addAnnouncement(announcementText.getText(), announcementText.getFrequency()));

        chatBot.joinChannel(config.channelName);
        uptime = new Stopwatch();
    }

    private void disconnect() {
        if (chatBot != null) {
            chatBot.stop();
            chatBot = null;
            uptime = null;
        }
    }

    private void normalizeConfig(Config config) {
        if (config.modules == null) {
            config.modules = new ArrayList<>(0);
        }

        if (config.announcements == null) {
            config.announcements = new HashMap<>(0);
        }
    }
}
