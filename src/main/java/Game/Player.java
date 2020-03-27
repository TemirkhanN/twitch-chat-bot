package Game;

public class Player {
    private String name;
    private boolean isLost = false;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isLost() {
        return isLost;
    }

    public void lose() {
        isLost = true;
    }

    public boolean equals(Player player) {
        return name.equals(player.getName());
    }
}
