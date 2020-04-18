# Twitch Chat Bot

## Build

```bash
$ gradlew clean
$ gradlew jar
```

## Launch
It is standalone image so double click on `build/libs/twitch-chatbot-0.1.jar` should be enough.

## Sound reactions
After joining chat bot starts listening for special words or combination of words.
If message matched one of the preset pattern system executes sound playback. Parallel execution not allowed.

## Roulette

Participants enter the game by command `r join`. After that the game lobby opens for 1 minute.
In that time new participants join same lobby.
Participants take turns and check if they are lucky. one-to-six chance to lose the game.
Game bets are coming soon.

## Announcements
Repetitive or one-timed announcements are available in Main.class(soon will be replaced with GUI control).

```java

class Main{
    public static void main(String[] args) {
        bot = new Bot(name, token);
        bot.addAnnouncement("One time announcement that will be made immediately");
        bot.addAnnouncement("Repetitive announcement that appears every 4 minutes. First appearance on 4th minute", 4);

        bot.joinChannel(someChannel);
    }
}

```

## TwitchDJ

This is very experimental and highly bases on low level SaaS [https://streamdj.ru](https://streamdj.ru).

Allows to view currently playing track or skip it. Not recommended for use. At least for now.
