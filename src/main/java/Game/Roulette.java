package Game;

import java.util.ArrayList;

public class Roulette {
    public enum State {
        LOOKING_FOR_PLAYERS,
        STARTED,
        OVER
    }

    private Enum state;

    private ArrayList<Player> players;

    private Revolver revolver;

    private int currentPlayerPosition;

    public Roulette() {
        state = State.LOOKING_FOR_PLAYERS;
        players = new ArrayList<>();
        revolver = new Revolver();
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

    public void join(Player player) throws GameException {
        if (!isLookingForPlayers()) {
            throw GameException.notOpenedForNewParticipants();
        }

        if(hasPlayer(player)) {
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

        currentPlayerPosition = 0;
        state = State.STARTED;
    }

    public Turn takeTurn() {
        if (!isStarted()) {
            throw GameException.gameHasNotStartedYet();
        }

        Player currentPlayer;
        do {
            currentPlayer = players.get(currentPlayerPosition);
             synchronized (this) {
                currentPlayerPosition = (currentPlayerPosition + 1) % players.size();
            }
        } while (currentPlayer.isLost());

        if (!revolver.shoot()) {
            return Turn.lucky(currentPlayer.getName());
        }

        currentPlayer.lose();
        if (getWinner() != null) {
            finishTheGame();
        }

        return Turn.unlucky(currentPlayer.getName());
    }

    public void disqualify(Player player) {
        player.lose();
    }

    public Player getCurrentPlayer() {
        if (players.size() == 0) {
            return null;
        }

        if (isOver()) {
            return getWinner();
        }

        int playerPosition = currentPlayerPosition;
        Player currentPlayer;
        do {
            currentPlayer = players.get(playerPosition);
            playerPosition = (playerPosition + 1) % players.size();
        } while (currentPlayer.isLost());

        return currentPlayer;
    }

    public boolean hasPlayer(Player player) {
        return players.stream().anyMatch(existingPlayer -> existingPlayer.equals(player));
    }

    private void finishTheGame() {
        state = State.OVER;
    }

    private Player getWinner() {
        Player activePlayer = null;
        for (Player player: players) {
            if (!player.isLost()) {
                // if there is active player already then there is not winner yet
                if (activePlayer != null) {
                    return null;
                }

                activePlayer = player;
            }
        }

        return activePlayer;
    }
}
