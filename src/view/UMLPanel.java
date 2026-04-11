package view;

import model.PortHit;
import model.UMLLink;
import model.UMLModel;
import model.Vector2D;
import model.enums.LinkType;
import model.enums.PortType;
import model.shape.UMLNode;
import model.shape.UMLOval;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class UMLPanel extends JPanel {
    private static final int PORT_SIZE = 8;
    private final UMLModel umlModel;

    public UMLPanel(UMLModel umlModel) {
        this.umlModel = umlModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (UMLLink link : umlModel.getLinksForRender()) {
            drawLink(g2d, link);
        }

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
            if (umlModel.isSelected(node) || umlModel.isHovered(node)) {
                drawPorts(g2d, node);
            }
        }

        if (umlModel.hasLinkDraft()) {
            PortHit startPort = umlModel.getLinkStartPort();
            Vector2D startPosition = umlModel.getPortPosition(startPort);
            Vector2D endPosition = umlModel.getLinkPreviewPoint();
            if (startPosition != null && endPosition != null) {
                g2d.setColor(Color.GRAY);
                g2d.drawLine(startPosition.x, startPosition.y, endPosition.x, endPosition.y);
            }
        }
    }

    private void drawPorts(Graphics2D g2d, UMLNode node) {
        g2d.setColor(Color.BLACK);
        for (PortType portType : node.getSupportedPorts()) {
            Vector2D port = node.getPortPosition(portType);
            g2d.fillRect(port.x - PORT_SIZE / 2, port.y - PORT_SIZE / 2, PORT_SIZE, PORT_SIZE);
        }
    }

    private void drawLink(Graphics2D g2d, UMLLink link) {
        UMLNode sourceNode = umlModel.getNodeById(link.getSourceNodeId());
        UMLNode targetNode = umlModel.getNodeById(link.getTargetNodeId());
        if (sourceNode == null || targetNode == null) {
            return;
        }

        Vector2D start = sourceNode.getPortPosition(link.getSourcePort());
        Vector2D end = targetNode.getPortPosition(link.getTargetPort());
        g2d.setColor(Color.BLACK);
        g2d.drawLine(start.x, start.y, end.x, end.y);

        if (link.getType() == LinkType.GENERALIZATION) {
            drawTriangleArrow(g2d, start, end);
        } else if (link.getType() == LinkType.COMPOSITION) {
            drawDiamondArrow(g2d, start, end);
        }
    }

    private void drawTriangleArrow(Graphics2D g2d, Vector2D start, Vector2D end) {
        Vector2D direction = unitDirection(start, end);
        Vector2D perpendicular = new Vector2D(-direction.y, direction.x);
        int arrowLength = 16;
        int arrowWidth = 8;

        int baseX = end.x - direction.x * arrowLength;
        int baseY = end.y - direction.y * arrowLength;
        int leftX = baseX + perpendicular.x * arrowWidth;
        int leftY = baseY + perpendicular.y * arrowWidth;
        int rightX = baseX - perpendicular.x * arrowWidth;
        int rightY = baseY - perpendicular.y * arrowWidth;

        Path2D triangle = new Path2D.Double();
        triangle.moveTo(end.x, end.y);
        triangle.lineTo(leftX, leftY);
        triangle.lineTo(rightX, rightY);
        triangle.closePath();
        g2d.setColor(Color.WHITE);
        g2d.fill(triangle);
        g2d.setColor(Color.BLACK);
        g2d.draw(triangle);
    }

    private void drawDiamondArrow(Graphics2D g2d, Vector2D start, Vector2D end) {
        Vector2D direction = unitDirection(start, end);
        Vector2D perpendicular = new Vector2D(-direction.y, direction.x);
        int length = 14;
        int width = 7;

        int backX = end.x - direction.x * length * 2;
        int backY = end.y - direction.y * length * 2;
        int middleX = end.x - direction.x * length;
        int middleY = end.y - direction.y * length;

        int leftX = middleX + perpendicular.x * width;
        int leftY = middleY + perpendicular.y * width;
        int rightX = middleX - perpendicular.x * width;
        int rightY = middleY - perpendicular.y * width;

        Path2D diamond = new Path2D.Double();
        diamond.moveTo(end.x, end.y);
        diamond.lineTo(leftX, leftY);
        diamond.lineTo(backX, backY);
        diamond.lineTo(rightX, rightY);
        diamond.closePath();
        g2d.fill(diamond);
    }

    private Vector2D unitDirection(Vector2D start, Vector2D end) {
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        double magnitude = Math.hypot(dx, dy);
        if (magnitude == 0) {
            return new Vector2D(1, 0);
        }
        return new Vector2D((int) Math.round(dx / magnitude), (int) Math.round(dy / magnitude));
    }
}
