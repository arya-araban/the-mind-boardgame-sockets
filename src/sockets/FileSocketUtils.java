package sockets;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileSocketUtils {

    public static void SetClientInfo(Client client) throws IOException { // set host and port from file. if exception happened, set default values.
        try {
            URL path = FileSocketUtils.class.getResource("ClientConfig.txt");
            List<String> lst = Files.readAllLines(Path.of(path.getPath()));

            for (String line : lst) {
                if (line.contains("host"))
                    client.setHost(line.substring(line.lastIndexOf("=") + 1));
                else if (line.contains("port"))
                    client.setPort(Integer.parseInt(line.substring(line.lastIndexOf("=") + 1)));
            }

        } catch (Exception e) {
            client.setHost("127.0.0.1");
            client.setPort(8000);
        }
    }


    public static void SetServerInfo(Server server) throws IOException { // set  port from file. if exception happened, set default values.
        try {
            URL path = FileSocketUtils.class.getResource("ServerConfig.txt");
            List<String> lst = Files.readAllLines(Path.of(path.getPath()));
            for (String line : lst) {
                if (line.contains("port"))
                    server.setPort(Integer.parseInt(line.substring(line.lastIndexOf("=") + 1)));
            }
        } catch (Exception e) {
            server.setPort(8000);
        }
    }

}
