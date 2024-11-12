package models;

import java.io.*;
import java.nio.file.*;
import java.util.List;



public class JavaClassGenerator {

    public void generate(List<Entity> entities, File targetDir) throws IOException {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        for (Entity entity : entities) {
            File javaFile = new File(targetDir, entity.getName() + ".java");
            try (BufferedWriter writer = Files.newBufferedWriter(javaFile.toPath())) {

                // Write class declaration
                writer.write("public class " + entity.getName() + " {\n\n");

                // Declare fields and flags for optional fields
                for (Field field : entity.getFields()) {
                    if (field.getAnnotation() != null) {
                        writer.write("    " + field.getAnnotation() + "\n");
                    }
                    String prefix = "    private ";
                    if (field.isImmutable()) {
                        prefix = "    private final ";
                    }
                    writer.write(prefix + field.getType() + " " + field.getName() + ";\n");
                    if (field.isOptional()) {
                        writer.write("    private boolean has" + capitalize(field.getName()) + " = false;\n");
                    }
                }
                writer.write("\n");

                // Private constructor accepting all fields
                writer.write("    private " + entity.getName() + "(");
                for (int i = 0; i < entity.getFields().size(); i++) {
                    Field field = entity.getFields().get(i);
                    writer.write(field.getType() + " " + field.getName());
                    if (i < entity.getFields().size() - 1) writer.write(", ");
                }
                writer.write(") {\n");
                for (Field field : entity.getFields()) {
                    writer.write("        this." + field.getName() + " = " + field.getName() + ";\n");
                }
                writer.write("    }\n\n");

                // Inner Builder Class
                writer.write("    public static class Builder {\n");

                // Builder fields and setters
                for (Field field : entity.getFields()) {
                    writer.write("        private " + field.getType() + " " + field.getName() + ";\n");

                    // Setter method for each field
                    writer.write("        public Builder set" + capitalize(field.getName()) + "(" + field.getType() + " " + field.getName() + ") {\n");
                    writer.write("            this." + field.getName() + " = " + field.getName() + ";\n");
                    if (field.isOptional()) {
                        writer.write("            has" + capitalize(field.getName()) + " = true;\n");
                    }
                    writer.write("            return this;\n");
                    writer.write("        }\n\n");
                }

                // Build method with required fields validation
                writer.write("        public " + entity.getName() + " build() {\n");

                // Check that all non-optional fields are set
                for (Field field : entity.getFields()) {
                    if (!field.isOptional()) {
                        writer.write("            if (this." + field.getName() + " == null) {\n");
                        writer.write("                throw new IllegalStateException(\"Required field '" + field.getName() + "' is missing\");\n");
                        writer.write("            }\n");
                    }
                }

                // Call private constructor from Builder
                writer.write("            return new " + entity.getName() + "(");
                for (int i = 0; i < entity.getFields().size(); i++) {
                    Field field = entity.getFields().get(i);
                    writer.write(field.getName());
                    if (i < entity.getFields().size() - 1) writer.write(", ");
                }
                writer.write(");\n        }\n");

                writer.write("    }\n\n");  // End of Builder class

                // Getter methods for each field in the main class
                for (Field field : entity.getFields()) {
                    String capitalized = capitalize(field.getName());

                    // Getter
                    writer.write("    public " + field.getType() + " get" + capitalized + "() {\n");
                    writer.write("        return " + field.getName() + ";\n");
                    writer.write("    }\n\n");
                }

                // Setter methods for each non-immutable field in the main class
                for (Field field : entity.getFields()) {
                    String capitalized = capitalize(field.getName());

                    if (!field.isImmutable()){
                        // Setter
                        writer.write("    public void set" + capitalized + "(" + field.getType() + " " + field.getName() + ") {\n");
                        writer.write("        this." + field.getName() + " = " + field.getName() + ";\n");
                        writer.write("    }\n\n");
                    }
                }

                writer.write("}\n");  // End of Entity class
            }
        }
    }

    // Helper method to capitalize field names for method generation
    private String capitalize(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
