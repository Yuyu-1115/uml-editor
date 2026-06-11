package model;

import model.node.UMLNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SelectionModel {
    private final Set<UUID> selectedNodeIds = new LinkedHashSet<>();
    private UUID hoveredNodeId;

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

    public List<UMLNode> getSelectedNodes(HashMap<UUID, UMLNode> objectRegistry) {
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
}
