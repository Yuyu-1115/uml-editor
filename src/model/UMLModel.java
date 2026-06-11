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

    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    public DraftModel getDraftModel() {
        return draftModel;
    }

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
        selectionModel.clearHover();
        draftModel.clearLinkDraft();
        draftModel.clearTemporaryCreatePreview();
        draftModel.clearSelectionAreaDraft();
    }

    public boolean startTemporaryCreateMode(UserMode mode) {
        if (isTemporaryCreateModeActive() || (mode != UserMode.RECT && mode != UserMode.OVAL)) {
            return false;
        }
        previousUserModeForTemporaryCreate = userMode;
        temporaryCreateMode = mode;
        userMode = mode;
        draftModel.clearTemporaryCreatePreview();
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
        draftModel.clearTemporaryCreatePreview();
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

    public UMLNode getNodeById(UUID id) {
        return objectRegistry.get(id);
    }

    public void createLink(UserMode mode, UMLPort start, UMLPort end) {
        UMLLink link = UMLLinkFactory.createLink(mode, start, end);
        if (link != null) {
            links.add(link);
        }
    }



    public boolean groupSelectedNodes() {
        List<UMLNode> selectedNodes = selectionModel.getSelectedNodes();
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
        selectionModel.setSelectedNode(group);
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
        List<UMLNode> selectedNodes = selectionModel.getSelectedNodes();
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
        selectionModel.setSelectedNodes(children);
        return true;
    }


}
