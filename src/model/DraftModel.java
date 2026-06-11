package model;

import record.UMLPort;
import record.Vector2D;

public class DraftModel {
    private UMLPort linkStartPort;
    private Vector2D linkPreviewPoint;
    private Vector2D temporaryCreatePreviewPosition;
    private Vector2D temporaryCreatePreviewSize;
    private Vector2D selectionAreaStart;
    private Vector2D selectionAreaEnd;

    public void startLinkDraft(UMLPort startPort, Vector2D startPoint) {
        linkStartPort = startPort;
        linkPreviewPoint = startPoint;
    }

    public UMLPort getLinkStartPort() {
        return linkStartPort;
    }

    public void updateLinkDraftPreview(Vector2D point) {
        linkPreviewPoint = point;
    }

    public Vector2D getLinkPreviewPoint() {
        return linkPreviewPoint;
    }

    public void clearLinkDraft() {
        linkStartPort = null;
        linkPreviewPoint = null;
    }

    public boolean hasLinkDraft() {
        return linkStartPort != null;
    }

    public void setTemporaryCreatePreview(Vector2D position, Vector2D size) {
        this.temporaryCreatePreviewPosition = position;
        this.temporaryCreatePreviewSize = size;
    }

    public void clearTemporaryCreatePreview() {
        temporaryCreatePreviewPosition = null;
        temporaryCreatePreviewSize = null;
    }

    public boolean hasTemporaryCreatePreview() {
        return temporaryCreatePreviewPosition != null && temporaryCreatePreviewSize != null;
    }

    public Vector2D getTemporaryCreatePreviewPosition() {
        return temporaryCreatePreviewPosition;
    }

    public Vector2D getTemporaryCreatePreviewSize() {
        return temporaryCreatePreviewSize;
    }

    public void setSelectionAreaDraft(Vector2D start, Vector2D end) {
        selectionAreaStart = start;
        selectionAreaEnd = end;
    }

    public void clearSelectionAreaDraft() {
        selectionAreaStart = null;
        selectionAreaEnd = null;
    }

    public boolean hasSelectionAreaDraft() {
        return selectionAreaStart != null && selectionAreaEnd != null;
    }

    public Vector2D getSelectionAreaStart() {
        return selectionAreaStart;
    }

    public Vector2D getSelectionAreaEnd() {
        return selectionAreaEnd;
    }
}
