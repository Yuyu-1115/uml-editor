package view;

import controller.ToolBarController;
import controller.UMLController;
import model.UMLModel;
import model.enums.UserMode;
import model.shape.UMLGroup;
import model.shape.UMLNode;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UMLUiBuilder {
    private static final Map<String, Color> COLOR_NAME_MAP = createColorNameMap();
    private final UMLModel umlModel;
    private final ToolBarController toolBarController;
    private UMLPanel canvasPanel;

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
        canvasPanel = new UMLPanel(umlModel);
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
        JMenu editMenu = new JMenu("Edit");
        JMenuItem groupItem = new JMenuItem("Group");
        groupItem.addActionListener(e -> {
            if (umlModel.groupSelectedNodes() && canvasPanel != null) {
                canvasPanel.repaint();
            }
        });
        JMenuItem labelItem = new JMenuItem("Label");
        labelItem.addActionListener(e -> showLabelStyleDialog());
        JMenuItem ungroupItem = new JMenuItem("Ungroup");
        ungroupItem.addActionListener(e -> {
            if (umlModel.ungroupSelectedNode() && canvasPanel != null) {
                canvasPanel.repaint();
            }
        });
        editMenu.add(labelItem);
        editMenu.add(groupItem);
        editMenu.add(ungroupItem);
        menuBar.add(editMenu);
        return menuBar;
    }

    private void showLabelStyleDialog() {
        List<UMLNode> selectedNodes = umlModel.getSelectedNodes();
        if (selectedNodes.size() != 1) {
            return;
        }
        UMLNode selectedNode = selectedNodes.get(0);
        if (selectedNode instanceof UMLGroup) {
            return;
        }

        JTextField nameField = new JTextField(selectedNode.getName() == null ? "" : selectedNode.getName());
        JComboBox<String> colorDropdown = new JComboBox<>(COLOR_NAME_MAP.keySet().toArray(new String[0]));
        colorDropdown.setSelectedItem(toColorName(selectedNode.getLabelColor()));
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Color"));
        formPanel.add(colorDropdown);

        int result = JOptionPane.showConfirmDialog(
                canvasPanel,
                formPanel,
                "Customize Label Style",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        selectedNode.setName(nameField.getText());
        String selectedColorName = (String) colorDropdown.getSelectedItem();
        selectedNode.setLabelColor(COLOR_NAME_MAP.getOrDefault(selectedColorName, Color.WHITE));
        if (canvasPanel != null) {
            canvasPanel.repaint();
        }
    }

    private static String toColorName(Color color) {
        for (Map.Entry<String, Color> entry : COLOR_NAME_MAP.entrySet()) {
            if (entry.getValue().equals(color)) {
                return entry.getKey();
            }
        }
        return "white";
    }

    private static Map<String, Color> createColorNameMap() {
        Map<String, Color> map = new LinkedHashMap<>();
        map.put("black", Color.BLACK);
        map.put("blue", Color.BLUE);
        map.put("cyan", Color.CYAN);
        map.put("darkgray", Color.DARK_GRAY);
        map.put("gray", Color.GRAY);
        map.put("green", Color.GREEN);
        map.put("lightgray", Color.LIGHT_GRAY);
        map.put("magenta", Color.MAGENTA);
        map.put("orange", Color.ORANGE);
        map.put("pink", Color.PINK);
        map.put("red", Color.RED);
        map.put("white", Color.WHITE);
        map.put("yellow", Color.YELLOW);
        return map;
    }
}
