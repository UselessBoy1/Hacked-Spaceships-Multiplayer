package Client.Players;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ShieldBar extends MissileBar {
    private final BufferedImage smallImage;

    public ShieldBar(int x, int y) {
        super(x, y);
        smallImage = loadImage("/shield/small_shield.png");
    }

    public boolean isActive() {
        if (value > 0) {
            value--;
            return true;
        }
        return false;
    }

    @Override
    protected void drawSmallPicture(Graphics2D g2) {
        g2.drawImage(smallImage, X + WIDTH, Y, null);
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
