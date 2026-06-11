package view;

import model.UMLLink;
import model.UMLModel;
import model.shape.UMLNode;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class UMLPanel extends JPanel {
    private final UMLModel umlModel;

    public UMLPanel(UMLModel umlModel) {
        this.umlModel = umlModel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Draw connections / links
        UMLLinkRenderer linkRenderer = new UMLLinkRenderer(g2d, umlModel);
        for (UMLLink link : umlModel.getLinksForRender()) {
            linkRenderer.draw(link);
        }

        // 2. Draw shapes using Visitor pattern
        UMLNodeRenderer nodeRenderer = new UMLNodeRenderer(g2d, umlModel);
        for (UMLNode node : umlModel.getNodesForRender()) {
            node.accept(nodeRenderer);
        }

        // 3. Draw temporary drafts (selection boxes, creation previews, link drafts)
        UMLDraftRenderer draftRenderer = new UMLDraftRenderer(g2d, umlModel);
        draftRenderer.drawDrafts();
    }
}
