package test;

import static org.junit.jupiter.api.Assertions.*;

import models.Entity;
import models.Field;
import models.JavaClassGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class JavaClassGeneratorTest {

    private final JavaClassGenerator generator = new JavaClassGenerator();

    @TempDir
    Path tempDir;

    // Helper method to create an Entity with fields
    private Entity createEntity(String name, List<Field> fields) {
        Entity entity = new Entity(name);
        for (Field field:fields){
            entity.addField(field);
        }
        return entity;
    }

    // Helper method to create a Field
    private Field createField(String name, String type, boolean isOptional, boolean isImmutable) {
        return new Field(name, type, isImmutable, isOptional, null);
    }

    // Test 1: Generate a simple Java class with basic fields
    @Test
    public void testGenerateSimpleJavaClass() throws IOException, InterruptedException {
        Entity personEntity = createEntity("Person", Arrays.asList(
                createField("age", "int", false, false),
                createField("name", "String", false, false)
        ));

        File outputFile = tempDir.resolve("Person").toFile();
        generator.generate(List.of(personEntity), outputFile);

        assertTrue(outputFile.exists());
        Path outputDir = outputFile.toPath();
        try {
            Path singleFilePath = Files.list(outputDir)
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No file found in directory"));

            String generatedCode = new String(Files.readAllBytes(singleFilePath));

            assertTrue(generatedCode.contains("public class Person"));
            assertTrue(generatedCode.contains("private int age;"));
            assertTrue(generatedCode.contains("private String name;"));
            assertTrue(generatedCode.contains("public int getAge()"));
            assertTrue(generatedCode.contains("public String getName()"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Test 2: Generate Java class with optional fields
    @Test
    public void testGenerateClassWithOptionalFields() throws IOException {
        Entity personEntity = createEntity("Person", Arrays.asList(
                createField("age", "int", false, false),
                createField("nickname", "String", true, false)
        ));

        File outputFile = tempDir.resolve("Person.java").toFile();
        generator.generate(List.of(personEntity), outputFile);

        assertTrue(outputFile.exists());
        Path outputDir = outputFile.toPath();
        try {
            Path singleFilePath = Files.list(outputDir)
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No file found in directory"));

            String generatedCode = new String(Files.readAllBytes(singleFilePath));

            assertTrue(generatedCode.contains("private String nickname;"));
            assertTrue(generatedCode.contains("private boolean hasNickname = false;"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Test 3: Generate Java class with immutable fields
    @Test
    public void testGenerateClassWithImmutableFields() throws IOException {
        Entity personEntity = createEntity("Person", Arrays.asList(
                createField("id", "int", false, true),
                createField("name", "String", false, false)
        ));

        File outputFile = tempDir.resolve("Person.java").toFile();
        generator.generate(List.of(personEntity), outputFile);

        assertTrue(outputFile.exists());

        Path outputDir = outputFile.toPath();

        try {
            Path singleFilePath = Files.list(outputDir)
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No file found in directory"));

            String generatedCode = new String(Files.readAllBytes(singleFilePath));

            assertTrue(generatedCode.contains("private final int id;"));
            assertTrue(generatedCode.contains("public void setName(String name)"));
            assertFalse(generatedCode.contains("public void setId("));  // Immutable fields should not have a setter

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Test 4: Generate multiple classes
    @Test
    public void testGenerateMultipleClasses() throws IOException {
        Entity personEntity = createEntity("Person", Arrays.asList(
                createField("age", "int", false, false),
                createField("name", "String", false, false)
        ));
        Entity addressEntity = createEntity("Address", Arrays.asList(
                createField("street", "String", false, false),
                createField("city", "String", false, false)
        ));

        File personOutputFile = tempDir.resolve("Person.java").toFile();
        File addressOutputFile = tempDir.resolve("Address.java").toFile();

        generator.generate(List.of(personEntity), personOutputFile);
        generator.generate(List.of(addressEntity), addressOutputFile);

        assertTrue(personOutputFile.exists());
        assertTrue(addressOutputFile.exists());

        Path personOutputDir = personOutputFile.toPath();
        Path addressOutputtDir = addressOutputFile.toPath();

        try {
            Path personSingleFilePath = Files.list(personOutputDir)
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No file found in directory"));
            String personCode = new String(Files.readAllBytes(personSingleFilePath));

            Path addressFilePath = Files.list(addressOutputtDir)
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No file found in directory"));
            String addressCode = new String(Files.readAllBytes(addressFilePath));

            assertTrue(personCode.contains("public class Person"));
            assertTrue(addressCode.contains("public class Address"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(personOutputFile.exists());
        assertTrue(addressOutputFile.exists());
    }


    // Test 5: Generate Java class with complex data types
    @Test
    public void testGenerateClassWithComplexTypes() throws IOException {
        Entity personEntity = createEntity("Person", Arrays.asList(
                createField("friends", "List<Person>", false, false)
        ));

        File outputFile = tempDir.resolve("Person.java").toFile();
        generator.generate(List.of(personEntity), outputFile);

        assertTrue(outputFile.exists());

        Path outputDir = outputFile.toPath();

        try {
            Path singleFilePath = Files.list(outputDir)
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No file found in directory"));

            String generatedCode = new String(Files.readAllBytes(singleFilePath));

            assertTrue(generatedCode.contains("private List<Person> friends;"));
            assertTrue(generatedCode.contains("public List<Person> getFriends()"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
