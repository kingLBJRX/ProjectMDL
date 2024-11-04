package models;

public class Field {
    private final String name;
    private final String type;
    private final boolean isImmutable;
    private final boolean isOptional;
    private final String annotation;

    public Field(String name, String type, boolean isImmutable, boolean isOptional, String annotation) {
        this.name = name;
        this.type = type;
        this.isImmutable = isImmutable;
        this.isOptional = isOptional;
        this.annotation = annotation;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isImmutable() {
        return isImmutable;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public String getAnnotation() {
        return annotation;
    }
}
