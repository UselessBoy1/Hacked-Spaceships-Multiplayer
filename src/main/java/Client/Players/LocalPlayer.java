package Client.Players;

import Client.Handlers.KeyHandler;

import java.awt.*;

public class LocalPlayer extends Player{
    private final KeyHandler keyHandler;
    private long lastSpaceTypedTime = 0;

    public LocalPlayer(KeyHandler kH) {
        keyHandler = kH;
        name = "PLAYER";
        x = 400;
        y = 600;
        hpBar = new HpBar(20, 720, hp, name);
        loadAllImages("player/player_");
    }

    public void shot() {
        long currentSpaceTypedTime = System.nanoTime();
        long diff = currentSpaceTypedTime - lastSpaceTypedTime;
        if (keyHandler.spaceTyped && diff > 2_000_000_00) {
            keyHandler.spaceTyped = false;
            lastSpaceTypedTime = currentSpaceTypedTime;
            bullets.add(new Bullet(x + width / 2, y, false));
//            bulletsPos.add(new Point(x + width / 2, y));
        }
    }

    public void move() {
        if (keyHandler.rightPressed && !keyHandler.leftPressed) {
            x += SPEED;
//            currentImages = imagesTurnRight;
        }
        else if (keyHandler.leftPressed && !keyHandler.rightPressed) {
            x -= SPEED;
//            currentImages = imagesTurnLeft;
        }
//        else currentImages = images;

        if (keyHandler.upPressed) y -= SPEED;
        if (keyHandler.downPressed) y += SPEED;

        shot();
        hpBar.setHp(hp);
        detectBorderCollision();
    }

    private void detectBorderCollision() {
        if (x <= 0) x = 0;
        if (x >= 1000 - width) x = 1000 - width;
        if (y <= 0) y = 0;
        if (y >= 750 - height) y = 750 - height;
    }
}
