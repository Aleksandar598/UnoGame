package bg.sofia.uni.fmi.mjt.card;

import java.util.List;

public enum CardColour {
    RED,
    BLUE,
    YELLOW,
    GREEN,
    SPECIAL,
    NOT_SELECTED;

    public static List<CardColour> getPlayableColours() {
        return List.of(RED, GREEN, YELLOW, BLUE);
    }
}
