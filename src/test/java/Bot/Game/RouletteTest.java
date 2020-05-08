package Bot.Game;

import Game.GameException;
import Game.Player;
import Game.Roulette;
import Game.Turn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RouletteTest {
    private static final int ERROR_GAME_IS_IN_PROGRESS = 1;
    private static final int ERROR_NOT_ENOUGH_PLAYERS = 2;
    private static final int ERROR_GAME_IS_NOT_IN_PROGRESS = 6;

    private Roulette roulette;

    @BeforeEach
    void setUp() {
        roulette = new Roulette();
    }

    @Test
    public void testStartGameWithoutEnoughPlayer() {
        GameException exception = assertThrows(GameException.class, () -> roulette.start());

        assertEquals("Недостаточно игроков для начала игры", exception.getMessage());
        assertEquals(ERROR_NOT_ENOUGH_PLAYERS, exception.getCode());
    }

    @Test
    public void testJoinGame() {
        Player player = createPlayer("John");

        assertDoesNotThrow(() -> roulette.join(player));

        assertTrue(roulette.isLookingForPlayers());
        assertTrue(roulette.hasPlayer(player));
    }

    @Test
    public void testStartGame() {
        Player bill = createPlayer("Bill");
        Player john = createPlayer("John");

        assertDoesNotThrow(() -> {
            roulette.join(bill);
            roulette.join(john);
            roulette.start();
        });


        assertTrue(roulette.hasPlayer(bill));
        assertTrue(roulette.hasPlayer(john));
        assertTrue(roulette.isStarted());
        assertTrue(roulette.getCurrentPlayer().equals(bill));
    }

    @Test
    public void testJoinStartedGame() {
        assertDoesNotThrow(() -> {
            roulette.join(createPlayer("John"));
            roulette.join(createPlayer("Bill"));
            roulette.start();
        });

        GameException exception = assertThrows(GameException.class, () -> roulette.join(createPlayer("Emily")));

        assertEquals("Нельзя вступить в игру, пока не закончилась предыдущая", exception.getMessage());
        assertEquals(ERROR_GAME_IS_IN_PROGRESS, exception.getCode());
    }

    @Test
    public void testTakeTurnWhileGameIsNotStartedYet() {
        Player bill = createPlayer("Bill");
        Player john = createPlayer("John");

        assertDoesNotThrow(() -> {
            roulette.join(bill);
            roulette.join(john);
        });

        GameException exception = assertThrows(GameException.class, () -> roulette.takeTurn());

        assertEquals("Игра еще не началась", exception.getMessage());
        assertEquals(ERROR_GAME_IS_NOT_IN_PROGRESS, exception.getCode());
    }

    @Test
    public void testTakeTurn() {
        Player bill = createPlayer("Bill");
        Player john = createPlayer("John");

        assertDoesNotThrow(() -> {
            roulette.join(bill);
            roulette.join(john);
            roulette.start();

            Turn billsTurn = roulette.takeTurn();

            assertEquals(bill.getName(), billsTurn.getPlayer());
            assertTrue(john.equals(roulette.getCurrentPlayer()));
            // TODO Freaky tests. Figure out what to do
            if (!billsTurn.isLucky()) {
                assertTrue(roulette.isOver());
                assertTrue(bill.isLost());
            } else {
                assertFalse(bill.isLost());
                assertTrue(roulette.isStarted());
            }
        });
    }

    @Test
    public void testTurnRotation() {
        Player bill = createPlayer("Bill");
        Player john = createPlayer("John");
        Player emily = createPlayer("Emily");

        assertDoesNotThrow(() -> {
            roulette.join(bill);
            roulette.join(john);
            roulette.join(emily);
            roulette.start();
        });

        try {
            ArrayList<String> lostPlayers = new ArrayList<>(2);
            Turn turn;
            // TODO more freaky test. Maybe revolver must be injected. But.. Thats strange
            while (!roulette.isOver()) {
                turn = roulette.takeTurn();
                if (!turn.isLucky()) {
                    lostPlayers.add(turn.getPlayer());
                }

            }
            Player winner = roulette.getCurrentPlayer();
            assertFalse(lostPlayers.contains(winner.getName()));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void testDisqualification() {
        Player bill = createPlayer("Bill");
        Player john = createPlayer("John");
        Player emily = createPlayer("Emily");

        assertDoesNotThrow(() -> {
            roulette.join(bill);
            roulette.join(john);
            roulette.join(emily);
            roulette.start();
        });


        assertDoesNotThrow(() -> {
            roulette.disqualify(john);
            // Bill takes turn
            roulette.takeTurn();
        });

        assertTrue(roulette.getCurrentPlayer().equals(emily));
    }

    private Player createPlayer(String name) {
        return new Player(name);
    }
}
