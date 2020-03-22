package Bot;

import java.util.TimerTask;

public class Announcement extends TimerTask {
    private static final int ONE_TIME_ANNOUNCEMENT_FREQUENCY = 0;
    private static final int MIN_FREQUENCY = 5;
    private Bot announcer;
    private String text;
    private int frequencyInMinutes;

    public Announcement(String text, Bot announcer) {
        this.text = text;
        this.announcer = announcer;
        frequencyInMinutes = ONE_TIME_ANNOUNCEMENT_FREQUENCY;
    }

    public Announcement(String text, Bot announcer, int frequencyInMinutes) throws Exception {
        this.text = text;
        this.announcer = announcer;
        if (frequencyInMinutes < MIN_FREQUENCY) {
            throw new Exception("You can send announcement more often than once in " + MIN_FREQUENCY + " minutes");
        }
        this.frequencyInMinutes = frequencyInMinutes;
    }

    public int getFrequency() {
        return frequencyInMinutes;
    }

    public boolean isRepetitive() {
        return frequencyInMinutes != ONE_TIME_ANNOUNCEMENT_FREQUENCY;
    }

    public void run() {
        // TODO do we rely on other scheduler class about execution and states?
        announcer.sendMessage(text);
        if (!isRepetitive()) {
            cancel();
        }
    }
}
