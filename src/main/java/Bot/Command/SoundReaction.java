package Bot.Command;

import Util.AudioPlayer;

import java.util.HashMap;

public class SoundReaction extends CommandHandler {
    private static HashMap<String, String> reactions = new HashMap<>();

    // TODO Move to CDN. Also it may be cool to extend reactions in runtime through GUI
    static {
        // Plain text
        reactions.put("бу", "boo"); reactions.put("boo", "boo");
        reactions.put("остынь", "directed-by"); reactions.put("curb", "directed-by");
        reactions.put("хехе", "hehe-boy"); reactions.put("hehe", "hehe-boy");
        reactions.put("привет", "hello"); reactions.put("hello", "hello"); reactions.put("hi", "hello");
        reactions.put("оу май", "oh-my"); reactions.put("oh my", "oh-my");
        reactions.put("rip", "rip");
        reactions.put("сюрприз", "surprise-motherfucker"); reactions.put("surprise", "surprise-motherfucker");
        reactions.put("the end", "to-be-continued"); reactions.put("енд", "to-be-continued"); reactions.put("to be continued", "to-be-continued");
        reactions.put("вот это поворот", "what-a-turn"); reactions.put("a twist", "what-a-turn");
        reactions.put("втф", "wtfit"); reactions.put("wtf", "wtfit");
        reactions.put("ват", "watafak"); reactions.put("wut", "watafak");
        reactions.put("вау", "woooow"); reactions.put("wow", "woooow");
        reactions.put("брух", "bruh"); reactions.put("bruh", "bruh");
        reactions.put("nani", "nani"); reactions.put("нани", "nani"); reactions.put("omae wa mou shindeiru", "nani");
        reactions.put("fukdup", "fukdup"); reactions.put("wasted", "fukdup"); reactions.put("гг", "fukdup");
        reactions.put("бадум", "badumtss"); reactions.put("badum", "badumtss");
        reactions.put("медик", "medic"); reactions.put("medic", "medic");
        reactions.put("ретард", "retardalert"); reactions.put("retard", "retardalert");

        // Emotion based
        reactions.put("wutface", "wtfit");
        reactions.put("kreygasm", "hidden/fap-fap");
        reactions.put("kappa", "directed-by");
        reactions.put("heyguys", "hello");
        reactions.put("pogchamp", "what-a-turn");
        reactions.put("ripepperonis", "rip");
        reactions.put("cmonbruh", "bruh");

        //hidden
        reactions.put("какого", "hidden/huya");
        reactions.put("johncena", "hidden/john-cena");
        reactions.put("ненене", "hidden/nononono");
        reactions.put("flick", "hidden/ricardo");
        reactions.put("suffer", "hidden/rice-fields");

        //individual
        reactions.put("slaves", "individual/zenrays/gachslaves");
        reactions.put("курва", "individual/duue88d7d/okurwa");
    }

    public boolean supports(Command command) {
        String reactionName = command.getCommand().toLowerCase();
        if (!exists(reactionName)) {
            return false;
        }

        return isAllowedToUseReaction(command.getInitiator().getName(), reactionName);
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

    private boolean isAllowedToUseReaction(String user, String reaction) {
        String reactionFilePath = getReactionSoundPath(reaction);
        if (reactionFilePath.contains("individual/" + user.toLowerCase() + "/")) {
            return true;
        }

        if (!reactionFilePath.contains("individual/")) {
            return true;
        }

        return false;
    }
}
