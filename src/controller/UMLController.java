package controller;

import model.UMLModel;
import model.Vector2D;
import model.UMLPort;
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
    private static final int AREA_SELECT_DRAG_THRESHOLD = 3;

    private enum SelectDragAction {
        IDLE,
        MOVING,
        RESIZING,
        AREA_SELECT
    }

    private final UMLModel model;
    private final UMLPanel umlPanel;
    private SelectDragAction selectDragAction = SelectDragAction.IDLE;
    private UUID activeNodeId;
    private Point lastDragPoint;
    private PortType activeResizePort;
    private Vector2D resizeOppositePoint;
    private Vector2D resizeInitialPosition;
    private Vector2D resizeInitialSize;
    private Point areaSelectStartPoint;
    private boolean areaSelectActivated;

    public UMLController(UMLModel model, UMLPanel umlPanel) {
        this.model = model;
        this.umlPanel = umlPanel;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (e.getButton() != MouseEvent.BUTTON1 || model.isTemporaryCreateModeActive()) {
            return;
        }

        Point point = e.getPoint();
        if (model.getUserMode().isLinkMode()) {
            UMLPort startPort = model.findTopPortAt(point.x, point.y);
            if (startPort != null) {
                model.startLinkDraft(startPort);
                model.setHoveredNode(model.getNodeById(startPort.ownerId()));
                model.updateLinkDraftPreview(new Vector2D(point.x, point.y));
                umlPanel.repaint();
            }
            return;
        }

        if (model.getUserMode() == UserMode.SELECT) {
            UMLNode clickedNode = model.findTopNodeAt(point.x, point.y);
            if (clickedNode == null) {
                resetSelectDragState();
                selectDragAction = SelectDragAction.AREA_SELECT;
                areaSelectStartPoint = point;
                return;
            }

            boolean keepMultiSelection = model.isSelected(clickedNode) && model.getSelectedNodes().size() > 1;
            if (!keepMultiSelection) {
                model.setSelectedNode(clickedNode);
            }

            model.bringToFront(clickedNode);
            UMLPort pressedPort = model.findTopPortAt(point.x, point.y);
            if (pressedPort != null && pressedPort.ownerId().equals(clickedNode.getId())) {
                selectDragAction = SelectDragAction.RESIZING;
                activeNodeId = clickedNode.getId();
                activeResizePort = pressedPort.portType();
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

        Point point = e.getPoint();
        if (selectDragAction == SelectDragAction.AREA_SELECT && areaSelectStartPoint != null) {
            int deltaX = Math.abs(point.x - areaSelectStartPoint.x);
            int deltaY = Math.abs(point.y - areaSelectStartPoint.y);
            if (!areaSelectActivated) {
                if (deltaX < AREA_SELECT_DRAG_THRESHOLD && deltaY < AREA_SELECT_DRAG_THRESHOLD) {
                    return;
                }
                areaSelectActivated = true;
                model.clearSelection();
                model.clearHover();
            }
            model.setSelectionAreaDraft(
                    new Vector2D(areaSelectStartPoint.x, areaSelectStartPoint.y),
                    new Vector2D(point.x, point.y)
            );
            umlPanel.repaint();
            return;
        }

        UMLNode activeNode = model.getNodeById(activeNodeId);
        if (activeNode == null) {
            resetSelectDragState();
            return;
        }

        model.setHoveredNode(activeNode);

        if (selectDragAction == SelectDragAction.MOVING && lastDragPoint != null) {
            int deltaX = point.x - lastDragPoint.x;
            int deltaY = point.y - lastDragPoint.y;
            if (model.isSelected(activeNode) && model.getSelectedNodes().size() > 1) {
                model.moveSelectedNodes(deltaX, deltaY);
            } else {
                model.moveNode(activeNode, deltaX, deltaY);
            }
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
            UMLPort startPort = model.getLinkStartPort();
            UMLPort endPort = model.findTopPortAt(point.x, point.y);
            model.createLink(model.getUserMode(), startPort, endPort);
            model.clearLinkDraft();
            model.clearHover();
            umlPanel.repaint();
            return;
        }

        if (model.getUserMode() == UserMode.SELECT && selectDragAction != SelectDragAction.IDLE) {
            boolean shouldRepaint = selectDragAction != SelectDragAction.AREA_SELECT;
            if (selectDragAction == SelectDragAction.AREA_SELECT && areaSelectStartPoint != null && areaSelectActivated) {
                Point point = e.getPoint();
                model.selectNodesFullyInsideArea(areaSelectStartPoint.x, areaSelectStartPoint.y, point.x, point.y);
                shouldRepaint = true;
            }
            resetSelectDragState();
            if (shouldRepaint) {
                umlPanel.repaint();
            }
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

    private void resetSelectDragState() {
        selectDragAction = SelectDragAction.IDLE;
        activeNodeId = null;
        lastDragPoint = null;
        activeResizePort = null;
        resizeOppositePoint = null;
        resizeInitialPosition = null;
        resizeInitialSize = null;
        areaSelectStartPoint = null;
        areaSelectActivated = false;
        model.clearSelectionAreaDraft();
    }
}
