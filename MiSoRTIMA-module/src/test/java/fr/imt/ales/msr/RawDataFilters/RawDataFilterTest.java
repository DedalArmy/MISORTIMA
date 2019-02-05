package fr.imt.ales.msr.RawDataFilters;

import fr.imt.ales.msr.RawDataFilters.RawDataFilter;
import org.json.JSONArray;
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
        fieldsToExtract.add("commits_url");
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

        JSONObject jsonObjectActual = rawDataFilter.extractSpecificFieldsFromJSONFile(pathToJsonFileTestRawData, "$..['git_url','stargazers_count','owner','commits_url']");
        assertEquals(new JSONObject(fileContentJsonObjectExpected).toString(2),jsonObjectActual.toString(2));
    }

    @Test
    public void testDeleteDuplicateEntriesJSONObject(){
        RawDataFilter rawDataFilter = new RawDataFilter();

        JSONArray jsonArrayItems = new JSONArray();
        jsonArrayItems.put(new JSONObject().put("key1Duplicate","value1duplicate"));
        jsonArrayItems.put(new JSONObject().put("key1Duplicate","value1duplicate"));
        jsonArrayItems.put(new JSONObject().put("key2","value2"));
        jsonArrayItems.put(new JSONObject().put("key3Duplicate","value3Duplicate"));
        jsonArrayItems.put(new JSONObject().put("key3Duplicate","value3Duplicate"));
        jsonArrayItems.put(new JSONObject().put("key4","value4"));
        JSONObject jsonObjectToClean = new JSONObject();
        jsonObjectToClean.put("items",jsonArrayItems);
        jsonObjectToClean.put("total_count",jsonArrayItems.length());

        JSONArray jsonArrayItemsExpected = new JSONArray();
        jsonArrayItemsExpected.put(new JSONObject().put("key1Duplicate","value1duplicate"));
        jsonArrayItemsExpected.put(new JSONObject().put("key2","value2"));
        jsonArrayItemsExpected.put(new JSONObject().put("key3Duplicate","value3Duplicate"));
        jsonArrayItemsExpected.put(new JSONObject().put("key4","value4"));
        JSONObject jsonObjectExpected = new JSONObject();
        jsonObjectExpected.put("items",jsonArrayItemsExpected);
        jsonObjectExpected.put("total_count",jsonArrayItemsExpected.length());

        rawDataFilter.deleteDuplicateEntriesJSONObject(jsonObjectToClean);

        assertEquals(jsonObjectExpected.toString(2),jsonObjectToClean.toString(2));
    }

    @Test
    public void testDeleteDuplicateEntriesJSONObjectWithoutItems(){
        RawDataFilter rawDataFilter = new RawDataFilter();

        JSONObject jsonObjectToClean = new JSONObject();
        jsonObjectToClean.put("total_count",52);
        jsonObjectToClean.put("hello",new JSONObject().put("World","MyWorld"));

        JSONObject jsonObjectExpected = new JSONObject(jsonObjectToClean.toString());

        rawDataFilter.deleteDuplicateEntriesJSONObject(jsonObjectToClean);

        assertEquals(jsonObjectExpected.toString(2),jsonObjectToClean.toString(2));
    }

    @Test
    public void testDeleteDuplicateEntriesJSONObjectWithItemsNotJSONArray(){
        RawDataFilter rawDataFilter = new RawDataFilter();

        JSONObject jsonObjectToClean = new JSONObject();
        jsonObjectToClean.put("total_count",52);
        jsonObjectToClean.put("items",new JSONObject().put("Hello","World"));

        JSONObject jsonObjectExpected = new JSONObject(jsonObjectToClean.toString());

        rawDataFilter.deleteDuplicateEntriesJSONObject(jsonObjectToClean);

        assertEquals(jsonObjectExpected.toString(2),jsonObjectToClean.toString(2));
    }

}