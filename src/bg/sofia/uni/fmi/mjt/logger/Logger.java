package bg.sofia.uni.fmi.mjt.logger;

import bg.sofia.uni.fmi.mjt.game.card.Card;
import bg.sofia.uni.fmi.mjt.player.Player;

public interface Logger {

    void logCard(Card c, Player p);

    void logWinner(Player p);

    String getCardLog();

    String getWinnerLog();
}
