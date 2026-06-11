package model.node;

import record.Vector2D;
import model.enums.PortType;

import java.util.List;

public class UMLRect extends UMLNode {
    public UMLRect(String name, Vector2D position, Vector2D size) {
        super(name, position, size);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return x >= getPosition().x() && x <= getPosition().x() + getSize().x()
                && y >= getPosition().y() && y <= getPosition().y() + getSize().y();
    }

    @Override
    public List<PortType> getSupportedPorts() {
        return List.of(
                PortType.TOP_LEFT,
                PortType.TOP,
                PortType.TOP_RIGHT,
                PortType.RIGHT,
                PortType.BOTTOM_RIGHT,
                PortType.BOTTOM,
                PortType.BOTTOM_LEFT,
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
