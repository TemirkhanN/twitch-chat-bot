package Bot.Command;

import Bot.Bot;
import Bot.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SoundReactionTest {
    private SoundReaction handler;

    @BeforeEach
    void setUp() {
        handler = new SoundReaction();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "втф",
            "wtf",
            "ват",
            "wut",
            "нани",
            "nani",
            "сюрприз",
            "surprise",
            "хехе",
            "hehe",
            "оу май",
            "oh my",
            "вау",
            "wow",
            "бу",
            "boo",
            "rip",
            "вот это поворот",
            "a twist",
            "енд",
            "the end",
            "остынь",
            "curb",
            "брух",
            "bruh"
    })
    public void testSupportedReactions(String reaction) {
        assertTrue(
                handler.supports(
                        new Command(
                                reaction,
                                new User("Some User"),
                                new Bot("Some bot", "Some token")
                        )
                )
        );
    }

    @Test
    public void testUnsupportedReaction() {
        assertFalse(handler.supports(
                new Command(
                        "Unknown reaction",
                        new User("Some User"),
                        new Bot("Some bot", "Some token")
                )
        ));
    }
}
