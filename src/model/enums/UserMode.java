package model.enums;

public enum UserMode {
    SELECT("Select"),
    ASSOCIATION("Association"),
    GENERALIZATION("Generalization"),
    COMPOSITION("Composition"),
    RECT("Rect"),
    OVAL("Oval");

    private final String text;

    public String getText() {
        return text;
    }

    UserMode(String text) {
        this.text = text;
    }
}
