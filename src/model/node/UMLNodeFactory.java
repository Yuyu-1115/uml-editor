package model.node;

import model.enums.UserMode;
import record.Vector2D;

public class UMLNodeFactory {
    public static UMLNode createNode(UserMode mode, Vector2D position, Vector2D size) {
        return switch (mode) {
            case RECT -> new UMLRect("", position, size);
            case OVAL -> new UMLOval("", position, size);
            default -> null;
        };
    }
}
