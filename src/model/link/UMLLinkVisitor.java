package model.link;

public interface UMLLinkVisitor {
    void visit(AssociationLink link);
    void visit(GeneralizationLink link);
    void visit(CompositionLink link);
}
