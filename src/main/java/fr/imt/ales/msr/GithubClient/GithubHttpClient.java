package fr.imt.ales.msr.GithubClient;

import fr.imt.ales.msr.FileWritersReaders.FileWriterJSON;
import fr.imt.ales.msr.LoggerUtils.LoggerPrintUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * HTTP Client to request the Github API
 * @author Quentin Perez
 * @version 1.0
 */
public class GithubHttpClient {
    private CloseableHttpClient httpClient;
    private GithubAPILimitManager githubAPILimitManager;
    private final static Logger logger = LogManager.getLogger(GithubHttpClient.class);

    /**
     * Default constructor
     */
    public GithubHttpClient(){
        httpClient = HttpClients.createDefault();
        githubAPILimitManager = new GithubAPILimitManager();
    }

    /**
     *
     * @param URLStringApi
     * @param jsonAllItems
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public JSONObject getRawDataJson(String URLStringApi, JSONObject jsonAllItems) throws IOException, URISyntaxException, InterruptedException {
        List<NameValuePair> urlParams = URLEncodedUtils.parse(new URI(urlEncodeSpecificChars(URLStringApi)), Charset.forName("UTF-8"));

        int currentPageNumber = 1;
        int lastPageNumber = 1;
        String urlNextPageString = null;
        boolean isFirstPage = true;


        for (NameValuePair param : urlParams) {
            if(param.getName().equals("page")){
                currentPageNumber = Integer.parseInt(param.getValue());
            }
        }

        logger.info("Get on : " + URLStringApi);
        //execute the first request
        HttpGet httpGet = new HttpGet(urlEncodeSpecificChars(URLStringApi));

        //Handle response body
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        String responseString = EntityUtils.toString(httpEntity, "UTF-8");

        //Get header for the next page
        Header linkHeader = httpResponse.getFirstHeader("Link");

        //Get headers and values for API limitations
        Long timestampLimitResetHeader   = Long.parseLong(httpResponse.getFirstHeader("X-RateLimit-Reset").getValue());
        Integer rateLimitRemainingHeader = Integer.parseInt(httpResponse.getFirstHeader("X-RateLimit-Remaining").getValue());

        JSONObject jsonObjectResponse = new JSONObject(responseString);

        logger.debug("Rate limit remaining : " + rateLimitRemainingHeader);

        githubAPILimitManager.handleLimitAPIGithub(timestampLimitResetHeader, rateLimitRemainingHeader);

        if(linkHeader != null){
            HeaderElement[] headerElementTabLink = linkHeader.getElements();
            for (HeaderElement headerElement : headerElementTabLink) {
                String headerElementLinkString = headerElement.toString();

                if(headerElementLinkString.contains("next")) {
                    urlNextPageString = headerElementLinkString.substring(headerElementLinkString.indexOf("<") + 1, headerElementLinkString.indexOf(">"));
                    logger.debug(urlNextPageString);
                }
                if(headerElementLinkString.contains("prev")) {
                    isFirstPage = false;
                }
                if (headerElementLinkString.contains("last")){
                    lastPageNumber = Integer.parseInt(StringUtils.substringBetween(headerElementLinkString,"page=", ">"));
                }
            }
        }

        if(isFirstPage){
            jsonAllItems = jsonObjectResponse;
        }else{
            //concatenate list items
            jsonAllItems.getJSONArray("items").put(jsonObjectResponse.getJSONArray("items"));
        }

        //return the json object if there is no next page
        if(urlNextPageString == null || urlNextPageString.equals(""))
            return jsonAllItems;

        //display launchbar
        LoggerPrintUtils.printLaunchBar(logger,"Progress status request Github API" ,currentPageNumber,lastPageNumber);
        //Call recursively on the next page
        return getRawDataJson(urlNextPageString, jsonAllItems);
    }

    /**
     * Returns the JSONObject which represent the last commit on a given branch
     * @param commitUrl URL which points to the address of the commit API, example : "https://api.github.com/repos/mybatis/spring/commits{/sha}"
     * @param branchLastCommit Branch to get the last commit
     * @return JSONObject corresponding to the last commit according to the branch given
     * @throws InterruptedException
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject getLastCommitFromRepo(String commitUrl, String branchLastCommit) throws InterruptedException, IOException, URISyntaxException {
        if(commitUrl.contains("{/sha}")){
            if(!branchLastCommit.contains("/")){
                branchLastCommit = "/" + branchLastCommit;
            }
            commitUrl = commitUrl.replace("{/sha}", branchLastCommit);
            return getRawDataJson(commitUrl,new JSONObject());
        }
        return null;
    }


    public JSONObject getLastCommitForRepositoriesList(JSONObject jsonObjectListRepo) throws InterruptedException, IOException, URISyntaxException {
        JSONArray jsonArrayItems = jsonObjectListRepo.getJSONArray("items");

        for (int i = 0; i < jsonArrayItems.length(); i++) {

            LoggerPrintUtils.printLaunchBar(logger,"==< Association of commit and repositories >==",i+1,jsonArrayItems.length());
            JSONObject commit = new JSONObject();

            if(jsonArrayItems.get(i) instanceof JSONObject){
                JSONObject jsonObjectRepo = jsonArrayItems.getJSONObject(i);

                JSONObject jsonObjectResponseCommit = getLastCommitFromRepo(jsonObjectRepo.getString("commits_url"),"master");

                if (jsonObjectResponseCommit.has("sha"))
                    commit.put("sha", jsonObjectResponseCommit.get("sha"));
                if(jsonObjectResponseCommit.has("html_url"))
                    commit.put("html_url", jsonObjectResponseCommit.get("html_url"));

                jsonObjectRepo.put("last_commit",commit);
            }
            else{
                commit.put("unknown_type",jsonArrayItems.get(i).toString());
            }
        }

        return jsonObjectListRepo;
    }

    public JSONObject getLastCommitForRepositoriesList(JSONObject jsonObjectListRepo, FileWriterJSON fileWriterJSON, String path, String filename) throws InterruptedException, IOException, URISyntaxException {
        JSONArray jsonArrayItems = jsonObjectListRepo.getJSONArray("items");

        for (int i = 0; i < jsonArrayItems.length(); i++) {

            LoggerPrintUtils.printLaunchBar(logger,"==< Association of commit and repositories >==",i+1,jsonArrayItems.length());
            JSONObject commit = new JSONObject();

            if(jsonArrayItems.get(i) instanceof JSONObject){
                JSONObject jsonObjectRepo = jsonArrayItems.getJSONObject(i);

                JSONObject jsonObjectResponseCommit = getLastCommitFromRepo(jsonObjectRepo.getString("commits_url"),"master");

                if (jsonObjectResponseCommit.has("sha"))
                    commit.put("sha", jsonObjectResponseCommit.get("sha"));
                if(jsonObjectResponseCommit.has("html_url"))
                    commit.put("html_url", jsonObjectResponseCommit.get("html_url"));

                jsonObjectRepo.put("last_commit",commit);
            }
            else{
                commit.put("unknown_type",jsonArrayItems.get(i).toString());
            }

            fileWriterJSON.writeJsonFile(jsonObjectListRepo,path,filename);
        }

        return jsonObjectListRepo;
    }

    /**
     * Replace specifically  the char '<' and '>' by the encoding char in HTML format
     * @param urlToEncodeString String url to encode
     * @return String the URL encoded
     */
    public String urlEncodeSpecificChars(String urlToEncodeString){
        return urlToEncodeString.replace(">","%3E").replace("<","%3C");
    }

    /**
     * Getter for the httpClient
     * @return CloseableHttpClient
     */
    public CloseableHttpClient getHttpClient(){
        return httpClient;
    }

    /**
     * Setter for the httpClient
     * @param httpClient CloseableHttpClient
     */
    public void setHttpClient(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }
}
