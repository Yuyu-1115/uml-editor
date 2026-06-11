package view.render;

import model.SelectionModel;
import model.UMLModel;
import model.node.UMLNodeVisitor;
import record.Vector2D;
import model.enums.PortType;
import model.node.UMLGroup;
import model.node.UMLNode;
import model.node.UMLOval;
import model.node.UMLRect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UMLNodeRenderer implements UMLNodeVisitor {
    private Graphics2D g2d;
    private final SelectionModel selectionModel;

    public UMLNodeRenderer(UMLModel model) {
        this.selectionModel = model.getSelectionModel();
    }

    public void setGraphics(Graphics2D g2d) {
        this.g2d = g2d;
    }

    @Override
    public void visit(UMLRect rect) {
        g2d.setColor(rect.getLabelColor());
        g2d.fillRect(rect.getPosition().x(), rect.getPosition().y(), rect.getSize().x(), rect.getSize().y());
        g2d.setColor(Color.BLACK);
        g2d.drawRect(rect.getPosition().x(), rect.getPosition().y(), rect.getSize().x(), rect.getSize().y());

        drawName(rect);
        if (selectionModel.isSelected(rect) || selectionModel.isHovered(rect)) {
            drawPorts(rect);
        }
    }

    @Override
    public void visit(UMLOval oval) {
        g2d.setColor(oval.getLabelColor());
        g2d.fillOval(oval.getPosition().x(), oval.getPosition().y(), oval.getSize().x(), oval.getSize().y());
        g2d.setColor(Color.BLACK);
        g2d.drawOval(oval.getPosition().x(), oval.getPosition().y(), oval.getSize().x(), oval.getSize().y());

        drawName(oval);
        if (selectionModel.isSelected(oval) || selectionModel.isHovered(oval)) {
            drawPorts(oval);
        }
    }

    @Override
    public void visit(UMLGroup group) {
        List<UMLNode> sortedNodes = new ArrayList<>(group.getChildren());
        sortedNodes.sort(Comparator.comparingInt(UMLNode::getDepth).reversed());
        for (UMLNode node : sortedNodes) {
            node.accept(this);
        }
        if (selectionModel.isSelected(group) || selectionModel.isHovered(group)) {
            drawGroupBoundary(group);
        }
    }

    private void drawName(UMLNode node) {
        String name = node.getName();
        if (name == null || name.isBlank()) {
            return;
        }
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(name);
        int textHeight = metrics.getAscent();
        int textX = node.getPosition().x() + (node.getSize().x() - textWidth) / 2;
        int textY = node.getPosition().y() + (node.getSize().y() + textHeight) / 2 - 2;
        g2d.setColor(Color.BLACK);
        g2d.drawString(name, textX, textY);
    }

    private void drawPorts(UMLNode node) {
        g2d.setColor(Color.BLACK);
        for (PortType portType : node.getSupportedPorts()) {
            Vector2D port = node.getPortPosition(portType);
            g2d.fillRect(port.x() - 6, port.y() - 6, 12, 12);
        }
    }

    private void drawGroupBoundary(UMLNode groupNode) {
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6f, 4f}, 0f));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(groupNode.getPosition().x(), groupNode.getPosition().y(), groupNode.getSize().x(), groupNode.getSize().y());
        g2d.setStroke(oldStroke);
    }
}
