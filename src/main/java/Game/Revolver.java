package Game;

import java.util.Random;

public class Revolver {
    private enum Round {
        DUMMY,
        DEADLY
    }

    private CircularQueue<Round> cylinder;

    Revolver() {
        cylinder = new CircularQueue<>();
        cylinder.add(Round.DEADLY);
        cylinder.add(Round.DUMMY);
        cylinder.add(Round.DUMMY);
        cylinder.add(Round.DUMMY);
        cylinder.add(Round.DUMMY);
        cylinder.add(Round.DUMMY);
        revolve();
    }

    public void revolve() {
        cylinder.movePointer((new Random()).nextInt(6));
    }

    public boolean shoot() {
        if (cylinder.current() == Round.DEADLY) {
            cylinder.next();

            return true;
        }
        cylinder.next();

        return false;
    }
}
