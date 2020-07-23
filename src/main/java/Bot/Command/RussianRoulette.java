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

    private static Roulette game;

    private final static ScheduledExecutorService lobbyTimer = Executors.newScheduledThreadPool(1);

    private Stopwatch currentTurnTime = new Stopwatch();

    @Override
    public boolean supports(Command command) {
        String commandText = command.getCommand();
        if (commandText.equals(JOIN_COMMAND)) {
            return true;
        }

        if (commandText.equals(TAKE_TURN_COMMAND)) {
            return true;
        }

        return commandText.equals(COMMAND_PREFIX);

    }

    @Override
    protected void run(Command command, OutputInterface output) {
        String commandText = command.getCommand();
        Player player = new Player(command.getInitiator());

        switch(commandText) {
            case JOIN_COMMAND:
                joinGame(player, output);
                break;
            case TAKE_TURN_COMMAND:
                takeTurn(player, output);
                break;
            default:
                output.write(getDescription());
                break;
        }
    }

    protected String getDescription() {
        return "Игра рулетка. Вступить в игру можно командой «!r join». Когда игра начнется, ход делается командой «!r go».";
    }

    private void createNewGame(OutputInterface output) {
        game = new Roulette();
        lobbyTimer.schedule(()->{
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

    private void joinGame(Player player, OutputInterface output) {
        if (game == null || game.isOver()) {
            createNewGame(output);
        }

        try {
            game.join(player);
            output.write(player.getName() + " вступает в игру.");
        } catch (GameException err) {
            switch(err.getCode()) {
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

    private void takeTurn(Player player, OutputInterface output) {
        if (game == null || game.isOver()) {
            output.write(player.getName() + ", сейчас нет доступных игр. Создай свою командой " + JOIN_COMMAND);

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

        Turn turn;
        try {
            turn = game.takeTurn();
        } catch (GameException e) {
            output.write(player.getName() + ", ты не должен сейчас этого делать");

            return;
        }

        String message;
        Player nextTurnBelongsTo = game.getCurrentPlayer();
        if (!turn.isLucky()) {
            message = "Раздается звук выстрела и @" + player.getName() + "' выбывает из игры.";
            if (game.isOver()) {
                message += "Поздравляю, @" + nextTurnBelongsTo.getName() + "! Твоя награда: (скоро добавим систему наград)";
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
