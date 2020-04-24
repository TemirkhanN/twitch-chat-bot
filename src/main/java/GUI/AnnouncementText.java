package GUI;

public class AnnouncementText {
    private String text;

    private int frequency;

    public AnnouncementText(String text, int frequency) {
        this.text = text;
        this.frequency = frequency;
    }

    public String getText() {
        return text;
    }

    public int getFrequency() {
        return frequency;
    }
}
