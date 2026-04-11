package model;

import model.enums.UserMode;
import model.shape.UMLNode;
import model.shape.UMLOval;
import model.shape.UMLRect;

import java.util.HashMap;
import java.util.UUID;

public class UMLModel {
    private UserMode userMode = UserMode.SELECT;
    private UserMode previousUserModeForTemporaryCreate;
    private UserMode temporaryCreateMode;
    private final HashMap<UUID, UMLNode> objectRegistry = new HashMap<>();

    public void newShape(Vector2D position, Vector2D size) {
        UMLNode shape;
        switch (userMode) {
            case RECT:
                shape = new UMLRect("shape", position, size);
                break;
            case OVAL:
                shape = new UMLOval("shape", position, size);
                break;
            default:
                return;
        }
        objectRegistry.put(shape.getId(), shape);
    }

    public UserMode getUserMode() {
        return userMode;
    }

    public void setUserMode(UserMode userMode) {
        this.userMode = userMode;
    }

    public boolean startTemporaryCreateMode(UserMode mode) {
        if (isTemporaryCreateModeActive() || (mode != UserMode.RECT && mode != UserMode.OVAL)) {
            return false;
        }
        previousUserModeForTemporaryCreate = userMode;
        temporaryCreateMode = mode;
        userMode = mode;
        return true;
    }

    public boolean isTemporaryCreateModeActive() {
        return temporaryCreateMode != null;
    }

    public UserMode getTemporaryCreateMode() {
        return temporaryCreateMode;
    }

    public UserMode getPreviousUserModeForTemporaryCreate() {
        return previousUserModeForTemporaryCreate;
    }

    public UserMode finishTemporaryCreateMode() {
        if (!isTemporaryCreateModeActive()) {
            return userMode;
        }
        UserMode restoredMode = previousUserModeForTemporaryCreate;
        temporaryCreateMode = null;
        previousUserModeForTemporaryCreate = null;
        userMode = restoredMode;
        return restoredMode;
    }

    public HashMap<UUID, UMLNode> getObjectRegistry() {
        return objectRegistry;
    }
}
