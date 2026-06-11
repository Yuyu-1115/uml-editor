package model.link;

import model.enums.LinkType;
import model.enums.PortType;

import java.util.UUID;

public class GeneralizationLink extends UMLLink {
    public GeneralizationLink(UUID sourceNodeId, PortType sourcePort, UUID targetNodeId, PortType targetPort) {
        super(sourceNodeId, sourcePort, targetNodeId, targetPort);
    }

    @Override
    public LinkType type() {
        return LinkType.GENERALIZATION;
    }

    @Override
    public void accept(UMLLinkVisitor visitor) {
        visitor.visit(this);
    }
}
