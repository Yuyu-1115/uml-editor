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
    private final Map<UserMode, CanvasTool> tools = new EnumMap<>(UserMode.class);
    private final CanvasTool noOpTool;

    public UMLController(UMLModel model, UMLPanel umlPanel) {
        this.model = model;

        SelectionModel selectionModel = model.getSelectionModel();
        CanvasTool selectTool = new SelectTool(model);
        CanvasTool linkTool = new LinkTool(model);
        noOpTool = new CanvasTool() {
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseDragged(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseMoved(MouseEvent e) {
                selectionModel.clearHover();
                model.fireModelChanged();
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
        getActiveTool().mousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        getActiveTool().mouseDragged(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        getActiveTool().mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        getActiveTool().mouseMoved(e);
    }
}
