package bg.sofia.uni.fmi.mjt.server.game;

public enum GameStatus {
    STARTED("Started"),
    ENDED("Ended"),
    AVAILABLE("Available"),
    ALL("All");

    private final String label;

    GameStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
