package fr.imt.ales.msr.GithubClient;

import fr.imt.ales.msr.LoggerUtils.LaunchBar;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GithubHttpClient {
    private CloseableHttpClient httpClient;
    final static Logger logger = LogManager.getLogger(GithubHttpClient.class);

    public GithubHttpClient(){
        httpClient = HttpClients.createDefault();
    }

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

        //execute the first request
        HttpGet httpGet = new HttpGet(urlEncodeSpecificChars(URLStringApi));

        //Handle response body
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        String responseString = EntityUtils.toString(httpEntity, "UTF-8");

        //Get header for the next page
        Header linkHeader = httpResponse.getFirstHeader("Link");
        HeaderElement[] headerElementTabLink = linkHeader.getElements();

        //Get headers and values for API limitations
        Long timestampLimitResetHeader   = Long.parseLong(httpResponse.getFirstHeader("X-RateLimit-Reset").getValue());
        Integer rateLimitRemainingHeader = Integer.parseInt(httpResponse.getFirstHeader("X-RateLimit-Remaining").getValue());

        JSONObject jsonObjectResponse = new JSONObject(responseString);

        handleLimitAPIGithub(timestampLimitResetHeader, rateLimitRemainingHeader);

        for (HeaderElement headerElement : headerElementTabLink) {
            System.out.println(headerElement.toString());
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
        if(isFirstPage){
            jsonAllItems = jsonObjectResponse;
        }else{
            //concatenate list items
            jsonAllItems.getJSONArray("items").put(jsonObjectResponse.getJSONArray("items"));
        }

        //display launchbar
        LaunchBar.displayLaunchBar(logger,"Progress status request Github API" ,currentPageNumber,lastPageNumber);

        if(urlNextPageString == null || urlNextPageString.equals(""))
            return jsonAllItems;
        //Call recursively on the next page
        return getRawDataJson(urlNextPageString, jsonAllItems);
    }

    /**
     * Sleep the main Thread according to the rate limit remaining and the rate limit timestamp provided by the GitHub API
     * @param timestampRateLimitReset Long timestamp rate limit reset (corresponds to HTTP Header field : X-RateLimit-Reset)
     * @param rateLimitRemaining Integer rate limit remaining (corresponds to HTTP Header field : X-RateLimit-Remaining)
     * @throws InterruptedException Exception thrown when a problem occurred with the Thread
     */
    public long handleLimitAPIGithub(Long timestampRateLimitReset, Integer rateLimitRemaining) throws InterruptedException {
        if(rateLimitRemaining <= 0){
            long sleepTime = Math.abs(timestampRateLimitReset - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))*1000;
            logger.debug("");
            logger.debug("GitHub API Limit reached");
            logger.debug("WAIT : "+ sleepTime + " milliseconds before next request");
            Thread.sleep(sleepTime);
            return sleepTime;
        }
        return 0;
    }

    /**
     * Replace specifically '<' and '>' by the
     * @param urlToEncodeString
     * @return
     */
    public String urlEncodeSpecificChars(String urlToEncodeString){
        return urlToEncodeString.replace(">","%3E");
    }

    public CloseableHttpClient getHttpClient(){
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }
}
