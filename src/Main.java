import controller.UMLController;
import model.UMLModel;
import view.UMLPanel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUi);
    }

    private static void createAndShowUi() {
        setSystemLookAndFeel();

        JFrame frame = new JFrame("UML Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(createToolPanel(), BorderLayout.WEST);
        frame.add(createCanvasPanel(), BorderLayout.CENTER);

        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        toolPanel.setPreferredSize(new Dimension(180, 0));
        toolPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] tools = {
                "Select",
                "Association",
                "Generalization",
                "Composition",
                "Rect",
                "Oval"
        };

        for (String tool : tools) {
            JButton button = new JButton(tool);
            button.setAlignmentX(JButton.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            toolPanel.add(button);
            toolPanel.add(javax.swing.Box.createVerticalStrut(8));
        }

        return toolPanel;
    }

    private static JPanel createCanvasPanel() {
        UMLModel umlModel = new UMLModel();

        UMLPanel canvasPanel = new UMLPanel(umlModel);
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));


        UMLController umlController = new UMLController(umlModel,canvasPanel);
        canvasPanel.addMouseListener(umlController);
        canvasPanel.addMouseMotionListener(umlController);
        return canvasPanel;
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set system look and feel", e);
        }
    }
}
