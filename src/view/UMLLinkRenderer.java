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

    private static record ArrowCoordinates(
        int tipX, int tipY,
        int baseX, int baseY,
        int leftX, int leftY,
        int rightX, int rightY,
        int backX, int backY
    ) {}

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
        ArrowCoordinates coords = calculateArrowCoordinates(start, end, 14, 7);
        g2d.drawLine(start.x, start.y, coords.tipX, coords.tipY);
        g2d.drawLine(coords.tipX, coords.tipY, coords.leftX, coords.leftY);
        g2d.drawLine(coords.tipX, coords.tipY, coords.rightX, coords.rightY);
    }

    private void drawTriangleArrow(Vector2D start, Vector2D end) {
        ArrowCoordinates coords = calculateArrowCoordinates(start, end, 18, 9);
        Path2D triangle = new Path2D.Double();
        triangle.moveTo(coords.tipX, coords.tipY);
        triangle.lineTo(coords.leftX, coords.leftY);
        triangle.lineTo(coords.rightX, coords.rightY);
        triangle.closePath();

        g2d.drawLine(start.x, start.y, coords.baseX, coords.baseY);
        g2d.setColor(Color.WHITE);
        g2d.fill(triangle);
        g2d.setColor(Color.BLACK);
        g2d.draw(triangle);
    }

    private void drawDiamondArrow(Vector2D start, Vector2D end) {
        ArrowCoordinates coords = calculateArrowCoordinates(start, end, 14, 7);
        Path2D diamond = new Path2D.Double();
        diamond.moveTo(coords.tipX, coords.tipY);
        diamond.lineTo(coords.leftX, coords.leftY);
        diamond.lineTo(coords.backX, coords.backY);
        diamond.lineTo(coords.rightX, coords.rightY);
        diamond.closePath();

        g2d.drawLine(start.x, start.y, coords.backX, coords.backY);
        g2d.setColor(Color.WHITE);
        g2d.fill(diamond);
        g2d.setColor(Color.BLACK);
        g2d.draw(diamond);
    }

    private ArrowCoordinates calculateArrowCoordinates(Vector2D start, Vector2D end, int length, int width) {
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        double magnitude = Math.hypot(dx, dy);

        double ux = 1.0;
        double uy = 0.0;
        if (magnitude != 0) {
            ux = dx / magnitude;
            uy = dy / magnitude;
        }

        double px = -uy;
        double py = ux;

        int tipX = end.x;
        int tipY = end.y;

        int baseX = (int) Math.round(tipX - ux * length);
        int baseY = (int) Math.round(tipY - uy * length);

        int leftX = (int) Math.round(baseX + px * width);
        int leftY = (int) Math.round(baseY + py * width);

        int rightX = (int) Math.round(baseX - px * width);
        int rightY = (int) Math.round(baseY - py * width);

        int backX = (int) Math.round(tipX - ux * length * 2.0);
        int backY = (int) Math.round(tipY - uy * length * 2.0);

        return new ArrowCoordinates(tipX, tipY, baseX, baseY, leftX, leftY, rightX, rightY, backX, backY);
    }
}
