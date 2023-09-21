package by.javaguru;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class JakartaTest {
    private static final Path RESOURCES_PATH = Path.of("src", "test", "resources");
    private static final Path CORRUPTED_JSON_PATH =
            RESOURCES_PATH.resolve("corrupted.json").toAbsolutePath();
    private static Path JSON_WITH_CONTENT_PATH;
    private static String SAVE_FILE_NAME;

    @BeforeAll
    public static void loadProperties() {
        Properties properties;
        try (InputStream input = JakartaTest.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            properties = new Properties();
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String jsonName = properties.getProperty("jsonPath");
        JSON_WITH_CONTENT_PATH = RESOURCES_PATH.resolve(jsonName).toAbsolutePath();
        SAVE_FILE_NAME = properties.getProperty("saveFileName");

        System.out.println("json name for tests: " + jsonName); // for debugging
        System.out.println("jakarta objects saves to: " + SAVE_FILE_NAME);
    }

    @Test
    public void writeToJson_validPath_createJson() throws IOException {
        Jakarta jakarta = createJakarta();
        Path path = Path.of(SAVE_FILE_NAME).toAbsolutePath();
        jakarta.writeToJson(path.toString());

        String expected = Files.readString(JSON_WITH_CONTENT_PATH);
        String actual = Files.readString(path);
        assertEquals(expected, actual);

        Files.deleteIfExists(path);
    }

    @Test
    public void writeToJson_invalidPath_throwsRuntimeException() {
        Path path = Path.of(".com/wrongPath");
        Jakarta jakarta = new Jakarta();
        assertThrows(RuntimeException.class,
                () -> jakarta.writeToJson(path.toString()));
    }

    @Test
    public void writeToJson_nullArgument_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Jakarta jakarta = new Jakarta();
            jakarta.writeToJson(null);
        });
    }

    @Test
    public void readFromJson_validPath_returnsResult() {
        Jakarta actual = Jakarta.readFromJson(JSON_WITH_CONTENT_PATH.toString());
        Jakarta expected = createJakarta();
        assertEquals(expected, actual);
    }

    @Test
    public void readFromJson_invalidPath_throwsRuntimeException() {
        assertThrows(RuntimeException.class,
                () -> Jakarta.readFromJson(".com/wrongPath"));
    }

    @Test
    public void readFromJson_wrongJsonContent_doesNotThrowException() {
        assertThrows(RuntimeException.class,
                () -> Jakarta.readFromJson(CORRUPTED_JSON_PATH.toString()));
    }

    @Test
    public void readFromJson_nullArgument_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Jakarta.readFromJson(null));
    }

    @Test
    public void updateTechnology_nullArgument_doesNotThrowException() {
        Jakarta jakarta = new Jakarta();
        Technology technology1 = new Technology(
                "Technology 1", "Description about technology 1.");
        Technology technology2 = new Technology(
                "Technology 2", "Description about technology 2.");
        Technology technology3 = new Technology(
                "Technology 3", "Description about technology 3.");

        List<Technology> tech = List.of(technology1, technology2, technology3);
        jakarta.setTechnologies(tech);

        assertDoesNotThrow(() -> jakarta.updateTechnology(null));
        assertEquals(jakarta.getTechnologies(), tech);
    }

    @Test
    public void updateTechnology_validArgument_replaceDescription() {
        Jakarta jakarta = new Jakarta();
        Technology technology1 = new Technology(
                "Technology 1", "Description about technology 1.");
        Technology technology2 = new Technology(
                "Technology 2", "Description about technology 2.");
        Technology technology3 = new Technology(
                "Technology 3", "Description about technology 3.");

        List<Technology> tech = List.of(technology1, technology2, technology3);
        jakarta.setTechnologies(tech);

        Technology editedTechnology2 = new Technology(
                "Technology 2",
                "New description about technology 2.");
        jakarta.updateTechnology(editedTechnology2);

        List<Technology> updatedTech = jakarta.getTechnologies();
        assertEquals(updatedTech.size(), 3);
        assertEquals(updatedTech, List.of(technology1, editedTechnology2, technology3));
    }

    private static Jakarta createJakarta() {
        String version = "10";
        String description = "Jakarta EE 10 is packed with new features for building " +
                             "modernized, simplified, and lightweight cloud native" +
                             " Java applications.";

        Technology servlet = new Technology(
                "Servlet",
                "Jakarta Servlet defines a server-side API for handling" +
                " HTTP requests and responses.");
        Technology enterpriseBeans = new Technology(
                "Enterprise Beans",
                "Jakarta Enterprise Beans defines an architecture for the" +
                " development and deployment of component-based business applications.");

        Technology persistence = new Technology(
                "Persistence",
                "Jakarta Persistence defines a standard for management of" +
                " persistence and object/relational mapping in Java(R) environments.");

        List<Technology> technologies = List.of(servlet, enterpriseBeans, persistence);

        return new Jakarta(version, description, technologies);
    }
}
