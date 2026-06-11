package model.node;

public interface UMLNodeVisitor {
    void visit(UMLRect rect);
    void visit(UMLOval oval);
    void visit(UMLGroup group);
}
