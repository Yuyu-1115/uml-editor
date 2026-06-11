package model.node;

import record.Vector2D;
import model.enums.PortType;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

public abstract class UMLNode {
    private UUID id;
    private String name;
    private int depth;
    private UMLGroup parent;
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

    public UMLGroup getParent() {
        return parent;
    }

    public void setParent(UMLGroup parent) {
        this.parent = parent;
    }

    public abstract boolean containsPoint(int x, int y);

    public abstract List<PortType> getSupportedPorts();

    public Vector2D getPortPosition(PortType portType) {
        int left = position.x();
        int right = position.x() + size.x();
        int centerX = position.x() + (size.x() / 2);
        int top = position.y();
        int bottom = position.y() + size.y();
        int centerY = position.y() + (size.y() / 2);

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

    public abstract void accept(UMLNodeVisitor visitor);

    public abstract void move(int deltaX, int deltaY);

    public void resizeByPort(
            PortType draggedPort,
            Vector2D oppositePortPoint,
            Vector2D dragPoint,
            Vector2D initialPosition,
            Vector2D initialSize,
            int minSize
    ) {
        if (draggedPort == null || oppositePortPoint == null || dragPoint == null || initialPosition == null || initialSize == null) {
            return;
        }

        int initialLeft = initialPosition.x();
        int initialTop = initialPosition.y();
        int initialRight = initialPosition.x() + initialSize.x();
        int initialBottom = initialPosition.y() + initialSize.y();

        int left = initialLeft;
        int right = initialRight;
        int top = initialTop;
        int bottom = initialBottom;

        boolean horizontalResize = draggedPort == PortType.LEFT || draggedPort == PortType.RIGHT || draggedPort == PortType.TOP_LEFT
                || draggedPort == PortType.TOP_RIGHT || draggedPort == PortType.BOTTOM_LEFT || draggedPort == PortType.BOTTOM_RIGHT;
        boolean verticalResize = draggedPort == PortType.TOP || draggedPort == PortType.BOTTOM || draggedPort == PortType.TOP_LEFT
                || draggedPort == PortType.TOP_RIGHT || draggedPort == PortType.BOTTOM_LEFT || draggedPort == PortType.BOTTOM_RIGHT;

        if (horizontalResize) {
            left = Math.min(dragPoint.x(), oppositePortPoint.x());
            right = Math.max(dragPoint.x(), oppositePortPoint.x());
        }
        if (verticalResize) {
            top = Math.min(dragPoint.y(), oppositePortPoint.y());
            bottom = Math.max(dragPoint.y(), oppositePortPoint.y());
        }

        int minLength = Math.max(40, minSize);

        if (right - left < minLength) {
            if (draggedPort == PortType.LEFT || draggedPort == PortType.TOP_LEFT || draggedPort == PortType.BOTTOM_LEFT) {
                left = right - minLength;
            } else if (draggedPort == PortType.RIGHT || draggedPort == PortType.TOP_RIGHT || draggedPort == PortType.BOTTOM_RIGHT) {
                right = left + minLength;
            } else {
                right = left + minLength;
            }
        }
        if (bottom - top < minLength) {
            if (draggedPort == PortType.TOP || draggedPort == PortType.TOP_LEFT || draggedPort == PortType.TOP_RIGHT) {
                top = bottom - minLength;
            } else if (draggedPort == PortType.BOTTOM || draggedPort == PortType.BOTTOM_LEFT || draggedPort == PortType.BOTTOM_RIGHT) {
                bottom = top + minLength;
            } else {
                bottom = top + minLength;
            }
        }

        setPosition(new Vector2D(left, top));
        setSize(new Vector2D(right - left, bottom - top));
    }
}
