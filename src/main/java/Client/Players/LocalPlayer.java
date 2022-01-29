package Client.Players;

import Client.Handlers.KeyHandler;

import java.awt.*;

public class LocalPlayer extends Player{
    private final KeyHandler keyHandler;
    private long lastSpaceTypedTime = 0;

    public LocalPlayer(KeyHandler kH) {
        keyHandler = kH;
        name = "PLAYER";
        pos = new Point(440, 600);
        hpBar = new HpBar(5, 720, hp, name);
        loadAllImages("player/player_");
    }

    public void shot() {
        // this time difference counter prevents player from shooting too fast
        long currentSpaceTypedTime = System.nanoTime();
        long diff = currentSpaceTypedTime - lastSpaceTypedTime;
        if (keyHandler.spaceTyped && diff > 2_000_000_00) {
            keyHandler.spaceTyped = false;
            lastSpaceTypedTime = currentSpaceTypedTime;
            bullets.add(new Bullet(pos.x + WIDTH / 2, pos.y, false));
        }
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

        shot();
        hpBar.setHp(hp);
        detectBorderCollision();
    }

    private void detectBorderCollision() {
        if (pos.x <= 0) pos.x = 0;
        if (pos.x >= 1000 - WIDTH) pos.x = 1000 - WIDTH;
        if (pos.y <= 0) pos.y = 0;
        if (pos.y >= 750 - HEIGHT) pos.y = 750 - HEIGHT;
    }
}
