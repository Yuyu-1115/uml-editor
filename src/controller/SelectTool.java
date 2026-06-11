package controller;

import model.DraftModel;
import model.SelectionModel;
import model.UMLModel;
import model.Vector2D;
import model.UMLPort;
import model.enums.PortType;
import model.node.UMLGroup;
import model.node.UMLNode;
import view.UMLPanel;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.UUID;

public class SelectTool implements CanvasTool {
    private static final int MIN_RESIZE_SIZE = 20;
    private static final int AREA_SELECT_DRAG_THRESHOLD = 3;

    private enum SelectDragAction {
        IDLE,
        MOVING,
        RESIZING,
        AREA_SELECT
    }

    private final UMLModel model;
    private final SelectionModel selectionModel;
    private final DraftModel draftModel;
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

    public SelectTool(UMLModel model, UMLPanel umlPanel) {
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
        UMLNode clickedNode = model.findTopNodeAt(point.x, point.y);
        if (clickedNode == null) {
            resetSelectDragState();
            selectDragAction = SelectDragAction.AREA_SELECT;
            areaSelectStartPoint = point;
            return;
        }

        boolean keepMultiSelection = selectionModel.isSelected(clickedNode) && selectionModel.getSelectedNodes().size() > 1;
        if (!keepMultiSelection) {
            selectionModel.setSelectedNode(clickedNode);
        }

        model.bringToFront(clickedNode);
        UMLPort pressedPort = model.findTopPortAt(point.x, point.y);
        if (pressedPort != null && pressedPort.ownerId().equals(clickedNode.getId())) {
            selectDragAction = SelectDragAction.RESIZING;
            activeNodeId = clickedNode.getId();
            activeResizePort = pressedPort.portType();
            PortType oppositePort = activeResizePort.getOpposite();
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

    @Override
    public void mouseDragged(MouseEvent e) {
        Point point = e.getPoint();
        if (selectDragAction == SelectDragAction.AREA_SELECT && areaSelectStartPoint != null) {
            int deltaX = Math.abs(point.x - areaSelectStartPoint.x);
            int deltaY = Math.abs(point.y - areaSelectStartPoint.y);
            if (!areaSelectActivated) {
                if (deltaX < AREA_SELECT_DRAG_THRESHOLD && deltaY < AREA_SELECT_DRAG_THRESHOLD) {
                    return;
                }
                areaSelectActivated = true;
                selectionModel.clearSelection();
                selectionModel.clearHover();
            }
            draftModel.setSelectionAreaDraft(
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

        selectionModel.setHoveredNode(activeNode);

        if (selectDragAction == SelectDragAction.MOVING && lastDragPoint != null) {
            int deltaX = point.x - lastDragPoint.x;
            int deltaY = point.y - lastDragPoint.y;
            if (selectionModel.isSelected(activeNode) && selectionModel.getSelectedNodes().size() > 1) {
                moveSelectedNodes(deltaX, deltaY);
            } else {
                activeNode.move(deltaX, deltaY);
            }
            lastDragPoint = point;
            umlPanel.repaint();
            return;
        }

        if (selectDragAction == SelectDragAction.RESIZING && activeResizePort != null) {
            activeNode.resizeByPort(
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
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        if (selectDragAction != SelectDragAction.IDLE) {
            boolean shouldRepaint = selectDragAction != SelectDragAction.AREA_SELECT;
            if (selectDragAction == SelectDragAction.AREA_SELECT && areaSelectStartPoint != null && areaSelectActivated) {
                Point point = e.getPoint();
                selectNodesFullyInsideArea(areaSelectStartPoint.x, areaSelectStartPoint.y, point.x, point.y);
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
        draftModel.clearSelectionAreaDraft();
    }

    private void moveSelectedNodes(int deltaX, int deltaY) {
        java.util.List<UMLNode> selectedTopLevelNodes = new java.util.ArrayList<>();
        for (UMLNode node : selectionModel.getSelectedNodes()) {
            if (node == null) {
                continue;
            }
            UMLGroup ancestor = node.getParent();
            boolean ancestorSelected = false;
            while (ancestor != null) {
                if (selectionModel.isSelected(ancestor)) {
                    ancestorSelected = true;
                    break;
                }
                ancestor = ancestor.getParent();
            }
            if (!ancestorSelected) {
                selectedTopLevelNodes.add(node);
            }
        }

        for (UMLNode node : selectedTopLevelNodes) {
            node.move(deltaX, deltaY);
        }
    }

    private void selectNodesFullyInsideArea(int x1, int y1, int x2, int y2) {
        int left = Math.min(x1, x2);
        int right = Math.max(x1, x2);
        int top = Math.min(y1, y2);
        int bottom = Math.max(y1, y2);
        java.util.List<UMLNode> selectedNodes = new java.util.ArrayList<>();
        for (UMLNode node : model.getNodesForRender()) {
            int nodeLeft = node.getPosition().x;
            int nodeTop = node.getPosition().y;
            int nodeRight = node.getPosition().x + node.getSize().x;
            int nodeBottom = node.getPosition().y + node.getSize().y;
            if (nodeLeft >= left && nodeTop >= top && nodeRight <= right && nodeBottom <= bottom) {
                selectedNodes.add(node);
            }
        }
        selectionModel.setSelectedNodes(selectedNodes);
    }
}
