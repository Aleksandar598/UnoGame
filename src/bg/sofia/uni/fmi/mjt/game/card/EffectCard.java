package bg.sofia.uni.fmi.mjt.game.card;

import bg.sofia.uni.fmi.mjt.game.controller.GameController;
import bg.sofia.uni.fmi.mjt.exception.NoColourSelectedException;

public interface EffectCard {

    void applyEffect(GameController controller) throws NoColourSelectedException;

}
