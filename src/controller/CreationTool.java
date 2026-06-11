package controller;

import model.UMLModel;
import model.Vector2D;
import model.enums.UserMode;
import model.node.UMLNode;
import model.node.UMLNodeFactory;
import view.UMLPanel;

import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

public class CreationTool implements AWTEventListener {
    private static final int CREATE_PREVIEW_WIDTH = 100;
    private static final int CREATE_PREVIEW_HEIGHT = 100;

    private final UMLModel model;
    private final UMLPanel panel;
    private final UserMode mode;
    private final Runnable onFinished;

    public CreationTool(UMLModel model, UMLPanel panel, UserMode mode, Runnable onFinished) {
        this.model = model;
        this.panel = panel;
        this.mode = mode;
        this.onFinished = onFinished;
    }

    public void start() {
        if (model.startTemporaryCreateMode(mode)) {
            Toolkit.getDefaultToolkit().addAWTEventListener(
                    this,
                    AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
            );
        }
    }

    public void stop() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        if (!(event instanceof MouseEvent mouseEvent)) {
            return;
        }
        if (!model.isTemporaryCreateModeActive()) {
            stop();
            return;
        }

        if (mouseEvent.getID() == MouseEvent.MOUSE_MOVED || mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
            updatePreview(mouseEvent);
            return;
        }

        if (mouseEvent.getID() != MouseEvent.MOUSE_RELEASED || !SwingUtilities.isLeftMouseButton(mouseEvent)) {
            return;
        }

        if (panel != null) {
            Point releasePoint = new Point(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
            SwingUtilities.convertPointFromScreen(releasePoint, panel);
            if (panel.contains(releasePoint)) {
                Vector2D position = new Vector2D(releasePoint.x - (CREATE_PREVIEW_WIDTH / 2), releasePoint.y - (CREATE_PREVIEW_HEIGHT / 2));
                Vector2D size = new Vector2D(CREATE_PREVIEW_WIDTH, CREATE_PREVIEW_HEIGHT);
                UMLNode shape = UMLNodeFactory.createNode(mode, position, size);
                if (shape != null) {
                    model.addNode(shape);
                }
                panel.repaint();
            }
        }

        stop();
        if (onFinished != null) {
            onFinished.run();
        }
    }

    private void updatePreview(MouseEvent mouseEvent) {
        if (panel == null) {
            return;
        }
        Point previewPoint = new Point(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
        SwingUtilities.convertPointFromScreen(previewPoint, panel);
        if (!panel.contains(previewPoint)) {
            model.clearTemporaryCreatePreview();
            panel.repaint();
            return;
        }
        model.setTemporaryCreatePreview(
                new Vector2D(previewPoint.x - (CREATE_PREVIEW_WIDTH / 2), previewPoint.y - (CREATE_PREVIEW_HEIGHT / 2)),
                new Vector2D(CREATE_PREVIEW_WIDTH, CREATE_PREVIEW_HEIGHT)
        );
        panel.repaint();
    }
}
