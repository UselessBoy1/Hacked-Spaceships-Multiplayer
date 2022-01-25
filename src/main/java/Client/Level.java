package Client;

import Client.Handlers.KeyHandler;
import Client.Players.LocalPlayer;
import Client.Players.Player;
import GameObject.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Level {
    private final BufferedImage backgroundImage;
    private Player opponentPlayer;
    private LocalPlayer localPlayer;

    private final int FONT_SIZE = 50;
    private final Font font = new Font("FreeSans", Font.BOLD, FONT_SIZE);
    // states
    private final String CONNECTING = "connecting";
    private final String WAITING = "waiting";
    private final String GAME = "game";

    private String state = CONNECTING;

    private final SocketClient socketClient = new SocketClient();
    private Game gameObjFromServer;

    public Level(KeyHandler kH) {
        backgroundImage = loadBackgroundImage("/level_background/background_l1.png");
        opponentPlayer = new Player();
        localPlayer = new LocalPlayer(kH);
    }

    // for test
    public String getState() {
        return state;
    }

    public void update() {
        switch (state) {
            case CONNECTING -> {
                try {
                    socketClient.startConnection("127.0.0.1", 6666);
                    gameObjFromServer = socketClient.sendAndReceiveGame(null);
                    state = WAITING;
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Fail to connect");
                    state = CONNECTING;
                }
            }
            case WAITING -> {
                try {
                    gameObjFromServer = socketClient.sendAndReceiveGame(gameObjFromServer);
//                    System.out.println(gameObjFromServer); // debug
                    if (gameObjFromServer.isReady()) {
                        state = GAME;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Lost connection");
                    state = CONNECTING;
                }
            }
            case GAME -> {
                localPlayer.move();
                try {
                    gameObjFromServer = socketClient.sendAndReceiveGame(gameObjFromServer);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Lost connection");
                    state = CONNECTING;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(backgroundImage, 0, 0, null);
        switch (state) {
            case CONNECTING -> {
                g2.setColor(Color.black);
                g2.setFont(font);
                g2.drawString("Connecting to server", 250, 300);
            }
            case WAITING -> {
                localPlayer.draw(g2);
                localPlayer.drawHpBar(g2);
                g2.setColor(Color.black);
                g2.setFont(font);
                g2.drawString("Waiting for opponent", 250, 300);
            }
            case GAME -> {
                localPlayer.draw(g2);
                opponentPlayer.draw(g2);
                localPlayer.drawHpBar(g2);
                opponentPlayer.drawHpBar(g2);

                g2.setColor(Color.black);
                g2.setFont(new Font("FreeSans", Font.BOLD, 40));
                g2.drawString("GAME: " + gameObjFromServer.getID(), 800, 30);
            }
        }
    }

    private BufferedImage loadBackgroundImage(String path) {
        BufferedImage bg = null;
        try {
            bg = ImageIO.read(
                    Objects.requireNonNull(getClass().getResourceAsStream(path))
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bg;
    }
}
