package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private Socket clientSocket;
    private PrintWriter outputToServer;
    private BufferedReader inputFromServer;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String receiveMessage() throws IOException {
        return inputFromServer.readLine();
    }

    public void sendMessage(String message) {
        outputToServer.println(message);
    }


    public void stopConnection() throws IOException {
        inputFromServer.close();
        outputToServer.close();
        clientSocket.close();
    }
}
