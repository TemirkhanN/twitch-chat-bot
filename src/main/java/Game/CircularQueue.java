package Game;

import java.util.ArrayList;

// TODO looks like some disaster... Shall check it carefully
public class CircularQueue<E> extends ArrayList<E> {
    private int pointer;

    public void movePointer(int steps) {
         pointer = (pointer + steps) % size();
    }

    public E current() {
        return get(pointer);
    }

    public E next() {
        try {
            return get(++pointer);
        } catch (IndexOutOfBoundsException err) {
            pointer = 0;

            return current();
        }
    }
}
