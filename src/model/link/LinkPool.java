package model.link;

import java.util.ArrayList;
import java.util.List;

public class LinkPool {
    private final List<UMLLink> links = new ArrayList<>();

    public void addLink(UMLLink link) {
        if (link != null) {
            links.add(link);
        }
    }

    public List<UMLLink> getAllLinks() {
        return new ArrayList<>(links);
    }
}
