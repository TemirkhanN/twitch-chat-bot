package Bot.Command;

import Bot.Bot;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.util.HashMap;

public class SoundReaction extends CommandHandler {
    private static HashMap<String, String> reactions = new HashMap<>();

    // TODO Move to CDN. Also it may be cool to extend reactions in runtime through GUI
    static {
        reactions.put("бу", "boo");
        reactions.put("остынь", "directed-by");
        reactions.put("хехе", "hehe-boy");
        reactions.put("привет", "hello");
        reactions.put("оу май", "oh-my");
        reactions.put("rip", "rip");
        reactions.put("сюрприз", "surprise-motherfucker");
        reactions.put("the end", "to-be-continued");
        reactions.put("втф", "wtfit");
        reactions.put("штоето", "wtfit");
        reactions.put("вот это поворот", "what-a-turn");
        reactions.put("ваау", "woooow");

        //hidden
        reactions.put("fap", "hidden/fap-fap");
        reactions.put("какого", "hidden/huya");
        reactions.put("johncena", "hidden/john-cena");
        reactions.put("ненене", "hidden/nononono");
        reactions.put("flick", "hidden/ricardo");
        reactions.put("добро пожаловать сука", "hidden/rice-fields");
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

        return "sound/" + soundName + ".wav";
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
                    if (myLineEvent.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
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
