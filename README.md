# Twitch Chat Bot

## Build

```bash
$ gradlew clean
$ gradlew jar
```

## Launch
```bash
$ java -jar build/libs/twitch-chatbot-0.1.jar channel_name bot_name oauth_token(without prefix oauth:)
```

## Sound reactions
After joining chat bot starts listening for special words or combination of words.
If message matched one of the preset pattern system executes sound playback. Parallel execution not allowed.

## Roulette

Participants enter the game by command `roulette join`. After that the game lobby opens for 1 minute.
In that time new participants join same lobby.
Participants take turns and check if they are lucky. one-to-six chance to lose the game.
Game bets are coming soon.
