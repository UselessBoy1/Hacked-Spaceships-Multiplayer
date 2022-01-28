package Client;

import GameDataObject.GameDataObject;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

// this class is used to communicate client with server
public class SocketClient {
    private Socket clientSocket;
    private ObjectOutputStream outputToServer;
    private ObjectInputStream inputFromServer;

    public void startConnection() throws IOException {
        // load ip and port number from app.config
        Properties properties = new Properties();
        try (InputStream fileIn = getClass().getResourceAsStream("/app.config")){
            properties.load(fileIn);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        String ip = properties.getProperty("ip");
        int port = Integer.parseInt(properties.getProperty("port"));

        clientSocket = new Socket(ip, port);
        clientSocket.setTcpNoDelay(true);
        // output and input streams to communicate with server
        outputToServer = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        outputToServer.flush();
        inputFromServer = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
    }

    public GameDataObject sendAndReceiveGame(GameDataObject gameDataObject) throws IOException, ClassNotFoundException {
        // send
        outputToServer.reset();
        outputToServer.writeObject(gameDataObject);
        outputToServer.flush();

        // receive
        return (GameDataObject) inputFromServer.readObject();
    }

    public void stopConnection() throws IOException {
        inputFromServer.close();
        outputToServer.close();
        clientSocket.close();
    }
}
