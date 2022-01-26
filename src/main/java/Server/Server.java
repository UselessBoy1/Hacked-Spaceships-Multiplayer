package Server;

import GameObject.Game;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private int idCounter = 0;
    private final Map<Integer, Game> gamesMap = new ConcurrentHashMap<>();

    public void start() throws IOException {
        InetAddress address = InetAddress.getByName("192.168.0.105");
        serverSocket = new ServerSocket(6666, 50, address);

        System.out.println("[SERVER] is running");

        while (true) {
            // wait for new client
            Socket newClient = serverSocket.accept();

            int gameId = idCounter / 2 + 1;
            // make sure that will be created new game if client connects after idCount--;
            while (gamesMap.containsKey(gameId) && gamesMap.get(gameId).isReady()) {
                gameId++;
            }
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
            outputToClient = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            outputToClient.flush();
            inputFromClient = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                // sends to client first game object
                Game serverGame = gamesMap.get(GAME_ID);
                serverGame.setPlayerId(PLAYER_ID);
                inputFromClient.readObject();
                outputToClient.writeObject(serverGame);
                outputToClient.flush();

                Game gameFromClient;
                try {
                    // get game data from client
                    while ( (gameFromClient = (Game) inputFromClient.readObject()) != null) {
                        if ( ! gamesMap.containsKey(GAME_ID)) {
                        // this means that current game no longer exists so player must join to new game
                            break;
                        }
                        outputToClient.reset();
                        Game gameToSend;
                        if (PLAYER_ID == 1) {
                            gameToSend = gamesMap.get(GAME_ID).determineAndUpdate1(gameFromClient);
                        }
                        else { // 2
                            gameToSend = gamesMap.get(GAME_ID).determineAndUpdate2(gameFromClient);
                        }

                        outputToClient.writeObject(gameToSend);
                        outputToClient.flush();
                    }

                }catch (SocketException | EOFException ignored) {}
                catch (Exception e) {
                    e.printStackTrace();
                } // client disconnected unexpectedly

                stopConnectionWithThisClient();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("[SERVER] client " + SOCKET_ID + " disconnected");
            idCounter--;
            if (gamesMap.containsKey(GAME_ID)) {
                gamesMap.remove(GAME_ID);
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
