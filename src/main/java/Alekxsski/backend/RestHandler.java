package Alekxsski.backend;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Response;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static org.asynchttpclient.Dsl.*;

public class RestHandler {

    private AsyncHttpClient client = null;

    private String DiscordApiBaseUrl;

    public RestHandler(){}

    private AsyncHttpClient getClient(){

        if (client == null){

            client = asyncHttpClient(config()
                    .setConnectTimeout(Duration.ofSeconds(5))
                    .setRequestTimeout(Duration.ofSeconds(30))
                    .setMaxConnections(500)
                    .setMaxConnectionsPerHost(100)
                    .setCompressionEnforced(true));

            System.out.println("Client initialized");
        }

        return client;

    }

    public Optional<Response> makeRequest(String method, String url, Map<CharSequence,String>headers, Map<String,String>params, String json) {

            Response response = null;

            try(AsyncHttpClient client = getClient()){


                BoundRequestBuilder preparedResponse = client.prepare(method,fullUrl(url));

                if (!headers.isEmpty()) headers.forEach(preparedResponse::addHeader);

                if(!params.isEmpty()) params.forEach(preparedResponse::addQueryParam);

                if(json != null) preparedResponse.setBody(json);

                response = preparedResponse.execute().get();

            }
            catch (Exception e){

                System.out.println(e);

            }

            return Optional.ofNullable(response);

    }

    private String fullUrl(String url){

        return String.format("%s%s",DiscordApiBaseUrl,url);


    }

}
