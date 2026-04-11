package model.shape;

import model.Vector2D;
import model.enums.PortType;

import java.util.List;

public class UMLRect extends UMLNode {
    public UMLRect(String name, Vector2D position, Vector2D size) {
        super(name, position, size);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return x >= position.x && x <= position.x + size.x && y >= position.y && y <= position.y + size.y;
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
}
