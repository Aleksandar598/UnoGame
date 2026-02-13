package bg.sofia.uni.fmi.mjt.card;

public abstract class AbstractEffectCard extends AbstractStandardCard implements EffectCard {

    protected AbstractEffectCard(CardType type, CardColour colour, int cardID) {
        super(type, colour, cardID);
    }
}
