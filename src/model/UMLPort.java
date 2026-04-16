package model;

import model.enums.PortType;

import java.util.UUID;

public record UMLPort(UUID ownerId, PortType portType) {
}
