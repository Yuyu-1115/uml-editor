package model.enums;

public enum PortType {
    TOP_LEFT,
    TOP,
    TOP_RIGHT,
    RIGHT,
    BOTTOM_RIGHT,
    BOTTOM,
    BOTTOM_LEFT,
    LEFT;

    public PortType getOpposite() {
        return switch (this) {
            case TOP_LEFT -> BOTTOM_RIGHT;
            case TOP -> BOTTOM;
            case TOP_RIGHT -> BOTTOM_LEFT;
            case RIGHT -> LEFT;
            case BOTTOM_RIGHT -> TOP_LEFT;
            case BOTTOM -> TOP;
            case BOTTOM_LEFT -> TOP_RIGHT;
            case LEFT -> RIGHT;
        };
    }
}
