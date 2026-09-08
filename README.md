# UNO Game

A multiplayer command-line UNO game written in Java. A non-blocking server accepts multiple clients, manages user sessions and game lobbies, and relays game events to players.

## Requirements

- IntelliJ IDEA
- JDK 25, which is the JDK currently configured for this project

The project bundles its test libraries in the `lib/` directory.

## Run the game

1. Open the project in IntelliJ IDEA and set the project SDK to JDK 25.
2. Run `bg.sofia.uni.fmi.mjt.server.StartServer`. The server listens on port `7777` by default.
3. Run `bg.sofia.uni.fmi.mjt.client.UnoClient` in one or more separate run configurations.
4. Type `help` in a client to see the available commands.

To use a different port, pass it as the first argument to the server. Pass the host and port as the first two arguments to a client:

```text
Server: StartServer 8888
Client: UnoClient localhost 8888
```

In IntelliJ, enable **Allow multiple instances** in the client run configuration to launch several clients at once.

Type `exit` in a client to close its session.

## Typical lobby flow

Register and log in each player:

```text
register --username=alice --password=secret
login --username=alice --password=secret
```

Create a game, then have other logged-in clients join it:

```text
create-game --game-id=weekend-game --number-of-players=4
join --game-id=weekend-game --display-name=Bob
start
```

The game creator starts the game. Players may use `leave` or `logout` to leave a lobby. Disconnecting a client also removes its player from the game and releases its session.

## Commands

Run `help` at any time. It is available before logging in.

| Command | Description |
| --- | --- |
| `register --username=<username> --password=<password>` | Create an account. |
| `login --username=<username> --password=<password>` | Start a session. |
| `logout` | End the current session. |
| `list-games [--status=all\|available\|started\|ended]` | Show game lobbies. |
| `create-game --game-id=<id> [--number-of-players=<count>]` | Create a lobby. The default capacity is 2. |
| `join --game-id=<id> [--display-name=<name>]` | Join a lobby. |
| `start` | Start your game if you created it. |
| `leave` | Leave the current game. |
| `summary --game-id=<id>` | Show a completed game summary. |

### In-game commands

| Command | Description |
| --- | --- |
| `show-hand` | Show your cards. |
| `show-last-card` | Show the card on top of the discard pile. |
| `show-played-cards` | Show cards played during the game. |
| `get-current-colour` | Show the active colour. |
| `draw` | Draw a card. |
| `play --card-id=<id>` | Play a standard card. |
| `play-choose --card-id=<id> --color=<color>` | Play a wild card and choose a colour. |
| `play-plus-four --card-id=<id> --color=<color>` | Play a Wild Draw Four and choose a colour. |
| `accept-effect` | Accept a pending card effect. |
| `spectate` | Inspect the other players' hands. |
| `spectate-hand --player-id=<id>` | Inspect one player's hand. |

Colours are `red`, `yellow`, `green`, and `blue`.

## Tests

Test classes are under `test/`. In IntelliJ, right-click the `test` directory and select **Run 'All Tests'**.

## Project structure

```text
src/
  bg/sofia/uni/fmi/mjt/client/       Command-line client
  bg/sofia/uni/fmi/mjt/server/       Server, sessions, lobbies, and networking
  bg/sofia/uni/fmi/mjt/command/      Lobby and in-game commands
  bg/sofia/uni/fmi/mjt/game/         UNO rules, cards, deck, and controller
test/                                Unit and server tests
lib/                                 Test dependencies
```
