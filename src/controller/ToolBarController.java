package controller;

import model.UMLModel;
import model.Vector2D;
import model.enums.UserMode;
import view.UMLPanel;

import javax.swing.BorderFactory;
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
    private final UMLModel model;
    private final Map<UserMode, JButton> buttons = new EnumMap<>(UserMode.class);
    private final Map<UserMode, Color> defaultTextColors = new EnumMap<>(UserMode.class);
    private final Map<UserMode, Color> defaultBackgroundColors = new EnumMap<>(UserMode.class);
    private final Map<UserMode, Border> defaultBorders = new EnumMap<>(UserMode.class);
    private UMLPanel editorPanel;
    private boolean pointerInsideEditorArea;
    private AWTEventListener temporaryReleaseListener;

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
        temporaryReleaseListener = event -> {
            if (!(event instanceof MouseEvent mouseEvent)) {
                return;
            }
            if (mouseEvent.getID() != MouseEvent.MOUSE_RELEASED || !SwingUtilities.isLeftMouseButton(mouseEvent)) {
                return;
            }
            if (!model.isTemporaryCreateModeActive()) {
                removeTemporaryReleaseListener();
                return;
            }

            if (editorPanel != null && pointerInsideEditorArea) {
                Point releasePoint = new Point(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
                SwingUtilities.convertPointFromScreen(releasePoint, editorPanel);
                if (editorPanel.contains(releasePoint)) {
                    model.newShape(new Vector2D(releasePoint.x - 50, releasePoint.y - 50), new Vector2D(100, 100));
                    editorPanel.repaint();
                }
            }

            restoreAfterTemporaryCreate();
            removeTemporaryReleaseListener();
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(temporaryReleaseListener, AWTEvent.MOUSE_EVENT_MASK);
    }

    private void removeTemporaryReleaseListener() {
        if (temporaryReleaseListener == null) {
            return;
        }
        Toolkit.getDefaultToolkit().removeAWTEventListener(temporaryReleaseListener);
        temporaryReleaseListener = null;
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
        if (button == null) {
            return;
        }
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
    }
}
