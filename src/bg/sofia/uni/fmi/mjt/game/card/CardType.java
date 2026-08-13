package bg.sofia.uni.fmi.mjt.game.card;

public enum CardType {
    ONE(1, "1"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    ZERO(0, "0"),
    CHOOSE_COLOUR_CARD(-1, "Choose colour"),
    PLUS_FOUR_CARD(-2, "+4"),
    PLUS_TWO_CARD(-3, "+2"),
    SWITCH_CARD(-4, "Reverse"),
    SKIP_MOVE_CARD(-5, "Skip move");

    private final int value;
    private final String label;

    CardType(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static CardType fromInt(int n) {
        for (CardType type : CardType.values()) {
            if (type.value == n && n >= 0) {
                return type;
            }
        }
        throw new IllegalArgumentException("No card type for value: " + n);
    }

}

