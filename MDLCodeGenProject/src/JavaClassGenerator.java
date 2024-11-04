import models.Entity;
import models.Field;

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
                writer.write("public class " + entity.getName() + " {\n\n");
                for (Field field : entity.getFields()) {
                    if (field.getAnnotation() != null) {
                        writer.write("    " + field.getAnnotation() + "\n");
                    }
                    writer.write("    private " + field.getType() + " " + field.getName() + ";\n");
                }
                writer.write("\n    // Constructor, Getters, and Setters\n\n");

                // Constructor
                writer.write("    public " + entity.getName() + "() {}\n\n");

                // Getters and Setters
                for (Field field : entity.getFields()) {
                    String capitalized = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

                    // Getter
                    writer.write("    public " + field.getType() + " get" + capitalized + "() {\n");
                    writer.write("        return " + field.getName() + ";\n");
                    writer.write("    }\n\n");

                    // Setter with immutability check
                    if (!field.isImmutable()) {
                        writer.write("    public void set" + capitalized + "(" + field.getType() + " " + field.getName() + ") {\n");
                        writer.write("        this." + field.getName() + " = " + field.getName() + ";\n");
                        writer.write("    }\n\n");
                    }
                }
                writer.write("}\n");
            }
        }
    }
}
