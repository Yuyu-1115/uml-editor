package model;

import model.enums.UMLShape;
import model.enums.UserMode;
import model.shape.UMLNode;
import model.shape.UMLOval;
import model.shape.UMLRect;

import java.util.HashMap;
import java.util.UUID;

public class UMLModel {
    private UserMode userMode;
    private final HashMap<UUID, UMLNode> objectRegistry = new HashMap<>();

    public void newShape(Vector2D position, Vector2D size) {
        UMLNode shape;
        if (userMode.equals(UserMode.RECT)) {
            shape = new UMLRect("shape", position, size);
        }
        else {
            shape = new UMLOval("shape", position, size);
        }
        objectRegistry.put(shape.getId(), shape);
    }

    public HashMap<UUID, UMLNode> getObjectRegistry() {
        return objectRegistry;
    }
}
