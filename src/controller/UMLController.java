package controller;

import model.UMLModel;
import model.Vector2D;
import model.PortHit;
import model.enums.UserMode;
import model.shape.UMLNode;
import view.UMLPanel;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UMLController extends MouseAdapter {
    private final UMLModel model;
    private final UMLPanel umlPanel;
    private final ToolBarController toolBarController;

    public UMLController(UMLModel model, UMLPanel umlPanel, ToolBarController toolBarController) {
        this.model = model;
        this.umlPanel = umlPanel;
        this.toolBarController = toolBarController;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() != MouseEvent.BUTTON1 || model.isTemporaryCreateModeActive()) {
            return;
        }

        Point point = e.getPoint();
        if (model.getUserMode().isLinkMode()) {
            PortHit startPort = model.findTopPortAt(point.x, point.y);
            if (startPort != null) {
                model.startLinkDraft(startPort);
                model.setHoveredNode(model.getNodeById(startPort.getOwnerId()));
                model.updateLinkDraftPreview(new Vector2D(point.x, point.y));
                umlPanel.repaint();
            }
            return;
        }

        if (model.getUserMode() == UserMode.SELECT) {
            UMLNode clickedNode = model.findTopNodeAt(point.x, point.y);
            if (clickedNode == null) {
                if (!e.isShiftDown()) {
                    model.clearSelection();
                }
                umlPanel.repaint();
                return;
            }

            if (e.isShiftDown()) {
                model.toggleSelectedNode(clickedNode);
            } else {
                model.setSelectedNode(clickedNode);
            }
            if (clickedNode != null) {
                model.bringToFront(clickedNode);
            }
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (!model.getUserMode().isLinkMode() || !model.hasLinkDraft()) {
            return;
        }
        Point point = e.getPoint();
        UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
        model.setHoveredNode(hoveredNode);
        model.updateLinkDraftPreview(new Vector2D(point.x, point.y));
        umlPanel.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() != MouseEvent.BUTTON1 || !model.getUserMode().isLinkMode() || !model.hasLinkDraft()) {
            return;
        }
        Point point = e.getPoint();
        PortHit startPort = model.getLinkStartPort();
        PortHit endPort = model.findTopPortAt(point.x, point.y);
        model.createLink(model.getUserMode(), startPort, endPort);
        model.clearLinkDraft();
        model.clearHover();
        umlPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        if (model.isTemporaryCreateModeActive()) {
            model.clearHover();
            umlPanel.repaint();
            return;
        }

        if (model.getUserMode() != UserMode.SELECT && !model.getUserMode().isLinkMode()) {
            model.clearHover();
            umlPanel.repaint();
            return;
        }

        Point point = e.getPoint();
        UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
        model.setHoveredNode(hoveredNode);
        umlPanel.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        toolBarController.onEditorMouseEntered();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        toolBarController.onEditorMouseExited();
    }
}
