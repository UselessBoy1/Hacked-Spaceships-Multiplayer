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

    public Level(KeyHandler kH) {
        backgroundImage = loadBackgroundImage("/level_background/background_l1.png");
        opponentPlayer = new Player();
        localPlayer = new LocalPlayer(kH);
    }

    public void update() {
        localPlayer.move();
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(backgroundImage, 0, 0, null);

        localPlayer.draw(g2);
        opponentPlayer.draw(g2);

        localPlayer.drawHpBar(g2);
        opponentPlayer.drawHpBar(g2);
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
