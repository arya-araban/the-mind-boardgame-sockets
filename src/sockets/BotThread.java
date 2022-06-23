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

        server.getGame().addToDeck(bot);

        printSetup(server.getClients(), server.getGame());

        resetAllBotThreads(server);


    }

    public static void resetAllBotThreads(Server server) {
        for (Thread bot : server.getBotThreads()) {// cancel execution of all threads
            bot.interrupt();
        }

        server.getBotThreads().clear();

        for (Player plr : server.getGame().getPlayers()) { //restart all threads
            if (plr instanceof Bot) {
                Thread bt = new Thread(new BotThread(server, (Bot) plr));
                server.getBotThreads().add(bt);
                bt.start();
            }
        }


    }}
