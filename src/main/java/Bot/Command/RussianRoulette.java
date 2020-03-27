package Bot.Command;

import Bot.Bot;
import Bot.User;
import Game.GameException;
import Game.Player;
import Game.Roulette;
import Game.Turn;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RussianRoulette extends CommandHandler {
    private final static String COMMAND_PREFIX = "!roulette";
    private final static String JOIN_COMMAND = COMMAND_PREFIX + " join";
    private final static String TAKE_TURN_COMMAND = COMMAND_PREFIX + " go";

    private static Roulette game;

    private Player player;

    private final static ScheduledExecutorService lobbyTimer = Executors.newScheduledThreadPool(1);

    RussianRoulette(String passedCommand, User participant) {
        super(passedCommand);
        this.player = new Player(participant.getName());
    }

    @Override
    public boolean isValidCommand() {
        return command.indexOf(COMMAND_PREFIX) == 0;
    }

    @Override
    protected void run(String Command, Bot executor) {
        if (game == null || game.isOver()) {
            createNewGame(executor);
        }

        if (command.equals(JOIN_COMMAND)) {
            joinGame(executor);

            return;
        }

        if (command.equals(TAKE_TURN_COMMAND)) {
            takeTurn(executor);

            return;
        }

        System.out.println("Unknown roulette command " + command);
    }

    private void createNewGame(Bot mediator) {
        game = new Roulette();
        lobbyTimer.schedule(()->{
            if (!game.isLookingForPlayers()) {
                throw new RuntimeException("Game should be looking for players");
            }

            try {
                game.start();
                mediator.sendMessage("Игра началась! @" + game.getCurrentPlayer().getName() + " ты начинаешь.");
            } catch (GameException err) {
                game = null;
                if (err.getCode() == GameException.CODE_NOT_ENOUGH_PLAYERS) {
                    mediator.sendMessage("Недостаточно игроков для начала игры.");
                }
            }
        }, 10, TimeUnit.SECONDS);
    }

    private void joinGame(Bot mediator) {
        try {
            game.join(player);
            mediator.sendMessage("@" + player.getName() + " вступает в игру.");
        } catch (GameException err) {
            switch(err.getCode()) {
                case GameException.CODE_PLAYER_ALREADY_JOINED:
                    mediator.whisper(player.getName(), "ты уже участвуешь в игре.");
                    break;
                case GameException.CODE_GAME_HAS_ALREADY_STARTED:
                    mediator.whisper(player.getName(), "игра уже идет.");
                    break;
                default:
                    System.err.println(err.toString());
            }
        }
    }

    private void takeTurn(Bot mediator) {
        if (!game.hasPlayer(player)) {
            mediator.whisper(player.getName(), "ты не участвуешь в игре");

            return;
        }

        Player currentTurnBelongsTo = game.getCurrentPlayer();
        if (!player.equals(currentTurnBelongsTo)) {
            mediator.whisper(player.getName(), "сейчас не твой ход");

            return;
        }

        String message;
        Turn turn = game.takeTurn();
        Player nextTurnBelongsTo = game.getCurrentPlayer();
        if (!turn.isLucky()) {
            message = "BANG! @" + player.getName() + "' выбывает из игры.";
            if (game.isOver()) {
                message += "Поздравляю, @" + nextTurnBelongsTo.getName() + "! Твоя награда: %bets%";
            } else {
                message += "@" + nextTurnBelongsTo.getName() + ", твой черед!";
            }
        } else {
            message = "@" + player.getName() + "'у повезло. @" + nextTurnBelongsTo.getName() + " твой черед!";
        }

        mediator.sendMessage(message);
    }
}
