package Client.Players;

import Client.Handlers.KeyHandler;

import java.awt.*;

public class LocalPlayer extends Player{
    private final KeyHandler keyHandler;
    private long lastSpaceTypedTime = 0;
    private final MissileBar missileBar = new MissileBar(820, 720);
    private final ShieldBar shieldBar = new ShieldBar(820, 680);

    public LocalPlayer(KeyHandler kH) {
        keyHandler = kH;
        name = "PLAYER";
        pos = new Point(440, 600);
        shield = new Shield(this);
        hpBar = new HpBar(5, 720, hp, name);
        loadAllImages("player/player_");
    }

    public void drawBars(Graphics2D g2) {
        drawHpBar(g2);
        missileBar.draw(g2);
        shieldBar.draw(g2);
    }

    public void shot(int opponentX) {
        // this time difference counter prevents player from shooting too fast
        long currentSpaceTypedTime = System.nanoTime();
        long diff = currentSpaceTypedTime - lastSpaceTypedTime;
        if (keyHandler.spaceTyped && diff > 2_000_000_00) {
//        if (diff > 2_000_000_00) {
            keyHandler.spaceTyped = false;
            lastSpaceTypedTime = currentSpaceTypedTime;
            bullets.add(new Bullet(pos.x + WIDTH / 2, pos.y, false));
        }
        if (missileBar.isReady()) {
            if (keyHandler.rTyped) {
                keyHandler.rTyped = false;
                missileBar.use();
                bullets.add(new Missile(pos.x + WIDTH / 2, pos.y, false, opponentX));
            }
        }
        else
            missileBar.update();
    }

    public void move() {
        if (keyHandler.rightPressed && !keyHandler.leftPressed) {
            pos.x += SPEED;
        }
        else if (keyHandler.leftPressed && !keyHandler.rightPressed) {
            pos.x -= SPEED;
        }

        if (keyHandler.upPressed) pos.y -= SPEED;
        if (keyHandler.downPressed) pos.y += SPEED;

        if (shieldActive) {
            boolean active = shieldBar.isActive();
            if (!active) {
                shieldActive = false;
            }
        }
        else {
            if (shieldBar.isReady()) {
                if (keyHandler.qTyped) {
                    keyHandler.qTyped = false;
                    shieldActive = true;
                }
            } else {
                shieldBar.update();
            }
        }

        hpBar.setValue(hp);
        detectBorderCollision();
    }

    private void detectBorderCollision() {
        if (pos.x <= 0) pos.x = 0;
        if (pos.x >= 1000 - WIDTH) pos.x = 1000 - WIDTH;
        if (pos.y <= 375) pos.y = 375;
        if (pos.y >= 750 - HEIGHT) pos.y = 750 - HEIGHT;
    }
}
