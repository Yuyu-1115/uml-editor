package view;

import controller.ToolBarController;
import controller.UMLController;
import model.UMLModel;
import model.enums.UserMode;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

public class UMLUiBuilder {
    private final UMLModel umlModel;
    private final ToolBarController toolBarController;

    public UMLUiBuilder(UMLModel umlModel) {
        this.umlModel = umlModel;
        this.toolBarController = new ToolBarController(umlModel);
    }

    public JPanel createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        toolPanel.setPreferredSize(new Dimension(180, 0));
        toolPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (UserMode mode : UserMode.values()) {
            JButton button = new JButton(mode.getText());
            button.setAlignmentX(JButton.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            this.toolBarController.bindToolButton(button, mode);
            toolPanel.add(button);
            toolPanel.add(javax.swing.Box.createVerticalStrut(8));
        }

        return toolPanel;
    }

    public JPanel createCanvasPanel() {
        UMLPanel canvasPanel = new UMLPanel(umlModel);
        toolBarController.setEditorPanel(canvasPanel);
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        UMLController umlController = new UMLController(umlModel, canvasPanel, toolBarController);
        canvasPanel.addMouseListener(umlController);
        canvasPanel.addMouseMotionListener(umlController);
        return canvasPanel;
    }

    public JMenuBar createTopMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.add(new JMenu("Edit"));
        return menuBar;
    }
}
