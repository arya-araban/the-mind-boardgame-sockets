package mindgame;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String playerName;
    private List<Integer> playerHand;

    public String getPlayerName() {
        return playerName;
    }

    public Player(String playerName) {
        this.playerName = playerName;
        playerHand = new ArrayList<Integer>();
    }

    public List<Integer> getPlayerHand() {
        return playerHand;
    }


    public void addCard(int cardNumber) {
        playerHand.add(cardNumber);
    }

    public void clearHand() {
        playerHand.clear();
    }



}
