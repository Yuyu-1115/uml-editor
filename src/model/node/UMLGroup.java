package model.node;

import record.Vector2D;
import model.enums.PortType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UMLGroup extends UMLNode {
    private final List<UMLNode> children = new ArrayList<>();

    public UMLGroup(String name, Vector2D position, Vector2D size) {
        super(name, position, size);
    }

    public List<UMLNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(UMLNode child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(UMLNode child) {
        children.remove(child);
        if (child.getParent() == this) {
            child.setParent(null);
        }
    }

    @Override
    public boolean containsPoint(int x, int y) {
        return x >= position.x() && x <= position.x() + size.x() && y >= position.y() && y <= position.y() + size.y();
    }

    @Override
    public List<PortType> getSupportedPorts() {
        return List.of();
    }

    @Override
    public void accept(UMLNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void move(int deltaX, int deltaY) {
        this.position = new Vector2D(this.position.x() + deltaX, this.position.y() + deltaY);
        for (UMLNode child : children) {
            child.move(deltaX, deltaY);
        }
    }
}
