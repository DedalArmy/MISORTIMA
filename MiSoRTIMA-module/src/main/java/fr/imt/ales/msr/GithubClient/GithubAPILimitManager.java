package fr.imt.ales.msr.GithubClient;

import fr.imt.ales.msr.LoggerUtils.LoggerPrintUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GithubAPILimitManager {
    final static Logger logger = LogManager.getLogger(GithubAPILimitManager.class);

    public GithubAPILimitManager(){}

    /**
     * Sleep the main Thread according to the rate limit remaining and the rate limit timestamp provided by the GitHub API
     * @param timestampRateLimitReset Long timestamp rate limit reset (corresponds to HTTP Header field : X-RateLimit-Reset)
     * @param rateLimitRemaining Integer rate limit remaining (corresponds to HTTP Header field : X-RateLimit-Remaining)
     * @throws InterruptedException Exception thrown when a problem occurred with the Thread
     */
    public long handleLimitAPIGithub(Long timestampRateLimitReset, Integer rateLimitRemaining) throws InterruptedException {
        if(rateLimitRemaining == 0){
            //Create the time diff between the timestamp returned by Github in the headers and the timestamp obtained from the local system
            long sleepTime = Math.abs(timestampRateLimitReset - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + 3)*1000;
            logger.info("==< GitHub API Limit reached >==");
            logger.info("WAIT : "+ sleepTime + " milliseconds before next request");
            logger.info("==< The program is not freeze, it's just a thread sleep ;-) >==");
            Thread.sleep(sleepTime);
            return sleepTime;
        }
        return 0;
    }

    /**
     * Display the JSON Object returned after a request on Github with the URI "/rate_limit" to know
     * the rate limits to request the API
     * @param responseBodyRateLimit JSONObject containing the response
     */
    public void printRateLimitAPI(JSONObject responseBodyRateLimit){
        if(responseBodyRateLimit == null)
            throw new NullPointerException("The JSON object for the rate limit cannot be null");

        JSONObject jsonObjectCore = responseBodyRateLimit.getJSONObject("resources").getJSONObject("core");
        JSONObject jsonObjectSearch = responseBodyRateLimit.getJSONObject("resources").getJSONObject("search");

        Map<String, Object> mapLimit = new LinkedHashMap<>();

        int coreLimit = jsonObjectCore.getInt("limit");
        int coreRemaining = jsonObjectCore.getInt("remaining");
        Integer coreReset = jsonObjectCore.getInt("reset");
        Date dateResetCore  = new Timestamp(coreReset);

        mapLimit.put("Requests on core API limit", Integer.toString(coreLimit));
        mapLimit.put("Requests on core API remaining", Integer.toString(coreRemaining));
        mapLimit.put("Reset core API at ", dateResetCore.toString());

        int searchLimit = jsonObjectSearch.getInt("limit");
        int searchRemaining = jsonObjectSearch.getInt("remaining");
        Integer searchReset = jsonObjectSearch.getInt("reset");
        Date dateResetSearch  = new Timestamp(searchReset);

        mapLimit.put("Requests on search API limit", Integer.toString(searchLimit));
        mapLimit.put("Requests on search API remaining", Integer.toString(searchRemaining));
        mapLimit.put("Reset search API at ", dateResetSearch.toString());

        LoggerPrintUtils.printMapAsTable(logger,  mapLimit);
    }

}
