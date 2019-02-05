package fr.imt.ales.msr.RawDataFilters;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import fr.imt.ales.msr.FileWritersReaders.FileReaderJSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.List;

public class RawDataFilter {
    final static Logger logger = LogManager.getLogger(RawDataFilter.class);


    private FileReaderJSON fileReaderJSON;
    private Configuration confJsonPath;

    /**
     * Constructor by default
     * Initialize the FileReaderJson and the Configuration for JsonPath
     */
    public RawDataFilter(){
        fileReaderJSON = new FileReaderJSON();
        confJsonPath = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider())
                                                .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS)
                                                .build();
    }

    /**
     * Extracts the fields according to the list of fields to extract and the path to the JSON file.
     * The function returns a JSONObject corresponding to the fields and values extracted from the JSON file.
     * -------------------------------------------------------------------------------------------
     * WARNING : the function can extract the fields only on a same depth level in the JSONObject
     * -------------------------------------------------------------------------------------------
     * @param pathToJSONFileFromGithub Path to the JSON file obtained from the GitHub API
     * @param fieldsToExtract List of fields in String to extract
     * @return A JSONObject (org.json) which contains only the fields extracted
     * @throws IOException Thrown when the an IO problem occurs with the Json file
     * @throws URISyntaxException Thrown when the path is file
     */
    public JSONObject extractSpecificFieldsFromJSONFile(String pathToJSONFileFromGithub, List<String> fieldsToExtract) throws IOException, URISyntaxException {
        if(fieldsToExtract == null || fieldsToExtract.size() < 1){
            logger.error("At least one field to extract must be specified");
            return null;
        }

        //read the JSON file saved from Github
        JSONObject allItemsFromGHjsonObject = fileReaderJSON.readJSONFile(pathToJSONFileFromGithub);

        //Construct the build path
        StringBuilder jsonPathStringBuilder = new StringBuilder("$..[");
        for (String field : fieldsToExtract) {
            jsonPathStringBuilder.append("'").append(field).append("',");
        }
        jsonPathStringBuilder.deleteCharAt(jsonPathStringBuilder.lastIndexOf(",")).append("]");

        //Filter fields with json path expression and return the JSON object with the extracted fields
        return extract(jsonPathStringBuilder.toString(), allItemsFromGHjsonObject);
    }

    /**
     * Extracts the fields according to jsonPath expression and the path to the JSON file.
     * The function returns a JSONObject corresponding to the fields and values extracted from the JSON file.
     * -------------------------------------------------------------------------------------------
     * WARNING : the function can extract the fields only on a same depth level in the JSONObject
     * -------------------------------------------------------------------------------------------
     * @param pathToJSONFileFromGithub Path to the JSON file obtained from the GitHub API
     * @param jsonPathExprString JsonPath expression
     * @return A JSONObject (org.json) which contains only the fields extracted
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject extractSpecificFieldsFromJSONFile(String pathToJSONFileFromGithub, String jsonPathExprString) throws IOException, URISyntaxException {
        if(jsonPathExprString == null || jsonPathExprString.equals("")){
            logger.error("The Json Path expression cannot be null or empty");
            return null;
        }

        //read the JSON file saved from Github
        JSONObject allItemsFromGHjsonObject = fileReaderJSON.readJSONFile(pathToJSONFileFromGithub);

        return extract(jsonPathExprString, allItemsFromGHjsonObject);
    }

    /**
     * Extracts the fields according to jsonPath expression and the JSON object
     * @param jsonPathExprString JsonPath expression
     * @param allItemsFromGHJsonObject JSON Object from Github which contains the fields to extract
     * @return A JSONObject (org.json) which contains only the fields extracted
     */
    public JSONObject extract(String jsonPathExprString, JSONObject allItemsFromGHJsonObject) {
        ArrayNode arrayNodeExtractedItems = JsonPath.using(confJsonPath)
                .parse(allItemsFromGHJsonObject.toString())
                .read(jsonPathExprString);

        JSONObject jsonObjExtractionResults = new JSONObject();
        jsonObjExtractionResults.put("total_count", arrayNodeExtractedItems.size());
        jsonObjExtractionResults.put("items", new JSONArray(arrayNodeExtractedItems.toString()));

        return jsonObjExtractionResults;
    }

    /**
     * Deletes the duplicate entries into the JSON object given in parameter
     * @param jsonObjectToClean JSONObject containing an element 'items' whose value is a JSONArray
     */
    public void deleteDuplicateEntriesJSONObject(JSONObject jsonObjectToClean){
        if(!jsonObjectToClean.has("items")){
            logger.warn("The JSON object contains a JSONArray 'items'");
            return;
        }

        if(!(jsonObjectToClean.get("items") instanceof JSONArray)){
            logger.warn("The JSON object 'items' is not a JSONArray");
            return;
        }

        JSONArray jsonArrayClean = deleteDuplicateEntriesJSONArray(jsonObjectToClean.getJSONArray("items"));

        jsonObjectToClean.remove("items");
        jsonObjectToClean.remove("total_count");
        jsonObjectToClean.put("total_count",jsonArrayClean.length());
        jsonObjectToClean.put("items",jsonArrayClean);
    }

    /**
     * Deletes the duplicate entries into the JSONArray given in parameter
     * @param jsonArrayToClean JSONArray which contains duplicate entries
     * @return JSONArray witout duplicate entries
     */
    public JSONArray deleteDuplicateEntriesJSONArray(JSONArray jsonArrayToClean){
        List<Object> listWithDuplicateEntries = jsonArrayToClean.toList();
        //Create a linkedHashSet to delete the duplicate
        LinkedHashSet<Object> linkedHashSetClean = new LinkedHashSet<>(listWithDuplicateEntries);
        //Create a new JSON Array with the linkedHashSet without duplicate objects
        return new JSONArray(linkedHashSetClean);
    }

}
