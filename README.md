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

Import from json file is available but GUI is not setuped to sync that yet.

## Commands
Chat commands that starts with `!`. For example `!mycommand`.
For now it is not configurable through GUI. Only import from json file as

```json
{
	"commands": {
		"mycommand": "Response that is send on command appear in the chat"
    }
}

For something complicated there is Function based handling. Only hardcode for now.
For example currently build in !uptime command that shows how long bot is being "online"

```java
class Main{
    public static void main(String[] args) {
        String botName = args[0];
        String authToken = args[1];
        String channelName = args[2]
        bot = new Bot(botName, authToken);
        Stopwatch stopwatch = new Stopwatch();
        chatBot.addChatCommand("uptime", () -> stopwatch.toString());
        bot.joinChannel(channelName);
    }
}
```

## TwitchDJ

Service from [https://streamdj.ru](https://streamdj.ru).

Commands
!music - current track name
!music skip - vote for music skip (3 votes required)