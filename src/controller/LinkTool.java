package controller;

import model.UMLModel;
import model.Vector2D;
import model.UMLPort;
import model.node.UMLNode;
import view.UMLPanel;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class LinkTool implements CanvasTool {
    private final UMLModel model;
    private final UMLPanel umlPanel;

    public LinkTool(UMLModel model, UMLPanel umlPanel) {
        this.model = model;
        this.umlPanel = umlPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1 || model.isTemporaryCreateModeActive()) {
            return;
        }

        Point point = e.getPoint();
        UMLPort startPort = model.findTopPortAt(point.x, point.y);
        if (startPort != null) {
            model.startLinkDraft(startPort);
            model.setHoveredNode(model.getNodeById(startPort.ownerId()));
            model.updateLinkDraftPreview(new Vector2D(point.x, point.y));
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (model.hasLinkDraft()) {
            Point point = e.getPoint();
            UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
            model.setHoveredNode(hoveredNode);
            model.updateLinkDraftPreview(new Vector2D(point.x, point.y));
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (model.hasLinkDraft()) {
            Point point = e.getPoint();
            UMLPort startPort = model.getLinkStartPort();
            UMLPort endPort = model.findTopPortAt(point.x, point.y);
            model.createLink(model.getUserMode(), startPort, endPort);
            model.clearLinkDraft();
            model.clearHover();
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (model.isTemporaryCreateModeActive()) {
            model.clearHover();
            umlPanel.repaint();
            return;
        }

        Point point = e.getPoint();
        UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
        model.setHoveredNode(hoveredNode);
        umlPanel.repaint();
    }
}
