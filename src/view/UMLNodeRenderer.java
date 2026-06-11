package view;

import model.UMLModel;
import model.UMLNodeVisitor;
import model.Vector2D;
import model.enums.PortType;
import model.shape.UMLGroup;
import model.shape.UMLNode;
import model.shape.UMLOval;
import model.shape.UMLRect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UMLNodeRenderer implements UMLNodeVisitor {
    private final Graphics2D g2d;
    private final UMLModel model;

    public UMLNodeRenderer(Graphics2D g2d, UMLModel model) {
        this.g2d = g2d;
        this.model = model;
    }

    @Override
    public void visit(UMLRect rect) {
        g2d.setColor(rect.getLabelColor());
        g2d.fillRect(rect.getPosition().x, rect.getPosition().y, rect.getSize().x, rect.getSize().y);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(rect.getPosition().x, rect.getPosition().y, rect.getSize().x, rect.getSize().y);

        drawName(rect);
        if (model.isSelected(rect) || model.isHovered(rect)) {
            drawPorts(rect);
        }
    }

    @Override
    public void visit(UMLOval oval) {
        g2d.setColor(oval.getLabelColor());
        g2d.fillOval(oval.getPosition().x, oval.getPosition().y, oval.getSize().x, oval.getSize().y);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(oval.getPosition().x, oval.getPosition().y, oval.getSize().x, oval.getSize().y);

        drawName(oval);
        if (model.isSelected(oval) || model.isHovered(oval)) {
            drawPorts(oval);
        }
    }

    @Override
    public void visit(UMLGroup group) {
        List<UMLNode> sortedChildren = new ArrayList<>(group.getChildren());
        sortedChildren.sort(Comparator.comparingInt(UMLNode::getDepth).reversed());
        for (UMLNode child : sortedChildren) {
            child.accept(this);
        }
        if (model.isSelected(group) || model.isHovered(group)) {
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
        int textX = node.getPosition().x + (node.getSize().x - textWidth) / 2;
        int textY = node.getPosition().y + (node.getSize().y + textHeight) / 2 - 2;
        g2d.setColor(Color.BLACK);
        g2d.drawString(name, textX, textY);
    }

    private void drawPorts(UMLNode node) {
        g2d.setColor(Color.BLACK);
        for (PortType portType : node.getSupportedPorts()) {
            Vector2D port = node.getPortPosition(portType);
            g2d.fillRect(port.x - 6, port.y - 6, 12, 12);
        }
    }

    private void drawGroupBoundary(UMLNode groupNode) {
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6f, 4f}, 0f));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(groupNode.getPosition().x, groupNode.getPosition().y, groupNode.getSize().x, groupNode.getSize().y);
        g2d.setStroke(oldStroke);
    }
}
