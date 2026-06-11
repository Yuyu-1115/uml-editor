package controller;

import model.UMLModel;
import model.enums.UserMode;
import view.UMLPanel;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Color;
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
                    UserMode previousMode = model.getUserMode();
                    CreationTool creationTool = new CreationTool(model, editorPanel, mode, ToolBarController.this::restoreAfterTemporaryCreate);
                    creationTool.start();
                    setButtonDefault(previousMode);
                    setButtonSelected(mode);
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
