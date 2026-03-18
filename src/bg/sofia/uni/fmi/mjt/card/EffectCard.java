package bg.sofia.uni.fmi.mjt.card;

import bg.sofia.uni.fmi.mjt.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public interface EffectCard {

    void applyEffect(GameController controller) throws NoColourSelectedException;

    void reset();

    void setChosenColour(CardColour colour);
}
