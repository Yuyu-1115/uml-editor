package model;

import model.enums.PortType;
import model.enums.UserMode;
import model.link.UMLLink;
import model.link.UMLLinkFactory;
import model.node.UMLGroup;
import model.node.UMLNode;
import model.node.UMLOval;
import model.node.UMLRect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    private final SelectionModel selectionModel = new SelectionModel();
    private final DraftModel draftModel = new DraftModel();

    public void addNode(UMLNode shape) {
        if (shape != null) {
            objectRegistry.put(shape.getId(), shape);
            bringToFront(shape);
        }
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
        selectionModel.setSelectedNode(node);
    }

    public void clearSelection() {
        selectionModel.clearSelection();
    }

    public void setSelectedNodes(List<UMLNode> nodes) {
        selectionModel.setSelectedNodes(nodes);
    }

    public List<UMLNode> getSelectedNodes() {
        return selectionModel.getSelectedNodes(objectRegistry);
    }

    public boolean isSelected(UMLNode node) {
        return selectionModel.isSelected(node);
    }



    public void setHoveredNode(UMLNode node) {
        selectionModel.setHoveredNode(node);
    }

    public void clearHover() {
        selectionModel.clearHover();
    }

    public boolean isHovered(UMLNode node) {
        return selectionModel.isHovered(node);
    }

    public UMLNode getNodeById(UUID id) {
        return objectRegistry.get(id);
    }

    public void startLinkDraft(UMLPort startPort) {
        draftModel.startLinkDraft(startPort, getPortPosition(startPort));
    }

    public UMLPort getLinkStartPort() {
        return draftModel.getLinkStartPort();
    }

    public void updateLinkDraftPreview(Vector2D point) {
        draftModel.updateLinkDraftPreview(point);
    }

    public Vector2D getLinkPreviewPoint() {
        return draftModel.getLinkPreviewPoint();
    }

    public void clearLinkDraft() {
        draftModel.clearLinkDraft();
    }

    public boolean hasLinkDraft() {
        return draftModel.hasLinkDraft();
    }

    public void createLink(UserMode mode, UMLPort start, UMLPort end) {
        UMLLink link = UMLLinkFactory.createLink(mode, start, end);
        if (link != null) {
            links.add(link);
        }
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

        return new UMLGroup("", new Vector2D(minX, minY), new Vector2D(maxX - minX, maxY - minY));
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
        draftModel.setTemporaryCreatePreview(position, size);
    }

    public void clearTemporaryCreatePreview() {
        draftModel.clearTemporaryCreatePreview();
    }

    public boolean hasTemporaryCreatePreview() {
        return draftModel.hasTemporaryCreatePreview();
    }

    public Vector2D getTemporaryCreatePreviewPosition() {
        return draftModel.getTemporaryCreatePreviewPosition();
    }

    public Vector2D getTemporaryCreatePreviewSize() {
        return draftModel.getTemporaryCreatePreviewSize();
    }

    public void setSelectionAreaDraft(Vector2D start, Vector2D end) {
        draftModel.setSelectionAreaDraft(start, end);
    }

    public void clearSelectionAreaDraft() {
        draftModel.clearSelectionAreaDraft();
    }

    public boolean hasSelectionAreaDraft() {
        return draftModel.hasSelectionAreaDraft();
    }

    public Vector2D getSelectionAreaStart() {
        return draftModel.getSelectionAreaStart();
    }

    public Vector2D getSelectionAreaEnd() {
        return draftModel.getSelectionAreaEnd();
    }
}
