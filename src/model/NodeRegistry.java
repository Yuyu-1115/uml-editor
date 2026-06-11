package model;

import model.node.UMLNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NodeRegistry {
    private final HashMap<UUID, UMLNode> objectRegistry = new HashMap<>();

    public void addNode(UMLNode node) {
        if (node != null) {
            objectRegistry.put(node.getId(), node);
        }
    }

    public UMLNode getNodeById(UUID id) {
        return objectRegistry.get(id);
    }

    public void removeNode(UUID id) {
        objectRegistry.remove(id);
    }

    public List<UMLNode> getAllNodes() {
        return new ArrayList<>(objectRegistry.values());
    }

    public List<UMLNode> getTopLevelNodes() {
        List<UMLNode> topLevel = new ArrayList<>();
        for (UMLNode node : objectRegistry.values()) {
            if (node.getParent() == null) {
                topLevel.add(node);
            }
        }
        return topLevel;
    }

    public boolean containsNode(UUID id) {
        return objectRegistry.containsKey(id);
    }
}
