package Client;

import Client.Players.LocalPlayer;
import Client.Players.Player;

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
    private final String CONNECTING = "conn";
    private final String WAITING = "wait";
    private final String GAME = "game";

    private String state = CONNECTING;

    private final SocketClient socketClient = new SocketClient();


    public Level(KeyHandler kH) {
        backgroundImage = loadBackgroundImage("/level_background/background_l1.png");
        opponentPlayer = new Player();
        localPlayer = new LocalPlayer(kH);
    }

    public void update() {
        switch (state) {
            case CONNECTING -> {
                try {
                    socketClient.startConnection("127.0.0.1", 6666);
//                    System.out.println(socketClient.receiveMessage());
                    state = WAITING;
                } catch (IOException e) {
                    e.printStackTrace();
                    state = CONNECTING;
                }
            }
            case WAITING -> {

            }
            case GAME -> {
                localPlayer.move();
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
