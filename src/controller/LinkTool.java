package controller;

import model.DraftModel;
import model.SelectionModel;
import model.UMLModel;
import model.Vector2D;
import model.UMLPort;
import model.node.UMLNode;
import view.UMLPanel;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class LinkTool implements CanvasTool {
    private final UMLModel model;
    private final SelectionModel selectionModel;
    private final DraftModel draftModel;
    private final UMLPanel umlPanel;

    public LinkTool(UMLModel model, UMLPanel umlPanel) {
        this.model = model;
        this.selectionModel = model.getSelectionModel();
        this.draftModel = model.getDraftModel();
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
            UMLNode startNode = model.getNodeById(startPort.ownerId());
            if (startNode != null) {
                draftModel.startLinkDraft(startPort, startNode.getPortPosition(startPort.portType()));
                selectionModel.setHoveredNode(startNode);
                draftModel.updateLinkDraftPreview(new Vector2D(point.x, point.y));
                umlPanel.repaint();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draftModel.hasLinkDraft()) {
            Point point = e.getPoint();
            UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
            selectionModel.setHoveredNode(hoveredNode);
            draftModel.updateLinkDraftPreview(new Vector2D(point.x, point.y));
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (draftModel.hasLinkDraft()) {
            Point point = e.getPoint();
            UMLPort startPort = draftModel.getLinkStartPort();
            UMLPort endPort = model.findTopPortAt(point.x, point.y);
            model.createLink(model.getUserMode(), startPort, endPort);
            draftModel.clearLinkDraft();
            selectionModel.clearHover();
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (model.isTemporaryCreateModeActive()) {
            selectionModel.clearHover();
            umlPanel.repaint();
            return;
        }

        Point point = e.getPoint();
        UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
        selectionModel.setHoveredNode(hoveredNode);
        umlPanel.repaint();
    }
}
