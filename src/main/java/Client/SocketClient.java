package Client;

import GameObject.Game;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private Socket clientSocket;
    private ObjectOutputStream outputToServer;
    private ObjectInputStream inputFromServer;

    public void startConnection(String ip, int port) throws IOException {
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
