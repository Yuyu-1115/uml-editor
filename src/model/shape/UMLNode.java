package model.shape;

import model.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UMLNode {
    private UUID id;
    private String name;
    private int depth;
    private UMLNode parent;
    private final List<UMLNode> children = new ArrayList<>();
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
}
