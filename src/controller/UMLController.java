package controller;

import model.UMLModel;
import model.Vector2D;
import model.PortHit;
import model.enums.PortType;
import model.enums.UserMode;
import model.shape.UMLNode;
import view.UMLPanel;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class UMLController extends MouseAdapter {
    private static final int MIN_RESIZE_SIZE = 20;

    private enum SelectDragAction {
        IDLE,
        MOVING,
        RESIZING
    }

    private final UMLModel model;
    private final UMLPanel umlPanel;
    private final ToolBarController toolBarController;
    private SelectDragAction selectDragAction = SelectDragAction.IDLE;
    private UUID activeNodeId;
    private Point lastDragPoint;
    private PortType activeResizePort;
    private Vector2D resizeOppositePoint;
    private Vector2D resizeInitialPosition;
    private Vector2D resizeInitialSize;

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
                resetSelectDragState();
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

            model.bringToFront(clickedNode);
            PortHit pressedPort = model.findTopPortAt(point.x, point.y);
            if (pressedPort != null && pressedPort.getOwnerId().equals(clickedNode.getId())) {
                selectDragAction = SelectDragAction.RESIZING;
                activeNodeId = clickedNode.getId();
                activeResizePort = pressedPort.getPortType();
                PortType oppositePort = model.getOppositePortType(activeResizePort);
                resizeOppositePoint = clickedNode.getPortPosition(oppositePort);
                resizeInitialPosition = new Vector2D(clickedNode.getPosition().x, clickedNode.getPosition().y);
                resizeInitialSize = new Vector2D(clickedNode.getSize().x, clickedNode.getSize().y);
            } else {
                selectDragAction = SelectDragAction.MOVING;
                activeNodeId = clickedNode.getId();
                lastDragPoint = point;
            }
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if (model.getUserMode().isLinkMode() && model.hasLinkDraft()) {
            Point point = e.getPoint();
            UMLNode hoveredNode = model.findTopNodeAt(point.x, point.y);
            model.setHoveredNode(hoveredNode);
            model.updateLinkDraftPreview(new Vector2D(point.x, point.y));
            umlPanel.repaint();
            return;
        }

        if (model.getUserMode() != UserMode.SELECT) {
            return;
        }

        UMLNode activeNode = model.getNodeById(activeNodeId);
        if (activeNode == null) {
            resetSelectDragState();
            return;
        }

        Point point = e.getPoint();
        model.setHoveredNode(activeNode);

        if (selectDragAction == SelectDragAction.MOVING && lastDragPoint != null) {
            int deltaX = point.x - lastDragPoint.x;
            int deltaY = point.y - lastDragPoint.y;
            model.moveNode(activeNode, deltaX, deltaY);
            lastDragPoint = point;
            umlPanel.repaint();
            return;
        }

        if (selectDragAction == SelectDragAction.RESIZING && activeResizePort != null) {
            model.resizeNodeByPort(
                    activeNode,
                    activeResizePort,
                    resizeOppositePoint,
                    new Vector2D(point.x, point.y),
                    resizeInitialPosition,
                    resizeInitialSize,
                    MIN_RESIZE_SIZE
            );
            umlPanel.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (model.getUserMode().isLinkMode() && model.hasLinkDraft()) {
            Point point = e.getPoint();
            PortHit startPort = model.getLinkStartPort();
            PortHit endPort = model.findTopPortAt(point.x, point.y);
            model.createLink(model.getUserMode(), startPort, endPort);
            model.clearLinkDraft();
            model.clearHover();
            umlPanel.repaint();
            return;
        }

        if (model.getUserMode() == UserMode.SELECT && selectDragAction != SelectDragAction.IDLE) {
            resetSelectDragState();
            umlPanel.repaint();
        }
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

    private void resetSelectDragState() {
        selectDragAction = SelectDragAction.IDLE;
        activeNodeId = null;
        lastDragPoint = null;
        activeResizePort = null;
        resizeOppositePoint = null;
        resizeInitialPosition = null;
        resizeInitialSize = null;
    }
}
