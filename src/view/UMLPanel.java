package view;

import model.UMLModel;
import model.shape.UMLNode;
import model.shape.UMLOval;

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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (UMLNode node: umlModel.getNodesForRender()) {
            if (node instanceof UMLOval) {
                g2d.setColor(Color.WHITE);
                g2d.fillOval(node.getPosition().x, node.getPosition().y, node.getSize().x, node.getSize().y);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(node.getPosition().x, node.getPosition().y, node.getSize().x, node.getSize().y);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(node.getPosition().x, node.getPosition().y, node.getSize().x, node.getSize().y);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(node.getPosition().x, node.getPosition().y, node.getSize().x, node.getSize().y);
            }
        }
    }
}
