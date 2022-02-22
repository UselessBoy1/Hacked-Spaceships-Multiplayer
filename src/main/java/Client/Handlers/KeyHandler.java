package Client.Handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// class which handles keyboard events
public class KeyHandler implements KeyListener {
    public boolean rightPressed, leftPressed;
    public boolean upPressed, downPressed;
    public boolean spaceTyped = false;
    public boolean rTyped = true;
    public boolean qTyped = false;
    // must be implemented
    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        rTyped = true;
        int code = keyEvent.getKeyCode();
        if (code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_SPACE) spaceTyped = true;
        //if (code == KeyEvent.VK_R) rTyped = true;
        if (code == KeyEvent.VK_Q) qTyped = true;
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        int code = keyEvent.getKeyCode();
        if (code == KeyEvent.VK_RIGHT) rightPressed = false;
        if (code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_SPACE) spaceTyped = false;
        //if (code == KeyEvent.VK_R) rTyped = false;
        if (code == KeyEvent.VK_Q) qTyped = false;
    }
}
