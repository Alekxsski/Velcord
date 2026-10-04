package Alekxsski.backend.DiscordHandler;

import Alekxsski.Utils.Config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import org.asynchttpclient.Response;
import Alekxsski.backend.DiscordHandler.DataUtils.ResponseData;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

//Class provide Human readable and consistent requests using RestHandler Class
public class DiscordApi {

    private final Config config;
    private final RestHandler restHandler;
    private final ObjectMapper mapper;

    private String ClientId;
    private String ClientSecret;
    private String RedirectUrl;



    @Inject
    public DiscordApi(Config config,RestHandler restHandler){

        this.mapper = new ObjectMapper();
        this.config = config;
        this.restHandler = restHandler;
        setUp();

    }

    private void setUp(){

        ClientId = config.getClientId();
        ClientSecret = config.getClientSecret();
        RedirectUrl = config.getRedirectUrl();

    }

    //Based on the validation of the Authorization code method returns status code of the request and access token
    public ResponseData getUserAccessToken(String authorizationCode) throws ExecutionException, InterruptedException, JsonProcessingException {

        Map<CharSequence,String> headers = Map.of("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format(
                "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s",
                ClientId,
                ClientSecret,
                authorizationCode,
                RedirectUrl
        );

        Optional<Response> responseOptional = restHandler.makeRequest("POST","oauth2/token", headers,null,body);

        if(responseOptional.isPresent()){

            Response response = responseOptional.get();

            if(response.getStatusCode() == 200){

                return readJson(response.getStatusCode(),response.getResponseBody(),"access_token");

            }

            return new ResponseData(response.getStatusCode(),response.getResponseBody());

        }

        return null;

    }

    //Using users access token method returns member id that's required for further user management
    public ResponseData getMemberId(String UserAuthorizationToken) throws ExecutionException, InterruptedException, JsonProcessingException {

        Map<CharSequence,String> headers = Map.of("Authorization", String.format("Bearer %s",UserAuthorizationToken));


        Optional<Response> responseOptional = restHandler.makeRequest("GET","users/@me", headers,null,null);


        if(responseOptional.isPresent()){

            Response response = responseOptional.get();

            return readJson(response.getStatusCode(),response.getResponseBody(),"id");

        }

        return null;

    }

    private ResponseData readJson(int code,String json, String valueName) throws JsonProcessingException {

        JsonNode jsonNode = mapper.readTree(json);

        if(code == 200){

            return new ResponseData(code,jsonNode.get(valueName).asText());

        }

        return new ResponseData(code,jsonNode.get("message").asText());


    }

}
