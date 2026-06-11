package controller;

import model.SelectionModel;
import model.UMLModel;
import model.enums.UserMode;
import view.UMLPanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.Map;

public class UMLController extends MouseAdapter {
    private final UMLModel model;
    private final UMLPanel umlPanel;
    private final Map<UserMode, CanvasTool> tools = new EnumMap<>(UserMode.class);
    private final CanvasTool noOpTool;

    public UMLController(UMLModel model, UMLPanel umlPanel) {
        this.model = model;
        this.umlPanel = umlPanel;

        SelectionModel selectionModel = model.getSelectionModel();
        CanvasTool selectTool = new SelectTool(model, umlPanel);
        CanvasTool linkTool = new LinkTool(model, umlPanel);
        noOpTool = new CanvasTool() {
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseDragged(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseMoved(MouseEvent e) {
                selectionModel.clearHover();
                umlPanel.repaint();
            }
        };

        tools.put(UserMode.SELECT, selectTool);
        tools.put(UserMode.ASSOCIATION, linkTool);
        tools.put(UserMode.GENERALIZATION, linkTool);
        tools.put(UserMode.COMPOSITION, linkTool);
        tools.put(UserMode.RECT, noOpTool);
        tools.put(UserMode.OVAL, noOpTool);
    }

    private CanvasTool getActiveTool() {
        return tools.getOrDefault(model.getUserMode(), noOpTool);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        getActiveTool().mousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        getActiveTool().mouseDragged(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        getActiveTool().mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        getActiveTool().mouseMoved(e);
    }
}
