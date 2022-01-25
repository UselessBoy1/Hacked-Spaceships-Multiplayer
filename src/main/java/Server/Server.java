package Server;

import GameObject.Game;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private int idCounter = 0;
    private final Map<Integer, Game> gamesMap = new ConcurrentHashMap<>();

    public void start() throws IOException {
        serverSocket = new ServerSocket(6666);

        System.out.println("[SERVER] is running");

        while (true) {
            // wait for new client
            Socket newClient = serverSocket.accept();

            int gameId = idCounter / 2 + 1;
            int playerId = 1;
            idCounter++;

            System.out.println("[SERVER] client "  + idCounter + " joined");

            // if idCounter is odd -> first player joined -> make new game
            if (idCounter % 2 == 1) {
                gamesMap.put(gameId, new Game(gameId));
                System.out.println("[SERVER] create game " + gameId);
            }
            // if idCounter is even -> second player joined -> start new game and give second player different id
            else {
                gamesMap.get(gameId).setReady(true);
                System.out.println("[SERVER] game " + gameId + " is ready to play");
                playerId = 2;
            }

            // start new thread which handles one client
            new ClientHandler(newClient, playerId, gameId, idCounter).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final int PLAYER_ID;
        private final int GAME_ID;
        private final int SOCKET_ID;
        private final ObjectOutputStream outputToClient;
        private final ObjectInputStream inputFromClient;

        public ClientHandler(Socket socket, int playerId, int gameId, int socketId) throws IOException {
            clientSocket = socket;
            PLAYER_ID = playerId;
            GAME_ID = gameId;
            SOCKET_ID = socketId;
            outputToClient = new ObjectOutputStream(clientSocket.getOutputStream());
            inputFromClient = new ObjectInputStream(clientSocket.getInputStream());
        }

        @Override
        public void run() {
            try {
//                outputToClient.println("[SERVER] Hello client " + SOCKET_ID); // test
                Game game = gamesMap.get(GAME_ID);
                outputToClient.writeObject(game);

                Game gameFromClient;
                try {
                    while ( (gameFromClient = (Game) inputFromClient.readObject()) != null) {
                        if ( ! gamesMap.containsKey(GAME_ID)) {
                            break;
                        }
                        System.out.println("[Client " + SOCKET_ID + "] " + gameFromClient.isReady());
                    }
                } catch (Exception ignored) {} // client disconnected unexpectedly

                stopConnectionWithThisClient();

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[SERVER] client " + SOCKET_ID + " disconnected");
            if (gamesMap.containsKey(GAME_ID)) {
                gamesMap.remove(GAME_ID);
                idCounter--;
                System.out.println("[SERVER] destroying game " + GAME_ID);
            }
        }

        private void stopConnectionWithThisClient() throws IOException {
            inputFromClient.close();
            outputToClient.close();
            clientSocket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
        server.stop();
    }
}
