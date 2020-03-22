package Bot.Command;

import Bot.Bot;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.util.HashMap;

public class SoundReaction extends CommandHandler {
    private static HashMap<String, String> reactions = new HashMap<>(8);

    // TODO Move to CDN. Also it may be cool to extend reactions in runtime through GUI
    static {
        reactions.put("вут", "watafak");
        reactions.put("ахем", "ahem.spy");
        reactions.put("гоп", "gotcha");
        reactions.put("хехе", "hehe-boy");
        reactions.put("привет", "hello-there");
        reactions.put("ой", "alert");
        reactions.put("сюрприз", "surprise-motherfucker");
        reactions.put("остынь", "directedby");
    }

    SoundReaction(String command) {
        super(command.toLowerCase());
    }

    public boolean isValidCommand() {
        return exists(command);
    }

    protected void run(String Command, Bot executor) {
        if (!isValidCommand()) {
            return;
        }

        playReaction(command);
    }

    private boolean exists(String reactionName) {
        return getReactionSoundPath(reactionName) != null;
    }

    private void playReaction(String reactionName) {
        if (!exists(reactionName)) {
            return;
        }

        String soundPath = getReactionSoundPath(reactionName);
        playSound(soundPath);
    }

    private String getReactionSoundPath(String reactionName) {
        String soundName = reactions.get(reactionName);
        if (soundName == null) {
            return null;
        }

        return "Sounds/" + soundName + ".wav";
    }

    private void playSound(String soundFile) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                        new BufferedInputStream(getClass().getClassLoader().getResourceAsStream(soundFile))
                );
                clip.open(inputStream);
                clip.addLineListener(myLineEvent -> {
                    if (myLineEvent.getType() == LineEvent.Type.STOP)
                        clip.close();
                });
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                // TODO
                System.err.println(e.toString());
            }
        }).start();
    }
}
