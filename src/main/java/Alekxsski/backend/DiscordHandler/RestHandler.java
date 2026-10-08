package Alekxsski.backend.DiscordHandler;
import Alekxsski.Utils.Config;
import com.google.inject.Inject;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


//Class provides most of necessary functionalities that would be needed to exchange tokens with discord or other api
public class RestHandler {

    private HttpClient httpClient = null;

    private final String DiscordApiBaseUrl;

    private final Logger logger;

    @Inject
    public RestHandler(Config config, Logger logger){

        this.DiscordApiBaseUrl = config.getDiscordApiBaseUrl();
        this.logger = logger;

    }

    //Creation of the AsyncHttpClient for application
    private HttpClient getClient(){

        if (httpClient == null){

            httpClient = HttpClient.newHttpClient();

            System.out.println("Client initialized");
        }

        return httpClient;

    }

    //Method provides interface to establish requests with api
    public HttpResponse<String> makeRequest(String method, String url, String[] headers, String body) throws InterruptedException, IOException {

        HttpClient httpClient = getClient();

        HttpRequest request = HttpRequest.newBuilder()
                .method(method, body != null ?  HttpRequest.BodyPublishers.ofString(body): HttpRequest.BodyPublishers.noBody())
                .uri(fullUrl(url))
                .headers(headers)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int retires = 0;

        while (response.statusCode() == 429 && retires < 3){

            retires++;
            Optional<String> wait = response.headers().firstValue("Retry-After");

            if(wait.isPresent()){

                double seconds = Double.parseDouble(wait.get());
                TimeUnit.MILLISECONDS.sleep((long) (seconds * 1000));

            }

            else TimeUnit.SECONDS.sleep(5L);

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        }

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    }

    //Fix api route
    private URI fullUrl(String url){

        return URI.create(String.format("%sv10/%s",DiscordApiBaseUrl,url));

    }

    private void debugResponse(HttpRequest request){

        logger.info("Method: {}", request.method());
        logger.info("URL: {}", request.uri());
        logger.info("Headers: {}", request.headers());

    }

}
