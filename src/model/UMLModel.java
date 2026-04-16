package model;

import model.enums.LinkType;
import model.enums.PortType;
import model.enums.UserMode;
import model.shape.UMLGroup;
import model.shape.UMLNode;
import model.shape.UMLOval;
import model.shape.UMLRect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UMLModel {
    private static final int PORT_HIT_RADIUS = 12;
    private static final int MIN_DEPTH = 0;
    private static final int MAX_DEPTH = 99;

    private UserMode userMode = UserMode.SELECT;
    private UserMode previousUserModeForTemporaryCreate;
    private UserMode temporaryCreateMode;
    private final HashMap<UUID, UMLNode> objectRegistry = new HashMap<>();
    private final List<UMLLink> links = new ArrayList<>();
    private final Set<UUID> selectedNodeIds = new LinkedHashSet<>();
    private UUID hoveredNodeId;
    private UMLPort linkStartPort;
    private Vector2D linkPreviewPoint;
    private Vector2D temporaryCreatePreviewPosition;
    private Vector2D temporaryCreatePreviewSize;
    private Vector2D selectionAreaStart;
    private Vector2D selectionAreaEnd;

    public void newShape(Vector2D position, Vector2D size) {
        UMLNode shape;
        switch (userMode) {
            case RECT:
                shape = new UMLRect("", position, size);
                break;
            case OVAL:
                shape = new UMLOval("", position, size);
                break;
            default:
                return;
        }
        objectRegistry.put(shape.getId(), shape);
        bringToFront(shape);
    }

    public UserMode getUserMode() {
        return userMode;
    }

    public void setUserMode(UserMode userMode) {
        this.userMode = userMode;
        clearHover();
        clearLinkDraft();
        clearTemporaryCreatePreview();
        clearSelectionAreaDraft();
    }

    public boolean startTemporaryCreateMode(UserMode mode) {
        if (isTemporaryCreateModeActive() || (mode != UserMode.RECT && mode != UserMode.OVAL)) {
            return false;
        }
        previousUserModeForTemporaryCreate = userMode;
        temporaryCreateMode = mode;
        userMode = mode;
        clearTemporaryCreatePreview();
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
        clearTemporaryCreatePreview();
        return restoredMode;
    }

    public List<UMLNode> getNodesForRender() {
        List<UMLNode> nodes = new ArrayList<>();
        for (UMLNode node : objectRegistry.values()) {
            if (node.getParent() == null) {
                nodes.add(node);
            }
        }
        nodes.sort(Comparator.comparingInt(UMLNode::getDepth).reversed());
        return nodes;
    }

    public List<UMLLink> getLinksForRender() {
        return new ArrayList<>(links);
    }

    public void bringToFront(UMLNode node) {
        if (node == null) {
            return;
        }
        List<UMLNode> depthScope = getDepthScope(node);
        if (depthScope.isEmpty()) {
            return;
        }
        depthScope.sort(Comparator.comparingInt(UMLNode::getDepth).thenComparing(n -> n.getId().toString()));
        depthScope.remove(node);
        depthScope.addFirst(node);
        reassignDepths(depthScope);
    }

    private List<UMLNode> getDepthScope(UMLNode node) {
        if (node.getParent() == null) {
            return getNodesForRender();
        }
        return new ArrayList<>(node.getParent().getChildren());
    }

    private void reassignDepths(List<UMLNode> orderedNodesFrontToBack) {
        int availableRange = (MAX_DEPTH - MIN_DEPTH) + 1;
        for (int index = 0; index < orderedNodesFrontToBack.size(); index++) {
            UMLNode current = orderedNodesFrontToBack.get(index);
            int boundedOffset = Math.min(index, availableRange - 1);
            current.setDepth(MIN_DEPTH + boundedOffset);
        }
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

    public UMLPort findTopPortAt(int x, int y) {
        List<UMLNode> nodes = getNodesForRender();
        for (int index = nodes.size() - 1; index >= 0; index--) {
            UMLNode node = nodes.get(index);
            for (PortType portType : node.getSupportedPorts()) {
                Vector2D portPosition = node.getPortPosition(portType);
                int dx = x - portPosition.x;
                int dy = y - portPosition.y;
                if (dx * dx + dy * dy <= PORT_HIT_RADIUS * PORT_HIT_RADIUS) {
                    return new UMLPort(node.getId(), portType);
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

    public void selectNodesFullyInsideArea(int x1, int y1, int x2, int y2) {
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        int top = Math.min(y1, y2);
        int bottom = Math.max(y1, y2);
        List<UMLNode> selectedNodes = new ArrayList<>();
        for (UMLNode node : getNodesForRender()) {
            if (node instanceof UMLGroup) {
                continue;
            }
            int nodeLeft = node.getPosition().x;
            int nodeTop = node.getPosition().y;
            int nodeRight = node.getPosition().x + node.getSize().x;
            int nodeBottom = node.getPosition().y + node.getSize().y;
            if (nodeLeft >= left && nodeTop >= top && nodeRight <= right && nodeBottom <= bottom) {
                selectedNodes.add(node);
            }
        }
        setSelectedNodes(selectedNodes);
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

    public void startLinkDraft(UMLPort startPort) {
        linkStartPort = startPort;
        linkPreviewPoint = getPortPosition(startPort);
    }

    public UMLPort getLinkStartPort() {
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

    public void createLink(UserMode mode, UMLPort start, UMLPort end) {
        if (start == null || end == null || start.ownerId().equals(end.ownerId())) {
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
        links.add(new UMLLink(linkType, start.ownerId(), start.portType(), end.ownerId(), end.portType()));
    }

    public Vector2D getPortPosition(UMLPort UMLPort) {
        if (UMLPort == null) {
            return null;
        }
        UMLNode node = getNodeById(UMLPort.ownerId());
        if (node == null) {
            return null;
        }
        return node.getPortPosition(UMLPort.portType());
    }

    public void moveNode(UMLNode node, int deltaX, int deltaY) {
        if (node == null || (deltaX == 0 && deltaY == 0)) {
            return;
        }
        moveNodeRecursive(node, deltaX, deltaY);
    }

    private void moveNodeRecursive(UMLNode node, int deltaX, int deltaY) {
        node.setPosition(new Vector2D(node.getPosition().x + deltaX, node.getPosition().y + deltaY));
        for (UMLNode child : node.getChildren()) {
            moveNodeRecursive(child, deltaX, deltaY);
        }
    }

    public void resizeNodeByPort(
            UMLNode node,
            PortType draggedPort,
            Vector2D oppositePortPoint,
            Vector2D dragPoint,
            Vector2D initialPosition,
            Vector2D initialSize,
            int minSize
    ) {
        if (node == null || draggedPort == null || oppositePortPoint == null || dragPoint == null || initialPosition == null || initialSize == null) {
            return;
        }

        int initialLeft = initialPosition.x;
        int initialTop = initialPosition.y;
        int initialRight = initialPosition.x + initialSize.x;
        int initialBottom = initialPosition.y + initialSize.y;

        int left = initialLeft;
        int right = initialRight;
        int top = initialTop;
        int bottom = initialBottom;

        boolean horizontalResize = draggedPort == PortType.LEFT || draggedPort == PortType.RIGHT || draggedPort == PortType.TOP_LEFT
                || draggedPort == PortType.TOP_RIGHT || draggedPort == PortType.BOTTOM_LEFT || draggedPort == PortType.BOTTOM_RIGHT;
        boolean verticalResize = draggedPort == PortType.TOP || draggedPort == PortType.BOTTOM || draggedPort == PortType.TOP_LEFT
                || draggedPort == PortType.TOP_RIGHT || draggedPort == PortType.BOTTOM_LEFT || draggedPort == PortType.BOTTOM_RIGHT;

        if (horizontalResize) {
            left = Math.min(dragPoint.x, oppositePortPoint.x);
            right = Math.max(dragPoint.x, oppositePortPoint.x);
        }
        if (verticalResize) {
            top = Math.min(dragPoint.y, oppositePortPoint.y);
            bottom = Math.max(dragPoint.y, oppositePortPoint.y);
        }

        int minLength = Math.max(40, minSize);

        if (right - left < minLength) {
            if (draggedPort == PortType.LEFT || draggedPort == PortType.TOP_LEFT || draggedPort == PortType.BOTTOM_LEFT) {
                left = right - minLength;
            } else if (draggedPort == PortType.RIGHT || draggedPort == PortType.TOP_RIGHT || draggedPort == PortType.BOTTOM_RIGHT) {
                right = left + minLength;
            } else {
                right = left + minLength;
            }
        }
        if (bottom - top < minLength) {
            if (draggedPort == PortType.TOP || draggedPort == PortType.TOP_LEFT || draggedPort == PortType.TOP_RIGHT) {
                top = bottom - minLength;
            } else if (draggedPort == PortType.BOTTOM || draggedPort == PortType.BOTTOM_LEFT || draggedPort == PortType.BOTTOM_RIGHT) {
                bottom = top + minLength;
            } else {
                bottom = top + minLength;
            }
        }

        node.setPosition(new Vector2D(left, top));
        node.setSize(new Vector2D(right - left, bottom - top));
    }

    public PortType getOppositePortType(PortType portType) {
        if (portType == null) {
            return null;
        }
        return switch (portType) {
            case TOP_LEFT -> PortType.BOTTOM_RIGHT;
            case TOP -> PortType.BOTTOM;
            case TOP_RIGHT -> PortType.BOTTOM_LEFT;
            case RIGHT -> PortType.LEFT;
            case BOTTOM_RIGHT -> PortType.TOP_LEFT;
            case BOTTOM -> PortType.TOP;
            case BOTTOM_LEFT -> PortType.TOP_RIGHT;
            case LEFT -> PortType.RIGHT;
        };
    }

    public boolean groupSelectedNodes() {
        List<UMLNode> selectedNodes = getSelectedNodes();
        List<UMLNode> topLevelSelectedNodes = new ArrayList<>();
        for (UMLNode selectedNode : selectedNodes) {
            if (selectedNode != null && selectedNode.getParent() == null) {
                topLevelSelectedNodes.add(selectedNode);
            }
        }
        if (topLevelSelectedNodes.size() < 2) {
            return false;
        }

        UMLGroup group = getUmlGroup(topLevelSelectedNodes);
        objectRegistry.put(group.getId(), group);
        for (UMLNode node : topLevelSelectedNodes) {
            group.addChild(node);
        }
        bringToFront(group);
        setSelectedNode(group);
        return true;
    }

    private static UMLGroup getUmlGroup(List<UMLNode> topLevelSelectedNodes) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (UMLNode node : topLevelSelectedNodes) {
            minX = Math.min(minX, node.getPosition().x);
            minY = Math.min(minY, node.getPosition().y);
            maxX = Math.max(maxX, node.getPosition().x + node.getSize().x);
            maxY = Math.max(maxY, node.getPosition().y + node.getSize().y);
        }

        UMLGroup group = new UMLGroup("", new Vector2D(minX, minY), new Vector2D(maxX - minX, maxY - minY));
        return group;
    }

    public boolean ungroupSelectedNode() {
        List<UMLNode> selectedNodes = getSelectedNodes();
        if (selectedNodes.size() != 1) {
            return false;
        }
        UMLNode selectedNode = selectedNodes.getFirst();
        if (!(selectedNode instanceof UMLGroup group)) {
            return false;
        }

        List<UMLNode> children = new ArrayList<>(group.getChildren());
        for (UMLNode child : children) {
            group.removeChild(child);
            bringToFront(child);
        }
        objectRegistry.remove(group.getId());
        setSelectedNodes(children);
        return true;
    }

    public void setTemporaryCreatePreview(Vector2D position, Vector2D size) {
        this.temporaryCreatePreviewPosition = position;
        this.temporaryCreatePreviewSize = size;
    }

    public void clearTemporaryCreatePreview() {
        temporaryCreatePreviewPosition = null;
        temporaryCreatePreviewSize = null;
    }

    public boolean hasTemporaryCreatePreview() {
        return temporaryCreatePreviewPosition != null && temporaryCreatePreviewSize != null;
    }

    public Vector2D getTemporaryCreatePreviewPosition() {
        return temporaryCreatePreviewPosition;
    }

    public Vector2D getTemporaryCreatePreviewSize() {
        return temporaryCreatePreviewSize;
    }

    public void setSelectionAreaDraft(Vector2D start, Vector2D end) {
        selectionAreaStart = start;
        selectionAreaEnd = end;
    }

    public void clearSelectionAreaDraft() {
        selectionAreaStart = null;
        selectionAreaEnd = null;
    }

    public boolean hasSelectionAreaDraft() {
        return selectionAreaStart != null && selectionAreaEnd != null;
    }

    public Vector2D getSelectionAreaStart() {
        return selectionAreaStart;
    }

    public Vector2D getSelectionAreaEnd() {
        return selectionAreaEnd;
    }
}
