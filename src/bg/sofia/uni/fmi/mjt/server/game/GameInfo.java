package bg.sofia.uni.fmi.mjt.server.game;

import bg.sofia.uni.fmi.mjt.server.exception.MissingArgumentsException;

import java.util.List;

public record GameInfo(
        String gameId,
        String creatorUsername,
        GameStatus status,
        List<Integer> players,
        int maxPlayerCount
) {
    public GameInfo {
        if (gameId == null) {
            throw new IllegalArgumentException("gameId cannot be null");
        }
        if (players == null) {
            throw new IllegalArgumentException("players is null");
        }
        if (creatorUsername == null) {
            throw new IllegalArgumentException("creatorUsername is null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status is null");
        }
        if (players.size() > maxPlayerCount) {
            throw new IllegalArgumentException("currentPlayerCount is greater than maxPlayerCount");
        }
        if (maxPlayerCount < 1) {
            throw new IllegalArgumentException("maxPlayerCount is less than 1");
        }
        for  (Integer player : players) {
            if (player == null) {
                throw new IllegalArgumentException("player is null");
            }
        }
    }

    public String getGameInfo() {
        return "Game id:" +
                gameId +
                ", Creator:" +
                creatorUsername +
                ", Status:" +
                status +
                ", Player Count:" +
                players.size() +
                ", Lobby Size:" +
                maxPlayerCount;
    }
}