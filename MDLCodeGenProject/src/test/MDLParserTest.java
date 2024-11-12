package test;
import static org.junit.jupiter.api.Assertions.*;

import models.Entity;
import models.Field;
import models.MDLParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MDLParserTest {

    @InjectMocks
    private MDLParser parser;

    @TempDir
    Path tempDir;

    // Helper method to create a temporary MDL file with the given content
    private File createTempMDLFile(String content) throws IOException {
        File tempFile = tempDir.resolve("test.mdl").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        return tempFile;
    }

    // Test 1: Parsing a simple MDL file with only attributes
    @Test
    public void testSimpleMDLParsing() throws IOException {
        String mdlContent = """
                entity Person {
                    age: int
                    name: String
                }""";
        File mdlFile = createTempMDLFile(mdlContent);

        Entity generatedClass = parser.parse(mdlFile).get(0);

        assertNotNull(generatedClass);
        assertEquals("Person", generatedClass.getName());
        assertEquals(generatedClass.getFields().stream().map(Field::getName).toList(), Arrays.asList("age", "name"));
        assertEquals(generatedClass.getFields().stream().map(Field::getType).toList(), Arrays.asList("int", "String"));
    }


    // Test 2: Parsing an MDL file with optional fields
    @Test
    public void testOptionalFieldsParsing() throws IOException {
        String mdlContent = """
                entity Person {
                    age: int
                    nickname: String optional
                }""";
        File mdlFile = createTempMDLFile(mdlContent);

        Entity generatedClass = parser.parse(mdlFile).get(0);

        assertNotNull(generatedClass);
        assertEquals("Person", generatedClass.getName());
        assertEquals(2, generatedClass.getFields().size());

        Field nicknameField = generatedClass.getFields().stream()
                .filter(field -> "nickname".equals(field.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(nicknameField);
        assertTrue(nicknameField.isOptional());
    }

    // Test 3: Parsing an MDL file with immutable fields
    @Test
    public void testImmutableFieldsParsing() throws IOException {
        String mdlContent = """
                entity Person {
                    age: int
                    name: String immutable
                }""";
        File mdlFile = createTempMDLFile(mdlContent);

        Entity generatedClass = parser.parse(mdlFile).get(0);

        assertNotNull(generatedClass);
        Field nameField = generatedClass.getFields().stream()
                .filter(field -> "name".equals(field.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(nameField);
        assertTrue(nameField.isImmutable());
    }

    // Test 4: Parsing multiple entities in one MDL file
    @Test
    public void testMultipleEntitiesParsing() throws IOException {
        String mdlContent = """
                entity Person {
                    age: int
                    name: String
                }
                entity Address {
                    street: String
                    city: String
                }""";
        File mdlFile = createTempMDLFile(mdlContent);

        List<Entity> entities = parser.parse(mdlFile);

        assertEquals(2, entities.size());

        Entity personEntity = entities.stream().filter(e -> "Person".equals(e.getName())).findFirst().orElse(null);
        Entity addressEntity = entities.stream().filter(e -> "Address".equals(e.getName())).findFirst().orElse(null);

        assertNotNull(personEntity);
        assertNotNull(addressEntity);
        assertEquals(Arrays.asList("age", "name"), personEntity.getFields().stream().map(Field::getName).toList());
        assertEquals(Arrays.asList("street", "city"), addressEntity.getFields().stream().map(Field::getName).toList());
    }

    // Test 5: Parsing an MDL file with complex data types (e.g., List)
    @Test
    public void testComplexDataTypesParsing() throws IOException {
        String mdlContent = """
                entity Person {
                    name: String
                    friends: List<Person>
                }""";
        File mdlFile = createTempMDLFile(mdlContent);

        Entity generatedClass = parser.parse(mdlFile).get(0);

        assertNotNull(generatedClass);
        Field friendsField = generatedClass.getFields().stream()
                .filter(field -> "friends".equals(field.getName()))
                .findFirst()
                .orElse(null);
        assertNotNull(friendsField);
        assertEquals("List<Person>", friendsField.getType());
    }
}
