package Game;

public class GameException extends Exception {
    public final static int CODE_UNDEFINED = 0;
    public final static int CODE_GAME_HAS_ALREADY_STARTED = 1;
    public final static int CODE_NOT_ENOUGH_PLAYERS = 2;
    public final static int CODE_PLAYER_ALREADY_JOINED = 3;
    public final static int CODE_GAME_HAS_ALREADY_ENDED = 5;

    private int code;

    private GameException(String message, int code) {
        super(message);
        this.code = code;
    }

    static GameException notOpenedForNewParticipants() {
        return new GameException("Нельзя вступить в игру, пока не закончилась предыдущая", CODE_GAME_HAS_ALREADY_STARTED);
    }

    static GameException playerHasAlreadyJoinedTheGame(String playerName) {
        return new GameException("Игрок " + playerName + " уже вступил в игру", CODE_PLAYER_ALREADY_JOINED);
    }

    static GameException notEnoughPlayers() {
        return new GameException("Недостаточно игроков для начала игры", CODE_NOT_ENOUGH_PLAYERS);
    }

    static GameException gameHasAlreadyEnded() {
        return new GameException("Игра уже завершена", CODE_GAME_HAS_ALREADY_ENDED);
    }

    static GameException gameHasAlreadyStarted() {
        return new GameException("Игра уже началась", CODE_GAME_HAS_ALREADY_STARTED);
    }

    public int getCode() {
        return code;
    }
}
