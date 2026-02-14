package bg.sofia.uni.fmi.mjt.logger;

import bg.sofia.uni.fmi.mjt.card.Card;
import bg.sofia.uni.fmi.mjt.player.Player;

public class GameLogger implements Logger {

    private StringBuffer cardLogger = new StringBuffer();
    private StringBuffer winnerLogger = new StringBuffer();
    int cardCount = 1;
    int winnerCount = 1;

    @Override
    public void logCard(Card c) {
        this.cardLogger.append(cardCount++)
                .append(". ")
                .append(c.getCardAsString())
                .append(System.lineSeparator());
    }

    @Override
    public void logWinner(Player p) {
        this.winnerLogger.append(winnerCount++)
                .append(". ")
                .append(p.getName())
                .append(System.lineSeparator());
    }

    @Override
    public String getCardLog() {
        return cardLogger.toString();
    }

    @Override
    public String getWinnerLog() {
        return winnerLogger.toString();
    }
}
