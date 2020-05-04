package Util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioPlayer {
    private Clip clip;

    private static AudioPlayer instance;

    private AudioPlayer() {
        try {
            clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
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
        if (clip.isRunning()) {
            return;
        }

        InputStream fileStream = getClass().getClassLoader().getResourceAsStream(audioFile);
        if (fileStream == null) {
            throw new RuntimeException("Couldn't get " + audioFile + ". Make sure it exists in resources.");
        }

        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(fileStream));
            clip.open(inputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(gainControl.getMaximum());
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception error) {
            // TODO
            clip.close();
            throw new RuntimeException("Audio playing failure", error);
        }
    }
}
