package Server;

import GameDataObject.GameDataObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private long idCounter = 0;
    private long gameId = 1;
    private final Map<Long, GameDataObject> gamesMap = new ConcurrentHashMap<>();

    public void start() throws IOException {
        Properties properties = new Properties();
        try (InputStream fileIn = getClass().getResourceAsStream("/app.config")){
            properties.load(fileIn);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        String ip = properties.getProperty("ip");
        InetAddress address = InetAddress.getByName(ip);
        int port = Integer.parseInt(properties.getProperty("port"));

        serverSocket = new ServerSocket(port, 50, address);

        System.out.println("[SERVER] is running");

        while (true) {
            // wait for new client
            Socket newClient = serverSocket.accept();

            idCounter++;
            System.out.println("[SERVER] client "  + idCounter + " joined");

            if ( ! gamesMap.containsKey(gameId)) {
                gamesMap.put(gameId, new GameDataObject(gameId));
                System.out.println("[SERVER] create game " + gameId);

                // start new thread which handles first player
                new ClientHandler(newClient, 1, gameId, idCounter).start();
            }
            else {
                gamesMap.get(gameId).setReady(true);
                System.out.println("[SERVER] game " + gameId + " is ready to play");
                // start new thread which handles second player
                new ClientHandler(newClient, 2, gameId, idCounter).start();

                gameId++;
            }
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final int PLAYER_ID;
        private final long GAME_ID;
        private final long SOCKET_ID;
        private final ObjectOutputStream outputToClient;
        private final ObjectInputStream inputFromClient;

        public ClientHandler(Socket socket, int playerId, long gameId, long socketId) throws IOException {
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
                GameDataObject serverGameDataObject = gamesMap.get(GAME_ID);
                serverGameDataObject.setPlayerId(PLAYER_ID);
                inputFromClient.readObject();
                outputToClient.writeObject(serverGameDataObject);
                outputToClient.flush();

                GameDataObject gameDataObjectFromClient;
                try {
                    // get game data from client
                    while ( (gameDataObjectFromClient = (GameDataObject) inputFromClient.readObject()) != null) {
                        if ( ! gamesMap.containsKey(GAME_ID)) {
                        // this means that current game no longer exists so player must join to new game
                            break;
                        }
                        outputToClient.reset();
                        GameDataObject gameDataObjectToSend;
                        if (PLAYER_ID == 1) {
                            gameDataObjectToSend = gamesMap.get(GAME_ID).determineAndUpdate1(gameDataObjectFromClient);
                        }
                        else { // 2
                            gameDataObjectToSend = gamesMap.get(GAME_ID).determineAndUpdate2(gameDataObjectFromClient);
                        }

                        outputToClient.writeObject(gameDataObjectToSend);
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
