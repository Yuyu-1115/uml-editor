package controller;

import model.UMLModel;
import model.Vector2D;
import view.UMLPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UMLController extends MouseAdapter {
    private final UMLModel model;
    private final UMLPanel umlPanel;

    public UMLController(UMLModel model, UMLPanel umlPanel) {
        this.model = model;
        this.umlPanel = umlPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        Point p = e.getPoint();
        model.newShape(new Vector2D(p.x - 50, p.y - 50), new Vector2D(100, 100));
        umlPanel.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        System.out.println("Mouse entered");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        System.out.println("Mouse exited");
    }
}
