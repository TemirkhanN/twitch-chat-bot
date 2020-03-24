package Bot.Command.Util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioPlayer {
    private Clip player;

    private static AudioPlayer instance;

    private AudioPlayer() {
        try {
            player = AudioSystem.getClip();
            player.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    player.close();
                }
            });
        } catch (LineUnavailableException e) {
            throw new RuntimeException("Couldn't initialize player", e);
        }
    }

    public static AudioPlayer getPlayer() {
        if (instance == null) {
            instance = new AudioPlayer();
        }

        return instance;
    }

    public void play(String audioFile) {
        // No audio intersection
        if (player.isRunning()) {
            return;
        }

        InputStream fileStream = getClass().getClassLoader().getResourceAsStream(audioFile);
        if (fileStream == null) {
            throw new RuntimeException("Couldn't get " + audioFile + ". Make sure it exists in resources.");
        }

        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(fileStream));
            player.open(inputStream);
            player.setFramePosition(0);
            player.start();
        } catch (Exception error) {
            // TODO
            player.close();
            throw new RuntimeException("Audio playing failure", error);
        }
    }
}
