package controller;

import model.UMLModel;
import model.Vector2D;
import model.enums.UserMode;
import view.UMLPanel;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.border.Border;

public class ToolBarController {
    private static final int CREATE_PREVIEW_WIDTH = 100;
    private static final int CREATE_PREVIEW_HEIGHT = 100;

    private final UMLModel model;
    private final Map<UserMode, JButton> buttons = new EnumMap<>(UserMode.class);
    private final Map<UserMode, Color> defaultTextColors = new EnumMap<>(UserMode.class);
    private final Map<UserMode, Color> defaultBackgroundColors = new EnumMap<>(UserMode.class);
    private final Map<UserMode, Border> defaultBorders = new EnumMap<>(UserMode.class);
    private UMLPanel editorPanel;
    private boolean pointerInsideEditorArea;
    private AWTEventListener temporaryCreateListener;

    public ToolBarController(UMLModel model) {
        this.model = model;
    }

    public void bindToolButton(JButton button, UserMode mode) {
        buttons.put(mode, button);
        defaultTextColors.put(mode, button.getForeground());
        defaultBackgroundColors.put(mode, button.getBackground());
        defaultBorders.put(mode, button.getBorder());
        if (mode == model.getUserMode()) {
            setButtonSelected(mode);
        }

        if (mode == UserMode.RECT || mode == UserMode.OVAL) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!SwingUtilities.isLeftMouseButton(e)) {
                        return;
                    }
                    if (model.startTemporaryCreateMode(mode)) {
                        UserMode previousMode = model.getPreviousUserModeForTemporaryCreate();
                        setButtonDefault(previousMode);
                        setButtonSelected(mode);
                        installTemporaryReleaseListener();
                    }
                }
            });
            return;
        }

        button.addActionListener(e -> {
            if (model.isTemporaryCreateModeActive()) {
                return;
            }
            UserMode previousMode = model.getUserMode();
            model.setUserMode(mode);
            setButtonDefault(previousMode);
            setButtonSelected(mode);
        });
    }

    public void restoreAfterTemporaryCreate() {
        UserMode temporaryMode = model.getTemporaryCreateMode();
        if (temporaryMode == null) {
            return;
        }
        setButtonDefault(temporaryMode);
        setButtonSelected(model.finishTemporaryCreateMode());
    }

    public void setEditorPanel(UMLPanel panel) {
        this.editorPanel = panel;
        this.pointerInsideEditorArea = false;
    }

    public void onEditorMouseEntered() {
        pointerInsideEditorArea = true;
    }

    public void onEditorMouseExited() {
        pointerInsideEditorArea = false;
    }

    private void installTemporaryReleaseListener() {
        removeTemporaryReleaseListener();
        temporaryCreateListener = event -> {
            if (!(event instanceof MouseEvent mouseEvent)) {
                return;
            }
            if (!model.isTemporaryCreateModeActive()) {
                removeTemporaryReleaseListener();
                return;
            }

            if (mouseEvent.getID() == MouseEvent.MOUSE_MOVED || mouseEvent.getID() == MouseEvent.MOUSE_DRAGGED) {
                updateTemporaryCreatePreview(mouseEvent);
                return;
            }

            if (mouseEvent.getID() != MouseEvent.MOUSE_RELEASED || !SwingUtilities.isLeftMouseButton(mouseEvent)) {
                return;
            }

            if (editorPanel != null) {
                Point releasePoint = new Point(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
                SwingUtilities.convertPointFromScreen(releasePoint, editorPanel);
                if (editorPanel.contains(releasePoint)) {
                    model.newShape(
                            new Vector2D(releasePoint.x - (CREATE_PREVIEW_WIDTH / 2), releasePoint.y - (CREATE_PREVIEW_HEIGHT / 2)),
                            new Vector2D(CREATE_PREVIEW_WIDTH, CREATE_PREVIEW_HEIGHT)
                    );
                    editorPanel.repaint();
                }
            }

            restoreAfterTemporaryCreate();
            removeTemporaryReleaseListener();
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(
                temporaryCreateListener,
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK
        );
    }

    private void removeTemporaryReleaseListener() {
        if (temporaryCreateListener == null) {
            return;
        }
        Toolkit.getDefaultToolkit().removeAWTEventListener(temporaryCreateListener);
        temporaryCreateListener = null;
    }

    private void updateTemporaryCreatePreview(MouseEvent mouseEvent) {
        if (editorPanel == null) {
            return;
        }
        Point previewPoint = new Point(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
        SwingUtilities.convertPointFromScreen(previewPoint, editorPanel);
        if (!editorPanel.contains(previewPoint)) {
            model.clearTemporaryCreatePreview();
            editorPanel.repaint();
            return;
        }
        model.setTemporaryCreatePreview(
                new Vector2D(previewPoint.x - (CREATE_PREVIEW_WIDTH / 2), previewPoint.y - (CREATE_PREVIEW_HEIGHT / 2)),
                new Vector2D(CREATE_PREVIEW_WIDTH, CREATE_PREVIEW_HEIGHT)
        );
        editorPanel.repaint();
    }

    private void setButtonDefault(UserMode mode) {
        JButton button = buttons.get(mode);
        Color defaultTextColor = defaultTextColors.get(mode);
        Color defaultBackgroundColor = defaultBackgroundColors.get(mode);
        Border defaultBorder = defaultBorders.get(mode);
        if (button == null || defaultTextColor == null || defaultBackgroundColor == null || defaultBorder == null) {
            return;
        }
        button.setForeground(defaultTextColor);
        button.setBackground(defaultBackgroundColor);
        button.setBorder(defaultBorder);
    }

    private void setButtonSelected(UserMode mode) {
        JButton button = buttons.get(mode);
        Border defaultBorder = defaultBorders.get(mode);
        if (button == null) {
            return;
        }
        button.setForeground(Color.BLACK);
        button.setBackground(Color.DARK_GRAY);
        if (defaultBorder != null) {
            button.setBorder(defaultBorder);
        }
    }
}
