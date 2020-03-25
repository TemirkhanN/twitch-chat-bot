package Game;

public class Roulette {
    public enum State {
        LOOKING_FOR_PLAYERS,
        STARTED,
        OVER
    }

    private Enum state;

    private CircularQueue<String> players;

    private Revolver revolver;

    public Roulette() {
        state = State.LOOKING_FOR_PLAYERS;
        players = new CircularQueue<>();
        revolver = new Revolver();
    }

    public void join(String player) throws GameException {
        if (!isLookingForPlayers()) {
            throw GameException.notOpenedForNewParticipants();
        }

        if (players.contains(player)){
            throw GameException.playerHasAlreadyJoinedTheGame(player);
        }

        players.add(player);
    }

    public void start() throws GameException {
        if (isOver()) {
            throw GameException.gameHasAlreadyEnded();
        }

        if (isStarted()) {
            throw GameException.gameHasAlreadyStarted();
        }

        if (players.size() < 2) {
            throw GameException.notEnoughPlayers();
        }

        state = State.STARTED;
    }

    public Turn takeTurn() {
        if (!isStarted()) {
            throw new RuntimeException("Игра еще не началась");
        }

        String playerName = players.current();
        players.next(); // Move pointer to next participant
        if (revolver.shoot()) {
            players.remove(playerName);

            if (players.size() == 1) {
                finishTheGame();
            }

            return Turn.unlucky(playerName);
        }

        return Turn.lucky(playerName);
    }

    public void revolveCylinder() {
        revolver.revolve();
    }

    public String getCurrentRevolverHolder() {
        return players.current();
    }

    public boolean hasPlayer(String player) {
        return players.contains(player);
    }

    private void finishTheGame() {
        state = State.OVER;
    }


    public boolean isStarted() {
        return state == State.STARTED;
    }

    public boolean isLookingForPlayers() {
        return state == State.LOOKING_FOR_PLAYERS;
    }

    public boolean isOver() {
        return state == State.OVER;
    }
}
