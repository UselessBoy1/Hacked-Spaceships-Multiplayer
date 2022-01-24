package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    private void start() throws IOException {
        serverSocket = new ServerSocket(6666);

        System.out.println("[SERVER] is running");

        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    private void stop() throws IOException {
        serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                PrintWriter outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String receivedLine;
                while ( (receivedLine = inputFromClient.readLine()) != null) {

                    System.out.println("[Client] " + receivedLine);
                }

                inputFromClient.close();
                outputToClient.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
        server.stop();
    }
}
