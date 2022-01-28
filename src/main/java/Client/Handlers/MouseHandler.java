package Client.Handlers;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// class which handles mouse events
public class MouseHandler implements MouseListener, MouseMotionListener {
    public boolean clicked = false;
    public Point mousePos = new Point(0, 0);

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        clicked = true;
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        mousePos.x = mouseEvent.getX();
        mousePos.y = mouseEvent.getY();
    }

    // these methods must be implemented even if they are empty
    @Override
    public void mousePressed(MouseEvent mouseEvent) {}
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {}
    @Override
    public void mouseEntered(MouseEvent mouseEvent) {}
    @Override
    public void mouseExited(MouseEvent mouseEvent) {}
    @Override
    public void mouseDragged(MouseEvent mouseEvent) {}
}
