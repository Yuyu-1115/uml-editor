package model.link;

import model.UMLPort;
import model.enums.UserMode;

public class UMLLinkFactory {
    public static UMLLink createLink(UserMode mode, UMLPort start, UMLPort end) {
        if (start == null || end == null || start.ownerId().equals(end.ownerId())) {
            return null;
        }
        return switch (mode) {
            case ASSOCIATION -> new AssociationLink(start.ownerId(), start.portType(), end.ownerId(), end.portType());
            case GENERALIZATION -> new GeneralizationLink(start.ownerId(), start.portType(), end.ownerId(), end.portType());
            case COMPOSITION -> new CompositionLink(start.ownerId(), start.portType(), end.ownerId(), end.portType());
            default -> null;
        };
    }
}
