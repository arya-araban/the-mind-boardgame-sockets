package sockets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mindgame.Bot;
import mindgame.Game;
import mindgame.Player;

import static sockets.ServerGameUtils.*;

public class Server {
    public Game getGame() {
        return game;
    }

    private Game game;
    private int port;

    public List<PrintStream> getClients() {
        return clients;
    }

    private List<PrintStream> clients;
    private List<String> clientAuthStrings;
    private ServerSocket server;

    public List<Thread> getBotThreads() {
        return botThreads;
    }

    private List<Thread> botThreads;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        new Server(12345).run();
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<PrintStream>();
        this.clientAuthStrings = new ArrayList<String>();
        this.botThreads = new ArrayList<Thread>();
    }


    public void run() throws IOException, NoSuchAlgorithmException {
        server = new ServerSocket(port) {
            protected void finalize() throws IOException {
                this.close();
            }
        };
        System.out.println("Port 12345 is now open.");

        while (true) {
            // accepts a new client
            Socket client = server.accept();
            System.out.println("Connection established with client: " + client.getInetAddress().getHostAddress());

            // add client message to list
            this.clients.add(new PrintStream(client.getOutputStream()));

            DataOutputStream client_dOut = new DataOutputStream(client.getOutputStream());

            client_dOut.write(clients.size());
            client_dOut.flush();

            String clientSR = generateAuth(6); //the secure random string used to identify client
            this.clientAuthStrings.add(clientSR);
            client_dOut.writeUTF(clientSR);
            client_dOut.flush();
            // create a new thread for client handling
            new Thread(new ClientHandler(this, client.getInputStream())).start();
        }
    }


    void processMessages(String serializedMessage) {
        ArrayList<String> dmsg = deserializeMessage(serializedMessage);
        String message = dmsg.get(0);
        String authString = dmsg.get(1);
        String specialFlag = dmsg.size() == 3 ? dmsg.get(2) : null;

        int playerIdx = this.clientAuthStrings.indexOf(authString);

        if (specialFlag != null) {

            if (clientAuthStrings.get(0).equals(authString) && specialFlag.equals("maxplayers")) {

                this.game = new Game(Integer.parseInt(message));

            }

            if (specialFlag.equals("nick")) {
                System.out.println(message);

                if (this.game.getPlayers().size() < this.game.getMaxPlayers()) {
                    game.addClientPlayer(message);
                } else {
                    clients.get(playerIdx).println("unfortunately there's no room left for you in this game!");
                }
            }

        }


        if (clientAuthStrings.get(0).equals(authString) && message.equals("start") && !game.hasGameStarted()) {
            game.startGame();
            printSetup(this.clients, game);

            for (Player plr : game.getPlayers()) {
                if (plr instanceof Bot) {
                    Thread bt = new Thread(new BotThread(this, (Bot) plr));
                    botThreads.add(bt);
                    bt.start();
                }
            }

        }

        if (clientAuthStrings.get(0).equals(authString) && message.equals("start") && !game.hasGameStarted()) {
            game.startGame();
            printSetup(this.clients, game);
        }

        if (message.equals("p") && game.hasGameStarted()) {

            try {
                playCard(game.getPlayers().get(playerIdx));

            } catch (IndexOutOfBoundsException IOB) {
                clients.get(playerIdx).println("you don't have any cards to play!");
            }
        }

        if (message.equals("n") && game.hasGameStarted()) {
            if (this.game.getNinjas() > 0) {
                this.game.activateNinja();

                if (game.getTotalNumCards() == 0) { //everyone has played their cards
                    game.advanceLevel();
                }

                printSetup(this.clients, this.game);
            } else {
                clients.get(playerIdx).println("There are no ninja cards to play! ");
            }
        }
        if (emojis.contains(message) && game.hasGameStarted()) {
            broadCastMessage(this.clients, game.getPlayers().get(playerIdx).getPlayerName() + ": " + message);
        }
    }

    public void playCard(Player plr) {
        game.addToDeck(plr);
        if (game.getTotalNumCards() == 0) {

            if (game.getLevel() < 12) {
                game.advanceLevel();
            } else {
                broadCastMessage(this.clients, "You have won the game. Congratulations!");
                closeSockets(this.clients);
            }

        }
        printSetup(this.clients, this.game);

        resetAllBotThreads();

        if (game.getHearts() == 0) {
            broadCastMessage(this.clients, "All hearts have been lost. Game Over!");
            closeSockets(this.clients);
        }
    }


    private void resetAllBotThreads() {
        for (Thread bot : this.getBotThreads()) { // cancel execution of all bot threads
            bot.interrupt();
        }

        this.getBotThreads().clear();

        for (Player plr : this.getGame().getPlayers()) { //restart all threads
            if (plr instanceof Bot) {
                Thread bt = new Thread(new BotThread(this, (Bot) plr));
                this.getBotThreads().add(bt);
                bt.start();
            }
        }
    }


}

class ClientHandler implements Runnable {

    private Server server;
    private InputStream client;

    public ClientHandler(Server server, InputStream client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        String message;

        // when there is a new message, broadcast to all
        Scanner sc = new Scanner(this.client);
        while (sc.hasNextLine()) {
            message = sc.nextLine();

            //System.out.println(message);
            server.processMessages(message);
        }
        sc.close();
    }
}
