
import fr.imt.ales.msr.GithubClient.GithubHttpClient;
import fr.imt.ales.msr.RawDataFilters.RawDataFilter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GithubHttpClient githubHttpClient = new GithubHttpClient();

        try {
            /*URI uri = new URIBuilder()
                    .setCharset(StandardCharsets.UTF_8)
                    .setScheme("https")
                    .setHost("api.github.com")
                    .setPath("/search/repositories")
                    .setParameter("q", "Spring+created:>=2010-01-01+language:java+stars:>=100+forks:>=20")
                    //.setParameter()
                    .build();
            JSONObject jsonObject = githubHttpClient.getRawDataJson("https://api.github.com/search/repositories?q=Spring+language:java+stars:>=100+created:>=2010-01-01+forks:>=20", new JSONObject());
            FileWriterJSON fileWriterJSON = new FileWriterJSON();
            //fileWriterJSON.writeJsonFile(jsonObject);
*/
            RawDataFilter rawDataFilter = new RawDataFilter();
            List<String> listFields = new ArrayList<>();
            rawDataFilter.extractSpecificFieldsFromJSONFile("file1.json", listFields);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

    }
}
