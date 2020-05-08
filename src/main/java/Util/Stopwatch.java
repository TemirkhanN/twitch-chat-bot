package Util;

public class Stopwatch {
    enum Time {
        HOUR,
        MINUTE,
        SECOND
    }

    private long startTime;

    public Stopwatch() {
        startTime = System.currentTimeMillis();
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }

    public boolean isTimePassed(int seconds) {
        return getPassedTime(Time.SECOND) >= seconds;
    }

    private int getPassedTime(Time timeUnit) {
        float divider;
        switch(timeUnit) {
            case SECOND:
                divider = 1000f;
                break;
            case MINUTE:
                divider = 60 * 1000f;
                break;
            case HOUR:
                divider = 60 * 60 * 1000f;
                break;
            default:
                divider = 1000f;
        }

        return (int) ((System.currentTimeMillis() - startTime)/divider);
    }

    public String toString() {
        int totalSecondsPassed = getPassedTime(Time.SECOND);
        int hours = totalSecondsPassed/3600;
        int minutes = (totalSecondsPassed % 3600)/60;
        int seconds = totalSecondsPassed % 60;

        return String.format("%d hours %d minutes %d seconds", hours, minutes, seconds);
    }
}
