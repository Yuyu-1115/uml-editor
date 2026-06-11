package model.link;

import model.enums.LinkType;
import model.enums.PortType;

import java.util.UUID;

public class CompositionLink extends UMLLink {
    public CompositionLink(UUID sourceNodeId, PortType sourcePort, UUID targetNodeId, PortType targetPort) {
        super(sourceNodeId, sourcePort, targetNodeId, targetPort);
    }

    @Override
    public LinkType type() {
        return LinkType.COMPOSITION;
    }

    @Override
    public void accept(UMLLinkVisitor visitor) {
        visitor.visit(this);
    }
}
