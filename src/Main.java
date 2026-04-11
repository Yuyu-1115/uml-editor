import model.UMLModel;
import view.UMLUiBuilder;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUi);
    }


    private static void createAndShowUi() {
        setSystemLookAndFeel();

        JFrame frame = new JFrame("UML Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        UMLModel umlModel = new UMLModel();
        UMLUiBuilder uiBuilder = new UMLUiBuilder(umlModel);
        frame.setJMenuBar(uiBuilder.createTopMenuBar());
        frame.add(uiBuilder.createToolPanel(), BorderLayout.WEST);
        frame.add(uiBuilder.createCanvasPanel(), BorderLayout.CENTER);

        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set system look and feel", e);
        }
    }
}
