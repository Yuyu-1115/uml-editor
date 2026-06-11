package model;

import model.shape.UMLGroup;
import model.shape.UMLOval;
import model.shape.UMLRect;

public interface UMLNodeVisitor {
    void visit(UMLRect rect);
    void visit(UMLOval oval);
    void visit(UMLGroup group);
}
