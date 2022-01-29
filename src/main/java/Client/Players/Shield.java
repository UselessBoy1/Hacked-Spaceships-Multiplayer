package Client.Players;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Shield {
    private final BufferedImage image;
    private final Player player;

    public Shield(Player player) {
        this.player = player;
        image = loadImage("/shield/shield_down.png");
    }

    public Shield(LocalPlayer player) {
        this.player = player;
        image = loadImage("/shield/shield_up.png");
    }


    public void draw(Graphics2D g2) {
        g2.drawImage(image, player.getPos().x, player.getPos().y, null);
    }

    private BufferedImage loadImage(String path) {
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
