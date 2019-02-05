package fr.imt.ales.msr.FileWritersReaders;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class FileReaderJSON {
    public FileReaderJSON(){ }

    public JSONObject readJSONFile(String pathToJsonFile) throws IOException, URISyntaxException {
        URL jsonFileUrl = new File(pathToJsonFile).toURI().toURL();

        String fileContentJson = new String(Files.readAllBytes(Paths.get(jsonFileUrl.toURI())), Charset.forName("UTF-8"));

        return new JSONObject(fileContentJson);
    }
}
