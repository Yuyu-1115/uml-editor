package model.shape;

import model.Vector2D;
import model.enums.PortType;

import java.util.List;

public class UMLGroup extends UMLNode {
    public UMLGroup(String name, Vector2D position, Vector2D size) {
        super(name, position, size);
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return x >= position.x && x <= position.x + size.x && y >= position.y && y <= position.y + size.y;
    }

    @Override
    public List<PortType> getSupportedPorts() {
        return List.of();
    }
}
