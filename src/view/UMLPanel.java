package view;

import model.link.UMLLink;
import model.UMLModel;
import model.node.UMLNode;
import view.render.UMLDraftRenderer;
import view.render.UMLLinkRenderer;
import view.render.UMLNodeRenderer;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class UMLPanel extends JPanel {
    private final UMLModel umlModel;
    private final UMLLinkRenderer linkRenderer;
    private final UMLNodeRenderer nodeRenderer;
    private final UMLDraftRenderer draftRenderer;

    public UMLPanel(UMLModel umlModel) {
        this.umlModel = umlModel;
        this.linkRenderer = new UMLLinkRenderer(umlModel);
        this.nodeRenderer = new UMLNodeRenderer(umlModel);
        this.draftRenderer = new UMLDraftRenderer(umlModel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Draw connections / links
        linkRenderer.setGraphics(g2d);
        for (UMLLink link : umlModel.getLinksForRender()) {
            linkRenderer.draw(link);
        }

        // 2. Draw shapes using Visitor pattern
        nodeRenderer.setGraphics(g2d);
        for (UMLNode node : umlModel.getNodesForRender()) {
            node.accept(nodeRenderer);
        }

        // 3. Draw temporary drafts (selection boxes, creation previews, link drafts)
        draftRenderer.setGraphics(g2d);
        draftRenderer.drawDrafts();
    }
}
