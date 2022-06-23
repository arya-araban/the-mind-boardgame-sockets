package mindgame;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String playerName;



    private List<Integer> playerHand;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerHand(List<Integer> playerHand) {
        this.playerHand = playerHand;
    }

    public Player(String playerName) {
        this.playerName = playerName;
        playerHand = new ArrayList<Integer>();
    }

    public List<Integer> getPlayerHand() {
        return playerHand;
    }

    public int getSmallestCard() {
        return playerHand.get(0);
    }

    public void addCard(int cardNumber) {
        playerHand.add(cardNumber);
    }

    public void clearHand() {
        playerHand.clear();
    }


}
