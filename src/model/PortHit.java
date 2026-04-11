package model;

import model.enums.PortType;

import java.util.UUID;

public class PortHit {
    private final UUID ownerId;
    private final PortType portType;

    public PortHit(UUID ownerId, PortType portType) {
        this.ownerId = ownerId;
        this.portType = portType;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public PortType getPortType() {
        return portType;
    }
}
