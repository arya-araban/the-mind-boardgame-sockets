package sockets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import mindgame.Game;

import static sockets.ServerGameUtils.*;

public class Server {
    private Game game;
    private int port;
    private List<PrintStream> clients;
    private List<InputStream> clientsIS;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        new Server(12345).run();
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<PrintStream>();
        this.clientsIS = new ArrayList<InputStream>();
    }

    public void run() throws IOException {
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
            this.clientsIS.add(client.getInputStream());
            DataOutputStream client_dOut = new DataOutputStream(client.getOutputStream());

            client_dOut.write(clients.size());
            client_dOut.flush();


            // create a new thread for client handling
            new Thread(new ClientHandler(this, client.getInputStream())).start();
        }
    }


    void broadcastMessages(InputStream client, String msg) {


        if (msg.contains("~~maxplayers") && client.equals(clientsIS.get(0))) {
            msg = msg.substring(0, msg.indexOf('~'));
            game = new Game(Integer.parseInt(msg));

        }

        if (msg.contains("~~nick")) {
            msg = msg.substring(0, msg.indexOf('~'));
            System.out.println(msg);
            game.addClientPlayer(msg);

        }

        if (msg.equals("start") && client.equals(clientsIS.get(0))) {
            game.startGame();
            printSetup(this.clients, game);
        }

        if (msg.equals("p")) {
            int playerIdx = clientsIS.indexOf(client);
            try {
                game.addToDeck(game.getPlayers().get(playerIdx));
                if (checkAllHandsEmpty(this.game)) {
                    game.advanceLevel();
                }
                printSetup(this.clients, this.game);

                if (game.getHearts() == 0) {
                    printGameOver(this.clients);
                }

            } catch (Exception IndexOutOfBoundsException) {
                clients.get(playerIdx).println("you don't have any cards to play!");
            }
        }

        if (msg.equals("n")) {
            int playerIdx = clientsIS.indexOf(client);
            if (this.game.getNinjas() > 0) {
                this.game.activateNinja();

                if (checkAllHandsEmpty(this.game)) {
                    game.advanceLevel();
                }

                printSetup(this.clients, this.game);
            } else {
                clients.get(playerIdx).println("There are no ninja cards to play! ");
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
            server.broadcastMessages(client, message); //reminder: client is inputstream
        }
        sc.close();
    }
}
