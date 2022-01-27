package Client;

import GameObject.Game;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

public class SocketClient {
    private Socket clientSocket;
    private ObjectOutputStream outputToServer;
    private ObjectInputStream inputFromServer;

    public void startConnection() throws IOException {
        Properties properties = new Properties();
        try (InputStream fileIn = getClass().getResourceAsStream("/app.config")){
            properties.load(fileIn);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        String ip = properties.getProperty("ip");
        int port = Integer.parseInt(properties.getProperty("port"));

        clientSocket = new Socket(ip, port);
        outputToServer = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        outputToServer.flush();
        inputFromServer = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
    }

    public Game sendAndReceiveGame(Game game) throws IOException, ClassNotFoundException {
        outputToServer.reset();
        outputToServer.writeObject(game);
        outputToServer.flush();
        return (Game) inputFromServer.readObject();
    }

    public void stopConnection() throws IOException {
        inputFromServer.close();
        outputToServer.close();
        clientSocket.close();
    }
}
