package Alekxsski.backend.DiscordHandler;
import Alekxsski.Utils.Config;
import com.google.inject.Inject;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.slf4j.Logger;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.asynchttpclient.Dsl.*;

//Class provides most of necessary functionalities that would be needed to exchange tokens with discord or other api
public class RestHandler {

    private AsyncHttpClient client = null;

    private HttpClient httpClient = null;

    private final String DiscordApiBaseUrl;

    private final Logger logger;

    @Inject
    public RestHandler(Config config, Logger logger){

        this.DiscordApiBaseUrl = config.getDiscordApiBaseUrl();
        this.logger = logger;

    }

    //Creation of the AsyncHttpClient for application
    private AsyncHttpClient getClient(){

        if (client == null){

            client = asyncHttpClient(config()
                    .setConnectTimeout(Duration.ofSeconds(5))
                    .setRequestTimeout(Duration.ofSeconds(10))
                    .setMaxConnections(50)
                    .setMaxConnectionsPerHost(10)
                    .setCompressionEnforced(true));

            System.out.println("Client initialized");
        }

        return client;

    }

    private HttpClient getClient_(){

        if (httpClient == null){

            httpClient = HttpClient.newHttpClient();

            System.out.println("Client initialized");
        }

        return httpClient;

    }

    //Method provides interface to establish requests with api
    public Optional<Response> makeRequest(String method, String url, Map<CharSequence,String>headers, Map<String,String>params, String body) throws ExecutionException, InterruptedException {

            Response response;

            AsyncHttpClient client = getClient();


                BoundRequestBuilder preparedResponse = client.prepare(method,fullUrl(url));

                if (headers != null) headers.forEach(preparedResponse::addHeader);

                if(params != null) params.forEach(preparedResponse::addQueryParam);

                if(body != null) preparedResponse.setBody(body);

                response = preparedResponse.execute().get();


            return Optional.ofNullable(response);

    }

    public void makeRequest_(String method, String url, Map<CharSequence,String>headers, Map<String,String>params, String body) throws ExecutionException, InterruptedException {

        //HttpClient httpClient = getClient_();

        //HttpRequest.BodyPublishers.

        //HttpRequest request = HttpRequest.newBuilder().method(method,body)

       // httpClient.send()

    }

    //Fix api route
    private String fullUrl(String url){

        return String.format("%sv10/%s",DiscordApiBaseUrl,url);

    }

    private void debugResponse(Request request){

        logger.info("Method: {}", request.getMethod());
        logger.info("URL: {}", request.getUrl());
        logger.info("Headers: {}", request.getHeaders());
        logger.info("Body: {}", request.getStringData());

    }

}
