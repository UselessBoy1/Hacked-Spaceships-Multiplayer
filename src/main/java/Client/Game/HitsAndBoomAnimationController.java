package Client.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class HitsAndBoomAnimationController {
    private final LinkedList<BoomPlayer> boomPlayers = new LinkedList<>();
    private final LinkedList<Hit> bulletHits = new LinkedList<>();
    private final int NUM_OF_IMAGES = 11;
    private final BufferedImage[] boomImages = new BufferedImage[NUM_OF_IMAGES];

    public HitsAndBoomAnimationController() {
        loadBoomImages();
    }

    public void draw(Graphics2D g2) {
        drawAllBoomAnimations(g2);
        drawHits(g2);
    }

    private class BoomPlayer {
        private final Point pos;
        private boolean isBoom = true;
        private int boomAnimationCounter = 0;

        private BoomPlayer(Point pos) {
            this.pos = pos;
        }

        private void drawBoomAnimation(Graphics2D g2) {
            boomAnimationCounter++;
            if (boomAnimationCounter >= 58) {
                isBoom = false;
            }
            g2.drawImage(boomImages[boomAnimationCounter / 6], pos.x, pos.y, null);
        }
    }
    public void startBoomAnimation(Point lastPosition) {
        boomPlayers.add(new BoomPlayer(lastPosition));
    }

    private void drawAllBoomAnimations(Graphics2D g2) {
        for (int i = 0; i < boomPlayers.size(); ++i) {
            BoomPlayer p = boomPlayers.get(i);
            p.drawBoomAnimation(g2);
            if (!p.isBoom) {
                boomPlayers.remove(i);
                --i;
            }
        }
    }

    private class Hit {
        private final BufferedImage[] hitImages = Arrays.copyOf(boomImages, NUM_OF_IMAGES);
        private final int x, y;
        private int boomCounter = 0;
        public boolean isBoom = true;

        public Hit(int x, int y, double scale) {
            this.x = x - 5;
            this.y = y;
            scaleBoomImages(scale);
        }

        public void drawBoomAnimation(Graphics2D g2) {
            boomCounter++;
            if (boomCounter >= 40) {
                isBoom = false;
            }
            g2.drawImage(hitImages[boomCounter / 4], x, y, null);
        }

        protected void scaleBoomImages(double scale) {
            for (int i = 0; i < hitImages.length; ++i) {
                int w = hitImages[i].getWidth();
                int h = hitImages[i].getHeight();
                int integerScale = (int) Math.ceil(scale);
                BufferedImage after = new BufferedImage(w * integerScale, h * integerScale, BufferedImage.TYPE_INT_ARGB);
                AffineTransform at = new AffineTransform();
                at.scale(scale, scale);
                AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                after = scaleOp.filter(hitImages[i], after);
                hitImages[i] = after;
            }
        }
    }

    public void startDrawingHit(int x, int y, double scale) {
        bulletHits.add(new Hit(x, y, scale));
    }

    private void drawHits(Graphics2D g2) {
        for (int i = 0; i < bulletHits.size(); ++i) {
            Hit h = bulletHits.get(i);
            h.drawBoomAnimation(g2);
            if (!h.isBoom) {
                bulletHits.remove(i);
                --i;
            }
        }
    }

    private void loadBoomImages() {
        for (int i = 1; i <= NUM_OF_IMAGES; ++i) {
            boomImages[i - 1] = loadImage("/boom_animation/boom_" + i + ".png");
        }
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
