package mindgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {


    private int level;


    private int hearts;


    private int ninjas;


    private int numPlayers;


    private boolean gameStarted;

    private final int maxPlayers;


    private List<Player> players; // player can be either client or bot(which is extended of client)


    private String ninjaRevealedDeck; // when ninja card is played, all players put their smallest card here, observabale by all players.

    private int cardOnTopDeck;


    public boolean hasGameStarted() {
        return gameStarted;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getLevel() {
        return level;
    }

    public int getHearts() {
        return hearts;
    }

    public int getNinjas() {
        return ninjas;
    }


    public List<Player> getPlayers() {
        return players;
    }

    public int getCardOnTopDeck() {
        return cardOnTopDeck;
    }

    public String getNinjaRevealedDeck() {
        return ninjaRevealedDeck;
    }



    public Game(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.hearts = maxPlayers;
        this.players = new ArrayList<Player>();
        this.ninjaRevealedDeck = "";
        this.level = 1;
        this.ninjas = 1;
        System.out.println("created game!");
    }

    public void addClientPlayer(String PlayerName) { // add real player
        if (this.numPlayers < this.maxPlayers) {
            players.add(new Player(PlayerName));
            this.numPlayers++;
            System.out.println("added player!");
            System.out.println(players);
        }

    }

    public void advanceLevel() {

        this.level++;
        this.cardOnTopDeck = 0; // reset card on deck
        this.ninjaRevealedDeck = ""; // reset ninja revealed decks

        giveCardsToPlayers(this.level);

        if (this.level == 3 || this.level == 6 || this.level == 9) {
            this.hearts++;
        }

        if (this.level == 2 || this.level == 5 || this.level == 8) {
            this.ninjas++;
        }
    }

    public void addToDeck(Player plr) { //player plr will add smallest card in hand to deck
        boolean removedHeart = false;
        this.cardOnTopDeck = plr.getPlayerHand().get(0);
        plr.getPlayerHand().remove(0);

        for (Player p : this.players) {

            if (p.equals(plr))
                continue;


            for (int i = 0; i < p.getPlayerHand().size(); i++) {
                int currentPlayerCard = p.getPlayerHand().get(i);

                if (currentPlayerCard < cardOnTopDeck) {
                    p.getPlayerHand().remove(Integer.valueOf(currentPlayerCard));
                    i--; // this is to make sure indices match with the new size
                    if (!removedHeart) {
                        this.hearts--;
                        removedHeart = true;
                    }

                } else break;

            }


        }
    }

    public void activateNinja() {
        this.ninjas--;

        String str = "[ ";
        for (Player plr : this.players) {
            if (!plr.getPlayerHand().isEmpty()) {
                str = str.concat(plr.getPlayerName() + ": " + plr.getPlayerHand().get(0) + " ~ ");
                plr.getPlayerHand().remove(0);
            }
        }
        str = str.substring(0, str.lastIndexOf("~"));
        str = str.concat("]");

        this.ninjaRevealedDeck = str;
    }

    public void startGame() {
        this.gameStarted = true;
        int numPlayers = this.players.size(); // initially we get the number of client players in the game
        int botidx = 1;
        while (numPlayers < this.maxPlayers) {
            players.add(new Bot("bot" + botidx));
            numPlayers++;
            botidx++;
        }
        giveCardsToPlayers(this.level);

        System.out.println(players);
        System.out.println(players.get(0).getPlayerHand());

    }

    public int getTotalNumCards() {
        int total = 0;
        for (Player player : getPlayers()) {
            total += player.getPlayerHand().size();
        }
        return total;
    }

    private void giveCardsToPlayers(int numCards) { // the amount of cards given will be the same as current level
        List<Integer> numList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) numList.add(i);
        Collections.shuffle(numList);

        int idx = 0;
        for (Player p : this.players) {
            p.clearHand();
            for (int i = 0; i < numCards; i++) {
                p.addCard(numList.get(idx));
                idx++;
            }
            Collections.sort(p.getPlayerHand());
        }

    }
}
