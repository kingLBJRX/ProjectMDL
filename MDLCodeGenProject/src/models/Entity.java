package models;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private final String name;
    private final List<Field> fields = new ArrayList<>();

    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void addField(Field field) {
        fields.add(field);
    }
}
