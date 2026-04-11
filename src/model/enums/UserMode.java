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

    public boolean isLinkMode() {
        return this == ASSOCIATION || this == GENERALIZATION || this == COMPOSITION;
    }

    UserMode(String text) {
        this.text = text;
    }
}
