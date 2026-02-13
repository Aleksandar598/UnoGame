package bg.sofia.uni.fmi.mjt;

import bg.sofia.uni.fmi.mjt.card.CardColour;

public interface GameController {

    void skipNextPlayer();

    void addCardForDraw(int count);

    void reversePlayerDirection();

    void setColour(CardColour colour);
}
