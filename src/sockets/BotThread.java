package sockets;

import mindgame.Game;
import mindgame.Bot;
import mindgame.Player;

import static sockets.ServerGameUtils.printSetup;

public class BotThread implements Runnable { //botThread to setBot
    private Server server;
    private Bot bot;

    public BotThread(Server server, Bot bot) {
        this.server = server;
        this.bot = bot;
    }

    public void run() {
        if (bot.getPlayerHand().size() == 0) return;

        int totalCards = server.getGame().getTotalNumCards() - bot.getPlayerHand().size(); // number of cards excluding the bots own cards

        int timeInterval = (int) (bot.waitTimeToPlay(totalCards) * 1000);

        try {
            Thread.sleep(timeInterval);
        } catch (InterruptedException e) {
            return;
        }

        server.playCard(bot);


    }

}
