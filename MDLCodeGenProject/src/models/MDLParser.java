package models;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class MDLParser {

    private static final Pattern ENTITY_PATTERN = Pattern.compile("entity (\\w+) \\{");
    private static final Pattern FIELD_PATTERN = Pattern.compile("(\\w+): (\\w+(?:<[^>]+>)?)(?:\\s+(.*))?");

    public List<Entity> parse(File mdlFile) throws IOException {
        List<Entity> entities = new ArrayList<>();
        Entity currentEntity = null;

        try (BufferedReader reader = Files.newBufferedReader(mdlFile.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                Matcher entityMatcher = ENTITY_PATTERN.matcher(line);
                Matcher fieldMatcher = FIELD_PATTERN.matcher(line);

                if (entityMatcher.matches()) {
                    currentEntity = new Entity(entityMatcher.group(1));
                    entities.add(currentEntity);
                } else if (fieldMatcher.matches() && currentEntity != null) {
                    String name = fieldMatcher.group(1);
                    String type = fieldMatcher.group(2);
                    String properties = fieldMatcher.group(3) != null ? fieldMatcher.group(3) : "";

                    boolean isImmutable = properties.contains("immutable");
                    boolean isOptional = properties.contains("optional");
                    String annotation = properties.contains("@") ? properties.substring(properties.indexOf("@")) : null;

                    currentEntity.addField(new Field(name, type, isImmutable, isOptional, annotation));
                }
            }
        }
        return entities;
    }
}
