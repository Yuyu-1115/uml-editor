package model;

import model.enums.LinkType;
import model.enums.PortType;

import java.util.UUID;

public record UMLLink(LinkType type, UUID sourceNodeId, PortType sourcePort, UUID targetNodeId, PortType targetPort) {
}
