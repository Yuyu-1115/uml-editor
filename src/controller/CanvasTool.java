package controller;

import java.awt.event.MouseEvent;

public interface CanvasTool {
    void mousePressed(MouseEvent e);
    void mouseDragged(MouseEvent e);
    void mouseReleased(MouseEvent e);
    void mouseMoved(MouseEvent e);
}
