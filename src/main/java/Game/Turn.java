package Game;

public class Turn {
    enum Result {
        LUCKY,
        UNLUCKY
    }

    private Result result;

    private String player;

    private Turn(String player, Result result) {
        this.player = player;
        this.result = result;
    }

    public static Turn unlucky(String player) {
        return new Turn(player, Result.UNLUCKY);
    }

    public static Turn lucky(String player) {
        return new Turn(player, Result.LUCKY);
    }

    public boolean isLucky() {
        return result == Result.LUCKY;
    }

    public String getPlayer() {
        return player;
    }
}
