package sockets;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String host;
    private int port;

    private boolean isHost;
    private String nickname;


    public static void main(String[] args) throws UnknownHostException, IOException {
        new Client("127.0.0.1", 12345).run();
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws UnknownHostException, IOException {
        // connect client to server

        Socket client = new Socket(host, port);

        System.out.println("Client successfully connected to server!");

        // create a new thread for server messages handling
        DataInputStream dIn = new DataInputStream(client.getInputStream());
        int clientSize = dIn.read();

        String clientAuthString = dIn.readUTF();


        new Thread(new ReceivedMessagesHandler(client.getInputStream())).start();


        Scanner sc = new Scanner(System.in);


        // read messages from keyboard and send to server
        PrintStream output = new PrintStream(client.getOutputStream());

        if (clientSize == 1) {
            System.out.println("Since you're the first client, you're also host! type 'start' to start the game\n");

            System.out.print("Specify Max Players: ");
            String maxPlayers = sc.nextLine();
            while (!maxPlayers.matches("-?(0|[1-9]\\d*)")) {
                System.out.println("please enter an integer");
                maxPlayers = sc.nextLine();
            }
            output.println(new Message(maxPlayers, clientAuthString, "maxplayers").toString());
            this.isHost = true;
        }
        // ask for a nickname
        System.out.print("Enter a nickname: ");
        output.println(new Message(sc.nextLine(), clientAuthString, "nick").toString()); // give this to the server!

        if (clientSize == 1) {
            System.out.println("Send messages: ");
        } else {
            System.out.println("awaiting host to start game..");
        }


        while (sc.hasNextLine()) {
            output.println(new Message(sc.nextLine(), clientAuthString).toString());
        }

        output.close();
        sc.close();
        client.close();
    }
}

class ReceivedMessagesHandler implements Runnable {

    private InputStream server;

    public ReceivedMessagesHandler(InputStream server) {
        this.server = server;
    }

    @Override
    public void run() {
        // receive server messages and print out to screen
        Scanner s = new Scanner(server);
        while (s.hasNextLine()) {
            String msg = s.nextLine();
            System.out.println(msg);
        }
        s.close();
    }
}
