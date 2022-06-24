package sockets;


import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static sockets.FileSocketUtils.SetClientInfo;

public class Client {


    private String host;

    private int port;

    private boolean isHost;
    private String nickname;


    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        System.out.println("Awaiting server response.."); //if server not open, when it opens client will connect.
        while (true) {
            try {
                new Client().run();
                break;
            } catch (Exception e) {
                // e.printStackTrace();
                TimeUnit.SECONDS.sleep(2);
            }
        }
    }

    public Client() throws IOException {
        SetClientInfo(this);
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
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
            if (msg.equals("kill")) {
                System.exit(0);
            }
            System.out.println(msg);

        }

        s.close();
    }
}
