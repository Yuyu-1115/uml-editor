package controller;

import model.DraftModel;
import model.SelectionModel;
import model.UMLModel;
import record.Vector2D;
import record.UMLPort;
import model.node.UMLNode;

import java.awt.Point;
import java.awt.event.MouseEvent;

public class LinkTool implements CanvasTool {
    private final UMLModel model;
    private final SelectionModel selectionModel;
    private final DraftModel draftModel;

    public LinkTool(UMLModel model) {
        this.model = model;
        this.selectionModel = model.getSelectionModel();
        this.draftModel = model.getDraftModel();
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
                model.fireModelChanged();
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
            model.fireModelChanged();
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
            model.fireModelChanged();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (model.isTemporaryCreateModeActive()) {
            selectionModel.clearHover();
            model.fireModelChanged();
            return;
        }

        Point point = e.getPoint();
        UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
        selectionModel.setHoveredNode(hoveredNode);
        model.fireModelChanged();
    }
}
