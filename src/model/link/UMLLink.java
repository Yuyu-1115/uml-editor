package model.link;

import model.enums.LinkType;
import model.enums.PortType;

import java.util.UUID;

public abstract class UMLLink {
    private final UUID sourceNodeId;
    private final PortType sourcePort;
    private final UUID targetNodeId;
    private final PortType targetPort;

    public UMLLink(UUID sourceNodeId, PortType sourcePort, UUID targetNodeId, PortType targetPort) {
        this.sourceNodeId = sourceNodeId;
        this.sourcePort = sourcePort;
        this.targetNodeId = targetNodeId;
        this.targetPort = targetPort;
    }

    public UUID sourceNodeId() {
        return sourceNodeId;
    }

    public PortType sourcePort() {
        return sourcePort;
    }

    public UUID targetNodeId() {
        return targetNodeId;
    }

    public PortType targetPort() {
        return targetPort;
    }

    public abstract LinkType type();

    public abstract void accept(UMLLinkVisitor visitor);
}
