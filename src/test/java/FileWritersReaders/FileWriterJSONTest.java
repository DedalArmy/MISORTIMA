package FileWritersReaders;

import fr.imt.ales.msr.FileWritersReaders.FileWriterJSON;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileWriterJSONTest {

    private final String NAME_FILE_JSON = "json-test-generated.json";

    private JSONObject jsonObjectExpected;
    private String fileContentJsonObjectExpected;
    private String pathTmpDir;

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("json-example.json");
        fileContentJsonObjectExpected = new String(Files.readAllBytes(Paths.get(url.toURI())), Charset.forName("UTF-8"));
        jsonObjectExpected = new JSONObject(fileContentJsonObjectExpected);

        pathTmpDir = System.getProperty("java.io.tmpdir");
    }

    @Test
    public void testWriteJsonFileNominalCase() throws IOException {
        FileWriterJSON fileWriterJSON = new FileWriterJSON();
        fileWriterJSON.writeJsonFile(jsonObjectExpected, pathTmpDir, NAME_FILE_JSON);

        File fileExpected = new File(pathTmpDir + "/" + NAME_FILE_JSON);
        assertTrue(fileExpected.isFile());

        String fileContentJsonObjectActual = new String(Files.readAllBytes(Paths.get(pathTmpDir + "/" + NAME_FILE_JSON)), Charset.forName("UTF-8"));
        assertEquals(fileContentJsonObjectExpected,fileContentJsonObjectActual);
    }


    @Test
    public void testWriteJsonFileWithMalformedPath() throws IOException {
        FileWriterJSON fileWriterJSON = new FileWriterJSON();
        String malformedPath = "Yooou/Hooouuu";

        Throwable exception = assertThrows(InvalidPathException.class,
                ()->{fileWriterJSON.writeJsonFile(jsonObjectExpected, malformedPath, NAME_FILE_JSON);} );

        assertEquals("Error path to write JSON file is invalid or is not a directory: " + malformedPath, exception.getMessage());
    }

    @Test
    public void testWriteJsonFileWithIOExceptionThrown() throws IOException {
        File file = new File(pathTmpDir +"/test");
        file.mkdir();
        //Lock the folder /tmp/test to generate IOException in the FileWriter
        file.setWritable(false);

        FileWriterJSON fileWriterJSON = new FileWriterJSON();
        fileWriterJSON.writeJsonFile(jsonObjectExpected, pathTmpDir +"/test", NAME_FILE_JSON);

        assertFalse( new File(pathTmpDir + "/" + NAME_FILE_JSON).exists());

        file.setWritable(true);
        file.delete();
    }

    @AfterEach
    public void tearDown(){
        File fileExpected = new File(pathTmpDir + "/" + NAME_FILE_JSON);
        fileExpected.delete();
    }
}
