package models;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        File mdlFolder = new File("MDLCodeGenProject/MDLs");
        File targetFolder = new File("MDLCodeGenProject/src/generatedClass");

        MDLParser parser = new MDLParser();
        JavaClassGenerator generator = new JavaClassGenerator();

        try {
            for (File mdlFile : mdlFolder.listFiles((dir, name) -> name.endsWith(".mdl"))) {
                List<Entity> entities = parser.parse(mdlFile);
                generator.generate(entities, targetFolder);
            }
            System.out.println("Java classes generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
