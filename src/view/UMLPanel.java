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
    private static final int PORT_SIZE = 12;
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

        if (link.getType() == LinkType.ASSOCIATION) {
            drawAssociationArrow(g2d, start, end);
        } else if (link.getType() == LinkType.GENERALIZATION) {
            drawTriangleArrow(g2d, start, end);
        } else if (link.getType() == LinkType.COMPOSITION) {
            drawDiamondArrow(g2d, start, end);
        }
    }

    private void drawAssociationArrow(Graphics2D g2d, Vector2D start, Vector2D end) {
        double[] unit = calculateUnitDirection(start, end);
        double ux = unit[0];
        double uy = unit[1];
        double px = -uy;
        double py = ux;
        int tipX = end.x;
        int tipY = end.y;
        int armLength = 14;
        int armWidth = 7;
        int baseX = (int) Math.round(tipX - ux * armLength);
        int baseY = (int) Math.round(tipY - uy * armLength);
        int leftX = (int) Math.round(baseX + px * armWidth);
        int leftY = (int) Math.round(baseY + py * armWidth);
        int rightX = (int) Math.round(baseX - px * armWidth);
        int rightY = (int) Math.round(baseY - py * armWidth);

        g2d.drawLine(start.x, start.y, tipX, tipY);
        g2d.drawLine(tipX, tipY, leftX, leftY);
        g2d.drawLine(tipX, tipY, rightX, rightY);
    }

    private void drawTriangleArrow(Graphics2D g2d, Vector2D start, Vector2D end) {
        double[] unit = calculateUnitDirection(start, end);
        double ux = unit[0];
        double uy = unit[1];
        double px = -uy;
        double py = ux;
        int arrowLength = 18;
        int arrowWidth = 9;
        int tipX = end.x;
        int tipY = end.y;
        int baseX = (int) Math.round(tipX - ux * arrowLength);
        int baseY = (int) Math.round(tipY - uy * arrowLength);
        int leftX = (int) Math.round(baseX + px * arrowWidth);
        int leftY = (int) Math.round(baseY + py * arrowWidth);
        int rightX = (int) Math.round(baseX - px * arrowWidth);
        int rightY = (int) Math.round(baseY - py * arrowWidth);

        Path2D triangle = new Path2D.Double();
        triangle.moveTo(tipX, tipY);
        triangle.lineTo(leftX, leftY);
        triangle.lineTo(rightX, rightY);
        triangle.closePath();
        g2d.drawLine(start.x, start.y, baseX, baseY);
        g2d.setColor(Color.WHITE);
        g2d.fill(triangle);
        g2d.setColor(Color.BLACK);
        g2d.draw(triangle);
    }

    private void drawDiamondArrow(Graphics2D g2d, Vector2D start, Vector2D end) {
        double[] unit = calculateUnitDirection(start, end);
        double ux = unit[0];
        double uy = unit[1];
        double px = -uy;
        double py = ux;
        int length = 14;
        int width = 7;
        int tipX = end.x;
        int tipY = end.y;
        int backX = (int) Math.round(tipX - ux * length * 2.0);
        int backY = (int) Math.round(tipY - uy * length * 2.0);
        int middleX = (int) Math.round(tipX - ux * length);
        int middleY = (int) Math.round(tipY - uy * length);

        int leftX = (int) Math.round(middleX + px * width);
        int leftY = (int) Math.round(middleY + py * width);
        int rightX = (int) Math.round(middleX - px * width);
        int rightY = (int) Math.round(middleY - py * width);

        Path2D diamond = new Path2D.Double();
        diamond.moveTo(tipX, tipY);
        diamond.lineTo(leftX, leftY);
        diamond.lineTo(backX, backY);
        diamond.lineTo(rightX, rightY);
        diamond.closePath();
        g2d.drawLine(start.x, start.y, backX, backY);
        g2d.setColor(Color.WHITE);
        g2d.fill(diamond);
        g2d.setColor(Color.BLACK);
        g2d.draw(diamond);
    }

    private double[] calculateUnitDirection(Vector2D start, Vector2D end) {
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        double magnitude = Math.hypot(dx, dy);
        if (magnitude == 0) {
            return new double[]{1.0, 0.0};
        }
        return new double[]{dx / magnitude, dy / magnitude};
    }
}
