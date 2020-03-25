package Bot.Command;

import Bot.Bot;
import Bot.User;
import Game.GameException;
import Game.Roulette;
import Game.Turn;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RussianRoulette extends CommandHandler {
    private final static String COMMAND_PREFIX = "!roulette";
    private final static String JOIN_COMMAND = COMMAND_PREFIX + " join";
    private final static String TAKE_TURN_COMMAND = COMMAND_PREFIX + " go";
    private final static String REVOLE_COMMAND = COMMAND_PREFIX + " revolve";

    private static Roulette game;

    private User participant;

    private ScheduledExecutorService lobbyTimer;

    RussianRoulette(String passedCommand, User participant) {
        super(passedCommand);
        this.participant = participant;
        lobbyTimer = Executors.newScheduledThreadPool(1);
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

        String player = participant.getName();
        if (command.equals(JOIN_COMMAND)) {
            joinGame(player, executor);

            return;
        }

        if (command.equals(TAKE_TURN_COMMAND)) {
            takeTurn(player, executor);

            return;
        }

        if (command.equals(REVOLE_COMMAND)) {
            game.revolveCylinder();
            takeTurn(player, executor);

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
                mediator.sendMessage("Игра началась! @" + game.getCurrentRevolverHolder() + " ты начинаешь.");
            } catch (GameException err) {
                game = null;
                if (err.getCode() == GameException.CODE_NOT_ENOUGH_PLAYERS) {
                    mediator.sendMessage("Недостаточно игроков для начала игры.");
                }
            }
        }, 5, TimeUnit.SECONDS);
    }

    private void joinGame(String player, Bot mediator) {
        try {
            game.join(player);
            mediator.whisper(participant, " вступает в игру.");
        } catch (GameException err) {
            switch(err.getCode()) {
                case GameException.CODE_PLAYER_ALREADY_JOINED:
                    mediator.whisper(participant, "ты уже участвуешь в игре.");
                    break;
                case GameException.CODE_GAME_HAS_ALREADY_STARTED:
                    mediator.whisper(participant, "нельзя вступить в игру");
                    break;
                default:
                    System.err.println(err.toString());
            }
        }
    }

    private void takeTurn(String player, Bot mediator) {
        if (!game.hasPlayer(player)) {
            return;
        }

        String message;
        Turn turn = game.takeTurn();
        String nextPlayer = game.getCurrentRevolverHolder();
        if (turn.isLucky()) {
            message = "@" + player + "'у повезло. @" + nextPlayer + " твой черед!";
        } else {
            message = "BANG! @" + player + "' выбывает из игры.";
            if (game.isOver()) {
                message += "Поздравляю, @" + nextPlayer + "! Твоя награда: %bets%";
            } else {
                message += "@" + nextPlayer + ", твой черед!";
            }
        }

        mediator.sendMessage(message);
    }
}
