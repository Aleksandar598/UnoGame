package bg.sofia.uni.fmi.mjt;

public enum CardType {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    ZERO(0),
    CHOOSE_COLOUR_CARD(-1),
    PLUS_FOUR_CARD(-2),
    PLUS_TWO_CARD(-3),
    SWITCH_CARD(-4),
    SKIP_MOVE_CARD(-5);

    private final int value;

    // Конструктор
    CardType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
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
