package Bot.Command;

import Bot.Bot;
import Game.GameException;
import Game.Player;
import Game.Roulette;
import Game.Turn;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RussianRoulette extends CommandHandler {
    private final static String COMMAND_PREFIX = "!r";
    private final static String JOIN_COMMAND = COMMAND_PREFIX + " join";
    private final static String TAKE_TURN_COMMAND = COMMAND_PREFIX + " go";

    private static Roulette game;

    private final static ScheduledExecutorService lobbyTimer = Executors.newScheduledThreadPool(1);

    @Override
    public boolean supports(Command command) {
        String commandText = command.getCommand();
        if (commandText.equals(JOIN_COMMAND)) {
            return true;
        }

        if (commandText.equals(TAKE_TURN_COMMAND)) {
            return true;
        }

        if (commandText.equals(COMMAND_PREFIX)) {
            return true;
        }

        return false;
    }

    @Override
    protected void run(Command command) {
        Bot mediator = command.getMediator();
        String commandText = command.getCommand();
        Player player = new Player(command.getInitiator().getName());

        switch(commandText) {
            case JOIN_COMMAND:
                joinGame(player, mediator);
                break;
            case TAKE_TURN_COMMAND:
                takeTurn(player, mediator);
                break;
            default:
                mediator.sendMessage(getDescription());
                break;
        }
    }

    protected String getDescription() {
        return "Игра рулетка. Вступить в игру можно командой «!r join». Когда игра начнется, ход делается командой «!r go».";
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
        }, 1, TimeUnit.MINUTES);
    }

    private void joinGame(Player player, Bot mediator) {
        if (game == null || game.isOver()) {
            createNewGame(mediator);
        }

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

    private void takeTurn(Player player, Bot mediator) {
        if (game == null || game.isOver()) {
            mediator.whisper(player.getName(), "Сейчас нет доступных игр. Создай свою командой " + JOIN_COMMAND);

            return;
        }

        if (!game.hasPlayer(player)) {
            mediator.whisper(player.getName(), "ты не участвуешь в игре");

            return;
        }

        Player currentTurnBelongsTo = game.getCurrentPlayer();
        if (!player.equals(currentTurnBelongsTo)) {
            mediator.whisper(player.getName(), "сейчас не твой ход");

            return;
        }

        Turn turn;
        try {
            turn = game.takeTurn();
        } catch (GameException e) {
            mediator.whisper(player.getName(), "ты не должен сейчас этого делать");
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

        mediator.sendMessage(message);
    }
}
