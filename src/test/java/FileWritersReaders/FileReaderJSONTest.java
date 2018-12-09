package FileWritersReaders;

import fr.imt.ales.msr.FileWritersReaders.FileReaderJSON;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class FileReaderJSONTest {
    private String pathToJsonFile;
    private String fileContentJsonObjectExpected;

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException{
        URL url = Thread.currentThread().getContextClassLoader().getResource("json-example.json");
        pathToJsonFile = url.getPath();
        fileContentJsonObjectExpected = new String(Files.readAllBytes(Paths.get(url.toURI())), Charset.forName("UTF-8"));
    }

    @Test
    public void testReadJSONFileNominalcase() throws IOException, URISyntaxException {
        FileReaderJSON fileReaderJSON = new FileReaderJSON();
        JSONObject jsonObjectActual = fileReaderJSON.readJSONFile(pathToJsonFile);

        assertEquals(fileContentJsonObjectExpected,jsonObjectActual.toString(2));
    }

    @Test
    public void testReadJSONFileWithMalformedPath(){
        FileReaderJSON fileReaderJSON = new FileReaderJSON();
        String malformedPath = "....Yooou\\/Hooouuu";

        assertThrows(NoSuchFileException.class, ()->{fileReaderJSON.readJSONFile(malformedPath);} );
    }

    @Test
    public void testReadJSONFileWithIOException() throws IOException {
        FileReaderJSON fileReaderJSON = new FileReaderJSON();

        assertThrows(IOException.class, ()->{fileReaderJSON.readJSONFile(pathToJsonFile + "Error");} );
    }
}
