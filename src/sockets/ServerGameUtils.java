package sockets;

import mindgame.Bot;
import mindgame.Game;
import mindgame.Player;

import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerGameUtils {

    static List<String> emojis = Arrays.asList(":)", ":(", ":o", ":D");


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

                clients.get(i).println(player.getPlayerName() + "'s hand: " + "[" + strs +
                        String.join("", Collections.nCopies(player.getPlayerHand().size(), " * ")) + "]");
            }

            if (!game.getNinjaRevealedDeck().isEmpty())
                clients.get(i).println("\nNinja revealed deck: " + game.getNinjaRevealedDeck());

            clients.get(i).println("\nHint: Enter 'p' to play card | 'n' to play ninja -- Sendable emojis = " + emojis + "\n");

        }
    }

    public static void clearConsole(PrintStream ps) {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                //ps.println("\033\143");
                for (int i = 0; i < 50; ++i) ps.println();
            }
        } catch (IOException | InterruptedException ex) {
        }

    }


    public static String generateAuth(int length) throws NoSuchAlgorithmException {
        String chrs = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom secureRandom = SecureRandom.getInstanceStrong();
        // length is the length of the string you want
        String customTag = secureRandom.ints(length, 0, chrs.length()).mapToObj(i -> chrs.charAt(i))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

        return customTag;

    }

    public static void broadCastMessage(List<PrintStream> clients, String message) {
        for (PrintStream client : clients) {
            client.println(message);
        }
    }

    public static ArrayList<String> deserializeMessage(String message) {
        return new ArrayList<String>(Arrays.asList(message.split("\\s*~\\s*"))); //seperates on ~

    }

    public static void closeSockets(List<PrintStream> clients) { //close all clients and close server
        broadCastMessage(clients, "kill"); // kill clients
        System.exit(0); // kill server
    }

    public static void resetAllBotThreads(List<Thread> botThreads) {
        for (Thread bot : botThreads) {
            bot.interrupt(); //when interrupted, their threads will stop
            bot.start();
        }
    }

//    public static void startAllBots(Game game) {
//        for (Player plr : game.getPlayers()) {
//            if (plr instanceof Bot) {
//                new BotThread().start();
//            }
//        }
//    }


    private ServerGameUtils() {
        throw new IllegalStateException("ServerGameUtils class");
    }


}

