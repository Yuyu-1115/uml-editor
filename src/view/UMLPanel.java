package view;

import model.UMLModel;
import model.shape.UMLNode;

import javax.swing.*;
import java.awt.*;

public class UMLPanel extends JPanel {
    private final UMLModel umlModel;

    public UMLPanel(UMLModel umlModel) {
        this.umlModel = umlModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (UMLNode node: umlModel.getObjectRegistry().values()) {
            g2d.drawRect(node.getPosition().x, node.getPosition().y, node.getSize().x, node.getSize().y);
        }
    }
}
