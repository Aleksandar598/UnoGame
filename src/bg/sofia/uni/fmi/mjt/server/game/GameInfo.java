package bg.sofia.uni.fmi.mjt.server.game;

import java.util.List;

public record GameInfo(
        String gameId,
        String creatorUsername,
        GameStatus status,
        List<Integer> players,
        int currentPlayerCount,
        int maxPlayerCount
) {
    public GameInfo {
        if (gameId == null) {
            throw new IllegalArgumentException("gameId is null");
        }
        if (creatorUsername == null) {
            throw new IllegalArgumentException("creatorUsername is null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status is null");
        }
        if (currentPlayerCount > maxPlayerCount) {
            throw new IllegalArgumentException("currentPlayerCount is greater than maxPlayerCount");
        }
        if (maxPlayerCount < 1) {
            throw new IllegalArgumentException("maxPlayerCount is less than 1");
        }
        if (players == null) {
            throw new IllegalArgumentException("players is null");
        }
        for  (Integer player : players) {
            if (player == null) {
                throw new IllegalArgumentException("player is null");
            }
        }
    }
}