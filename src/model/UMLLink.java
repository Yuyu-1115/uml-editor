package model;

import model.enums.LinkType;
import model.enums.PortType;

import java.util.UUID;

public class UMLLink {
    private final LinkType type;
    private final UUID sourceNodeId;
    private final PortType sourcePort;
    private final UUID targetNodeId;
    private final PortType targetPort;

    public UMLLink(LinkType type, UUID sourceNodeId, PortType sourcePort, UUID targetNodeId, PortType targetPort) {
        this.type = type;
        this.sourceNodeId = sourceNodeId;
        this.sourcePort = sourcePort;
        this.targetNodeId = targetNodeId;
        this.targetPort = targetPort;
    }

    public LinkType getType() {
        return type;
    }

    public UUID getSourceNodeId() {
        return sourceNodeId;
    }

    public PortType getSourcePort() {
        return sourcePort;
    }

    public UUID getTargetNodeId() {
        return targetNodeId;
    }

    public PortType getTargetPort() {
        return targetPort;
    }
}
