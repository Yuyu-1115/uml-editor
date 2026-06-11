package view;

import model.DraftModel;
import model.UMLModel;
import model.UMLPort;
import model.Vector2D;
import model.enums.UserMode;
import model.node.UMLNode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class UMLDraftRenderer {
    private Graphics2D g2d;
    private final UMLModel model;
    private final DraftModel draftModel;

    public UMLDraftRenderer(UMLModel model) {
        this.model = model;
        this.draftModel = model.getDraftModel();
    }

    public void setGraphics(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public void drawDrafts() {
        if (draftModel.hasTemporaryCreatePreview()) {
            drawTemporaryCreatePreview();
        }
        if (draftModel.hasSelectionAreaDraft()) {
            drawSelectionAreaDraft();
        }
        if (draftModel.hasLinkDraft()) {
            drawLinkDraft();
        }
    }

    private void drawTemporaryCreatePreview() {
        Vector2D position = draftModel.getTemporaryCreatePreviewPosition();
        Vector2D size = draftModel.getTemporaryCreatePreviewSize();
        if (position == null || size == null) {
            return;
        }
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6f, 4f}, 0f));
        g2d.setColor(new Color(80, 80, 80, 180));
        if (model.getTemporaryCreateMode() == UserMode.OVAL) {
            g2d.drawOval(position.x, position.y, size.x, size.y);
        } else {
            g2d.drawRect(position.x, position.y, size.x, size.y);
        }
        g2d.setStroke(oldStroke);
    }

    private void drawSelectionAreaDraft() {
        Vector2D start = draftModel.getSelectionAreaStart();
        Vector2D end = draftModel.getSelectionAreaEnd();
        if (start == null || end == null) {
            return;
        }
        int left = Math.min(start.x, end.x);
        int top = Math.min(start.y, end.y);
        int width = Math.abs(end.x - start.x);
        int height = Math.abs(end.y - start.y);
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6f, 4f}, 0f));
        g2d.setColor(new Color(60, 110, 200, 180));
        g2d.drawRect(left, top, width, height);
        g2d.setStroke(oldStroke);
    }

    private void drawLinkDraft() {
        UMLPort startPort = draftModel.getLinkStartPort();
        Vector2D startPosition = null;
        if (startPort != null) {
            UMLNode startNode = model.getNodeById(startPort.ownerId());
            if (startNode != null) {
                startPosition = startNode.getPortPosition(startPort.portType());
            }
        }
        Vector2D endPosition = draftModel.getLinkPreviewPoint();
        if (startPosition != null && endPosition != null) {
            g2d.setColor(Color.GRAY);
            g2d.drawLine(startPosition.x, startPosition.y, endPosition.x, endPosition.y);
        }
    }
}
