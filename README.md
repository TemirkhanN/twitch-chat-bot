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

Participants enter the game by command `roulette join`. After that the game lobby opens for 5 minutes.
In 5 minutes new participants join same lobby. On timeout participants are asked for bets.
Bets are preset list of items(like "chair", "pants", "soap" and that kind of weird stuff)
Participants take turns and check if they are lucky. one-to-six chance(rotative) to lose the game.

So the scenario is


// John writes "!roulette join" (after that joins Helen - one or few players.  Up to configurable 6.)
// if  there is an active&&!inProgress game -> join that game. Otherwise start new Game // Game.NEW || Game.PREPARATION
// On 5min timeout disable new players join and ask current players for bets. Players have 1 minute for that. On timeout player bets randomly if he didn't place bet. // Game.START
// IFCASE 1 John writes "!go" or "!bring it on"
    // Revolver attempts to shoot and cylinder rotates no matter shot was made.
    // If shot occurs then player leaves the game.
    // If there is only one player left then bot announces him a winner and moves all bets to him. || Game.OVER
 //ELSE if John writes "!revolve" then cylinder rotates changing bullet position(this surely helps if there were 5 turns without a shot)

// Current player has to take shot in 10 seconds or the next player does it automatically instead of him