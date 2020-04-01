package Bot.Command;

import Util.AudioPlayer;

import java.util.HashMap;

public class SoundReaction extends CommandHandler {
    private static HashMap<String, String> reactions = new HashMap<>();

    // TODO Move to CDN. Also it may be cool to extend reactions in runtime through GUI
    static {
        // Plain text
        reactions.put("бу", "boo");
        reactions.put("остынь", "directed-by");
        reactions.put("хехе", "hehe-boy");
        reactions.put("привет", "hello");
        reactions.put("оу май", "oh-my");
        reactions.put("rip", "rip");
        reactions.put("сюрприз", "surprise-motherfucker");
        reactions.put("the end", "to-be-continued");
        reactions.put("енд", "to-be-continued");
        reactions.put("to be continued", "to-be-continued");
        reactions.put("вот это поворот", "what-a-turn");
        reactions.put("втф", "wtfit");
        reactions.put("штоето", "wtfit");
        reactions.put("вут", "watafak");
        reactions.put("wat", "watafak");
        reactions.put("ваау", "woooow");
        reactions.put("вау", "woooow");
        reactions.put("wow", "woooow");
        reactions.put("брух", "bruh");
        reactions.put("bruh", "bruh");
        reactions.put("nani", "nani");
        reactions.put("нани", "nani");
        reactions.put("omae wa mou shindeiru", "nani");

        // Emotion based
        reactions.put("wutface", "wtfit");
        reactions.put("kreygasm", "hidden/fap-fap");
        reactions.put("kappa", "directed-by");
        reactions.put("heyguys", "hello");
        reactions.put("pogchamp", "what-a-turn");
        reactions.put("ripepperonis", "rip");
        reactions.put("cmonBruh", "bruh");

        //hidden
        reactions.put("какого", "hidden/huya");
        reactions.put("johncena", "hidden/john-cena");
        reactions.put("ненене", "hidden/nononono");
        reactions.put("flick", "hidden/ricardo");
        reactions.put("добро пожаловать сука", "hidden/rice-fields");
    }

    public boolean supports(Command command) {
        return exists(command.getCommand().toLowerCase());
    }

    protected void run(Command command) {
        playReaction(command.getCommand().toLowerCase());
    }

    protected String getDescription() {
        return "Звуковые реакции, которые воспроизводятся на стриме.";
    }

    private boolean exists(String reactionName) {
        return getReactionSoundPath(reactionName) != null;
    }

    private void playReaction(String reactionName) {
        if (!exists(reactionName)) {
            return;
        }

        String soundPath = getReactionSoundPath(reactionName);
        playSound(soundPath);
    }

    private String getReactionSoundPath(String reactionName) {
        String soundName = reactions.get(reactionName);
        if (soundName == null) {
            return null;
        }

        return "sound/" + soundName + ".wav";
    }

    private void playSound(String soundFile) {
        new Thread(() -> {
            try {
                AudioPlayer.getPlayer().play(soundFile);
            } catch (Exception e) {
                // TODO
                System.err.println("Audio player failure." + e.toString());
            }
        }).start();
    }
}
