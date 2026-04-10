package model.shape;

import model.Vector2D;

import java.util.UUID;

public class UMLNode {
    private UUID id;
    private String name;
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
}
