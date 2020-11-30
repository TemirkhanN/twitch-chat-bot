package Bot.Command;

import Game.GameException;
import Game.Player;
import Game.Roulette;
import Game.Turn;
import Util.Stopwatch;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RussianRoulette extends CommandHandler {
    private final static String COMMAND_PREFIX = "!r";
    private final static String JOIN_COMMAND = COMMAND_PREFIX + " join";
    private final static String TAKE_TURN_COMMAND = COMMAND_PREFIX + " go";

    private volatile Roulette game;

    private final static ScheduledExecutorService lobbyTimer = Executors.newScheduledThreadPool(1);

    private Stopwatch currentTurnTime = new Stopwatch();

    private OutputInterface output;

    public RussianRoulette(OutputInterface output) {
        this.output = output;
    }

    @Override
    public boolean supports(Command command) {
        String commandText = command.getCommand();
        if (commandText.equals(JOIN_COMMAND)) {
            return true;
        }

        if (commandText.equals(TAKE_TURN_COMMAND)) {
            return true;
        }

        return false;
    }

    @Override
    public int handle(Command command) {
        String commandText = command.getCommand();
        Player player = new Player(command.getInitiator());

        if (commandText.equals(JOIN_COMMAND)) {
            joinGame(player);

            return RESULT_CODE_OK;
        }

        if (commandText.equals(TAKE_TURN_COMMAND)) {
            takeTurn(player);

            return RESULT_CODE_OK;
        }

        return RESULT_CODE_ERROR;
    }

    private void createNewGame() {
        game = new Roulette();
        lobbyTimer.schedule(() -> {
            if (!game.isLookingForPlayers()) {
                throw new RuntimeException("Game should be looking for players");
            }

            try {
                game.start();
                output.write("Игра началась! @" + game.getCurrentPlayer().getName() + " ты начинаешь.");
                currentTurnTime.reset();
            } catch (GameException err) {
                game = null;
                if (err.getCode() == GameException.CODE_NOT_ENOUGH_PLAYERS) {
                    output.write("Недостаточно игроков для начала игры.");
                }
            }
        }, 1, TimeUnit.MINUTES);
    }

    private void joinGame(Player player) {
        if (game == null || game.isOver()) {
            createNewGame();
        }

        try {
            game.join(player);
            output.write(player.getName() + " вступает в игру.");
        } catch (GameException err) {
            switch (err.getCode()) {
                case GameException.CODE_PLAYER_ALREADY_JOINED:
                    output.write(player.getName() + ", ты уже участвуешь в игре.");
                    break;
                case GameException.CODE_GAME_HAS_ALREADY_STARTED:
                    output.write(player.getName() + ", игра уже идет.");
                    break;
                default:
                    System.err.println(err.toString());
            }
        }
    }

    private void takeTurn(Player player) {
        if (game == null || game.isOver()) {
            output.write(player.getName() + ", сейчас нет доступных игр. Создай свою командой " + JOIN_COMMAND);

            return;
        }

        if (!game.isStarted()) {
            output.write(player.getName() + ", игра еще не началась. Потерпи.");

            return;
        }

        if (!game.hasPlayer(player)) {
            output.write(player.getName() + ", ты не участвуешь в игре");

            return;
        }

        Player currentTurnBelongsTo = game.getCurrentPlayer();
        if (!player.equals(currentTurnBelongsTo)) {
            if (!currentTurnTime.isTimePassed(120)) {
                output.write(player.getName() + ", сейчас не твой ход");

                return;
            }

            output.write(currentTurnBelongsTo.getName() + " исключен за бездействие.");
            game.disqualify(currentTurnBelongsTo);
        }

        Turn turn = game.takeTurn();

        String message;
        Player nextTurnBelongsTo = game.getCurrentPlayer();
        if (!turn.isLucky()) {
            message = "Раздается звук выстрела и @" + player.getName() + "' выбывает из игры.";
            if (game.isOver()) {
                message += "Поздравляю, @" + nextTurnBelongsTo.getName() + "! Твоя награда: все, что снимешь с проигравших Kappa";
            } else {
                message += "@" + nextTurnBelongsTo.getName() + ", твой черед!";
            }
        } else {
            message = "@" + player.getName() + "'у повезло. @" + nextTurnBelongsTo.getName() + " твой черед!";
        }

        output.write(message);
        currentTurnTime.reset();
    }
}
