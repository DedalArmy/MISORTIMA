package fr.imt.ales.msr.RawDataFilters;

import fr.imt.ales.msr.RawDataFilters.RawDataFilter;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RawDataFilterTest {
    private String fileContentJsonObjectExpected;
    private String pathToJsonFileTestRawData;

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException {
        URL urlJsonFileRaw = Thread.currentThread().getContextClassLoader().getResource("json-example.json");
        pathToJsonFileTestRawData = urlJsonFileRaw.getPath();

        URL urlJsonFileExpected = Thread.currentThread().getContextClassLoader().getResource("json-example-filtered.json");
        fileContentJsonObjectExpected = new String(Files.readAllBytes(Paths.get(urlJsonFileExpected.toURI())), Charset.forName("UTF-8"));
    }

    @Test
    public void testExtractSpecificFieldsFromJSONFileNominalCase() throws IOException, URISyntaxException {
        RawDataFilter rawDataFilter = new RawDataFilter();

        List<String> fieldsToExtract = new ArrayList<>();
        fieldsToExtract.add("git_url");
        fieldsToExtract.add("stargazers_count");
        fieldsToExtract.add("owner");

        JSONObject jsonObjectActual = rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileTestRawData,fieldsToExtract);
        assertEquals(new JSONObject(fileContentJsonObjectExpected).toString(2),jsonObjectActual.toString(2));
    }

    @Test
    public void testExtractSpecificFieldsFromJSONFileWithFieldsListEmpty() throws IOException, URISyntaxException {
        RawDataFilter rawDataFilter = new RawDataFilter();

        List<String> fieldsToExtract = new ArrayList<>();

        assertNull(rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileTestRawData,fieldsToExtract));
    }
    @Test
    public void testExtractSpecificFieldsFromJSONFileWithFieldsListNull() throws IOException, URISyntaxException {
        RawDataFilter rawDataFilter = new RawDataFilter();

        assertNull(rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileTestRawData, (List<String>) null));
    }

    @Test
    public void testExtractSpecificFieldsFromJSONFileWithJsonPathExprEmpty() throws IOException, URISyntaxException {
        RawDataFilter rawDataFilter = new RawDataFilter();

        assertNull(rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileTestRawData,""));
    }

    @Test
    public void testExtractSpecificFieldsFromJSONFileWithJsonPathExprNull() throws IOException, URISyntaxException {
        RawDataFilter rawDataFilter = new RawDataFilter();

        assertNull(rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileTestRawData, (String) null));
    }

    @Test
    public void testExtractSpecificFieldsJsonPathFromJSONFileNominalCase() throws IOException, URISyntaxException {
        RawDataFilter rawDataFilter = new RawDataFilter();

        JSONObject jsonObjectActual = rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileTestRawData, "$..['git_url','stargazers_count','owner']");
        assertEquals(new JSONObject(fileContentJsonObjectExpected).toString(2),jsonObjectActual.toString(2));
    }
}
