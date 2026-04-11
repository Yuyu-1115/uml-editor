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
    private final ToolBarController toolBarController;

    public UMLController(UMLModel model, UMLPanel umlPanel, ToolBarController toolBarController) {
        this.model = model;
        this.umlPanel = umlPanel;
        this.toolBarController = toolBarController;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() != MouseEvent.BUTTON1 || model.isTemporaryCreateModeActive()) {
            return;
        }
        Point p = e.getPoint();
        model.newShape(new Vector2D(p.x - 50, p.y - 50), new Vector2D(100, 100));
        umlPanel.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        toolBarController.onEditorMouseEntered();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        toolBarController.onEditorMouseExited();
    }
}
