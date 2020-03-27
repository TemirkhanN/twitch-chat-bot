package Game;

import java.util.Random;

public class Revolver {
    private static Random randomizer = new Random();

    public boolean shoot() {
        // We take bias that there is less chance to get shot if we revolve cylinder after each shooting attempt
        // TODO Very random.. meh
       return randomizer.nextInt(6) == 3;
    }
}
