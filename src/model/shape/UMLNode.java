package model.shape;

import model.Vector2D;
import model.enums.PortType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class UMLNode {
    private UUID id;
    private String name;
    private int depth;
    private UMLNode parent;
    private final List<UMLNode> children = new ArrayList<>();
    private Color labelColor = Color.WHITE;
    protected Vector2D position;
    protected Vector2D size;

    public UMLNode(String name, Vector2D position, Vector2D size) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.position = position;
        this.size = size;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Color labelColor) {
        if (labelColor == null) {
            return;
        }
        this.labelColor = labelColor;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getSize() {
        return size;
    }

    public void setSize(Vector2D size) {
        this.size = size;
    }

    public UMLNode(Vector2D size) {
        this.size = size;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public UMLNode getParent() {
        return parent;
    }

    public void setParent(UMLNode parent) {
        this.parent = parent;
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

    public abstract boolean containsPoint(int x, int y);

    public abstract List<PortType> getSupportedPorts();

    public Vector2D getPortPosition(PortType portType) {
        int left = position.x;
        int right = position.x + size.x;
        int centerX = position.x + (size.x / 2);
        int top = position.y;
        int bottom = position.y + size.y;
        int centerY = position.y + (size.y / 2);

        return switch (portType) {
            case TOP_LEFT -> new Vector2D(left, top);
            case TOP -> new Vector2D(centerX, top);
            case TOP_RIGHT -> new Vector2D(right, top);
            case RIGHT -> new Vector2D(right, centerY);
            case BOTTOM_RIGHT -> new Vector2D(right, bottom);
            case BOTTOM -> new Vector2D(centerX, bottom);
            case BOTTOM_LEFT -> new Vector2D(left, bottom);
            case LEFT -> new Vector2D(left, centerY);
        };
    }
}
