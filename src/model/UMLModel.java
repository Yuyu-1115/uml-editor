package model;

import model.enums.LinkType;
import model.enums.PortType;
import model.enums.UserMode;
import model.shape.UMLNode;
import model.shape.UMLOval;
import model.shape.UMLRect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UMLModel {
    private static final int PORT_HIT_RADIUS = 8;

    private UserMode userMode = UserMode.SELECT;
    private UserMode previousUserModeForTemporaryCreate;
    private UserMode temporaryCreateMode;
    private int nextTopDepth = 0;
    private final HashMap<UUID, UMLNode> objectRegistry = new HashMap<>();
    private final List<UMLLink> links = new ArrayList<>();
    private final Set<UUID> selectedNodeIds = new LinkedHashSet<>();
    private UUID hoveredNodeId;
    private PortHit linkStartPort;
    private Vector2D linkPreviewPoint;

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
        shape.setDepth(nextTopDepth);
        nextTopDepth--;
        objectRegistry.put(shape.getId(), shape);
    }

    public UserMode getUserMode() {
        return userMode;
    }

    public void setUserMode(UserMode userMode) {
        this.userMode = userMode;
        clearHover();
        clearLinkDraft();
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

    public List<UMLNode> getNodesForRender() {
        List<UMLNode> nodes = new ArrayList<>(objectRegistry.values());
        nodes.sort(Comparator.comparingInt(UMLNode::getDepth).reversed());
        return nodes;
    }

    public List<UMLLink> getLinksForRender() {
        return new ArrayList<>(links);
    }

    public void bringToFront(UMLNode node) {
        node.setDepth(nextTopDepth);
        nextTopDepth--;
    }

    public UMLNode findTopNodeAt(int x, int y) {
        List<UMLNode> nodes = getNodesForRender();
        for (int index = nodes.size() - 1; index >= 0; index--) {
            UMLNode node = nodes.get(index);
            if (node.containsPoint(x, y)) {
                return node;
            }
        }
        return null;
    }

    public PortHit findTopPortAt(int x, int y) {
        List<UMLNode> nodes = getNodesForRender();
        for (int index = nodes.size() - 1; index >= 0; index--) {
            UMLNode node = nodes.get(index);
            for (PortType portType : node.getSupportedPorts()) {
                Vector2D portPosition = node.getPortPosition(portType);
                int dx = x - portPosition.x;
                int dy = y - portPosition.y;
                if (dx * dx + dy * dy <= PORT_HIT_RADIUS * PORT_HIT_RADIUS) {
                    return new PortHit(node.getId(), portType);
                }
            }
        }
        return null;
    }

    public void setSelectedNode(UMLNode node) {
        selectedNodeIds.clear();
        if (node != null) {
            selectedNodeIds.add(node.getId());
        }
    }

    public void addSelectedNode(UMLNode node) {
        if (node == null) {
            return;
        }
        selectedNodeIds.add(node.getId());
    }

    public void removeSelectedNode(UMLNode node) {
        if (node == null) {
            return;
        }
        selectedNodeIds.remove(node.getId());
    }

    public void toggleSelectedNode(UMLNode node) {
        if (node == null) {
            return;
        }
        UUID nodeId = node.getId();
        if (selectedNodeIds.contains(nodeId)) {
            selectedNodeIds.remove(nodeId);
            return;
        }
        selectedNodeIds.add(nodeId);
    }

    public void clearSelection() {
        selectedNodeIds.clear();
    }

    public void setSelectedNodes(List<UMLNode> nodes) {
        selectedNodeIds.clear();
        if (nodes == null) {
            return;
        }
        for (UMLNode node : nodes) {
            if (node != null) {
                selectedNodeIds.add(node.getId());
            }
        }
    }

    public Set<UUID> getSelectedNodeIds() {
        return Collections.unmodifiableSet(selectedNodeIds);
    }

    public List<UMLNode> getSelectedNodes() {
        List<UMLNode> selectedNodes = new ArrayList<>();
        for (UUID selectedNodeId : selectedNodeIds) {
            UMLNode selectedNode = objectRegistry.get(selectedNodeId);
            if (selectedNode != null) {
                selectedNodes.add(selectedNode);
            }
        }
        return selectedNodes;
    }

    public boolean isSelected(UMLNode node) {
        return node != null && selectedNodeIds.contains(node.getId());
    }

    public void setHoveredNode(UMLNode node) {
        hoveredNodeId = node == null ? null : node.getId();
    }

    public void clearHover() {
        hoveredNodeId = null;
    }

    public boolean isHovered(UMLNode node) {
        return node != null && node.getId().equals(hoveredNodeId);
    }

    public UMLNode getNodeById(UUID id) {
        return objectRegistry.get(id);
    }

    public void startLinkDraft(PortHit startPort) {
        linkStartPort = startPort;
        linkPreviewPoint = getPortPosition(startPort);
    }

    public PortHit getLinkStartPort() {
        return linkStartPort;
    }

    public void updateLinkDraftPreview(Vector2D point) {
        linkPreviewPoint = point;
    }

    public Vector2D getLinkPreviewPoint() {
        return linkPreviewPoint;
    }

    public void clearLinkDraft() {
        linkStartPort = null;
        linkPreviewPoint = null;
    }

    public boolean hasLinkDraft() {
        return linkStartPort != null;
    }

    public void createLink(UserMode mode, PortHit start, PortHit end) {
        if (start == null || end == null || start.getOwnerId().equals(end.getOwnerId())) {
            return;
        }
        LinkType linkType = switch (mode) {
            case ASSOCIATION -> LinkType.ASSOCIATION;
            case GENERALIZATION -> LinkType.GENERALIZATION;
            case COMPOSITION -> LinkType.COMPOSITION;
            default -> null;
        };
        if (linkType == null) {
            return;
        }
        links.add(new UMLLink(linkType, start.getOwnerId(), start.getPortType(), end.getOwnerId(), end.getPortType()));
    }

    public Vector2D getPortPosition(PortHit portHit) {
        if (portHit == null) {
            return null;
        }
        UMLNode node = getNodeById(portHit.getOwnerId());
        if (node == null) {
            return null;
        }
        return node.getPortPosition(portHit.getPortType());
    }
}
