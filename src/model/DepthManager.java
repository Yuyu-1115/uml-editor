package model;

import model.node.UMLNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DepthManager {
    private static final int MIN_DEPTH = 0;
    private static final int MAX_DEPTH = 99;

    public void bringToFront(UMLNode node, NodeRegistry registry) {
        if (node == null) {
            return;
        }
        List<UMLNode> depthScope = getDepthScope(node, registry);
        if (depthScope.isEmpty()) {
            return;
        }
        depthScope.sort(Comparator.comparingInt(UMLNode::getDepth).thenComparing(n -> n.getId().toString()));
        depthScope.remove(node);
        depthScope.addFirst(node);
        reassignDepths(depthScope);
    }

    private List<UMLNode> getDepthScope(UMLNode node, NodeRegistry registry) {
        if (node.getParent() == null) {
            return registry.getTopLevelNodes();
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
}
