package view;

import model.UMLLink;
import model.UMLModel;
import model.shape.UMLNode;
import model.Vector2D;
import model.enums.LinkType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

public class UMLLinkRenderer {
    private final Graphics2D g2d;
    private final UMLModel model;

    public UMLLinkRenderer(Graphics2D g2d, UMLModel model) {
        this.g2d = g2d;
        this.model = model;
    }

    public void draw(UMLLink link) {
        UMLNode sourceNode = model.getNodeById(link.sourceNodeId());
        UMLNode targetNode = model.getNodeById(link.targetNodeId());
        if (sourceNode == null || targetNode == null) {
            return;
        }

        Vector2D start = sourceNode.getPortPosition(link.sourcePort());
        Vector2D end = targetNode.getPortPosition(link.targetPort());
        g2d.setColor(Color.BLACK);

        if (link.type() == LinkType.ASSOCIATION) {
            drawAssociationArrow(start, end);
        } else if (link.type() == LinkType.GENERALIZATION) {
            drawTriangleArrow(start, end);
        } else if (link.type() == LinkType.COMPOSITION) {
            drawDiamondArrow(start, end);
        }
    }

    private void drawAssociationArrow(Vector2D start, Vector2D end) {
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

    private void drawTriangleArrow(Vector2D start, Vector2D end) {
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

    private void drawDiamondArrow(Vector2D start, Vector2D end) {
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
