package sockets;

import mindgame.Game;
import mindgame.Player;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerGameUtils {

    public static void clearConsole(PrintStream ps) {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                ps.println("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
        }
    }

    public static void printSetup(List<PrintStream> clients, Game game) {
        for (int i = 0; i < clients.size(); i++) {
            clearConsole(clients.get(i)); //clear console
            clients.get(i).flush();
            clients.get(i).println("level: " + game.getLevel() + "\n");
            clients.get(i).println("hearts: " + game.getHearts() + " | " + "ninjas: " + game.getNinjas() + "\n");
            clients.get(i).println("deck card: " + game.getCardOnTopDeck() + "\n");
            clients.get(i).println("your hand: " + game.getPlayers().get(i).getPlayerHand());
            for (Player player : game.getPlayers()) {
                if (game.getPlayers().get(i).equals(player))
                    continue;
                String strs = "";
                for (int m = 0; m < game.getActiveNinjas(); m++) {
                    strs = strs.concat(" " + String.valueOf(player.getPlayerHand().get(m)) + " ");
                }
                clients.get(i).println(player.getPlayerName() + "'s hand: " + "[" + strs +
                        String.join("", Collections.nCopies(player.getPlayerHand().size() - game.getActiveNinjas(), " * ")) + "]");
            }
            clients.get(i).println("\nhint: enter 'p' to play card | 'n' to play ninja\n");
        }
    }

    public static boolean checkAllHandsEmpty(Game game) {
        for (Player player : game.getPlayers()) {
            if (!player.getPlayerHand().isEmpty())
                return false;
        }
        return true;
    }


    public static void printGameOver(List<PrintStream> clients) {
        for (PrintStream client : clients) {
            client.println("All hearts have been lost. Game Over!");
        }
        System.exit(0);
    }



    private ServerGameUtils() {
        throw new IllegalStateException("Utils class");
    }
}
