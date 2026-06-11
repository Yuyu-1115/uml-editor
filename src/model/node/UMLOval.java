package model.node;

import record.Vector2D;
import model.enums.PortType;

import java.util.List;

public class UMLOval extends UMLNode {

    public UMLOval(String name, Vector2D position, Vector2D size) {
        super(name, position, size);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        double radiusX = getSize().x() / 2.0;
        double radiusY = getSize().y() / 2.0;
        if (radiusX <= 0 || radiusY <= 0) {
            return false;
        }
        double centerX = getPosition().x() + radiusX;
        double centerY = getPosition().y() + radiusY;
        double normalizedX = (x - centerX) / radiusX;
        double normalizedY = (y - centerY) / radiusY;
        return normalizedX * normalizedX + normalizedY * normalizedY <= 1.0;
    }

    @Override
    public List<PortType> getSupportedPorts() {
        return List.of(
                PortType.TOP,
                PortType.RIGHT,
                PortType.BOTTOM,
                PortType.LEFT
        );
    }

    @Override
    public void accept(UMLNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void move(int deltaX, int deltaY) {
        setPosition(new Vector2D(getPosition().x() + deltaX, getPosition().y() + deltaY));
    }
}
