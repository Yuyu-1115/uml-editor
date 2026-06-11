package model;

import model.node.UMLNode;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SelectionModel {
    private final Set<UMLNode> selectedNodes = new LinkedHashSet<>();
    private UMLNode hoveredNode;

    public void setSelectedNode(UMLNode node) {
        selectedNodes.clear();
        if (node != null) {
            selectedNodes.add(node);
        }
    }

    public void clearSelection() {
        selectedNodes.clear();
    }

    public void setSelectedNodes(List<UMLNode> nodes) {
        selectedNodes.clear();
        if (nodes != null) {
            for (UMLNode node : nodes) {
                if (node != null) {
                    selectedNodes.add(node);
                }
            }
        }
    }

    public List<UMLNode> getSelectedNodes() {
        return List.copyOf(selectedNodes);
    }

    public boolean isSelected(UMLNode node) {
        return node != null && selectedNodes.contains(node);
    }

    public void setHoveredNode(UMLNode node) {
        this.hoveredNode = node;
    }

    public void clearHover() {
        this.hoveredNode = null;
    }

    public boolean isHovered(UMLNode node) {
        return node != null && node.equals(hoveredNode);
    }
}
