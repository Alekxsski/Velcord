package Alekxsski.backend.DiscordHandler;

import Alekxsski.Utils.Config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import Alekxsski.backend.DiscordHandler.DataUtils.ResponseData;

import java.io.IOException;
import java.net.http.HttpResponse;

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
    public ResponseData getUserAccessToken(String authorizationCode) throws InterruptedException, IOException {

        String[] headers = {"Content-Type", "application/x-www-form-urlencoded"};

        String body = String.format(
                "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s",
                ClientId,
                ClientSecret,
                authorizationCode,
                RedirectUrl
        );

        HttpResponse<String> response = restHandler.makeRequest("POST","oauth2/token", headers,body);


        return readJson(response.statusCode(),response.body(),"access_token");



    }

    //Using users access token method returns member id that's required for further user management
    public ResponseData getMemberId(String UserAuthorizationToken) throws InterruptedException, IOException {

        String[] headers = {"Authorization", String.format("Bearer %s",UserAuthorizationToken)};


        HttpResponse<String> response = restHandler.makeRequest("GET","users/@me", headers,"");



        return readJson(response.statusCode(),response.body(),"id");


    }

    private ResponseData readJson(int code,String json, String valueName) throws JsonProcessingException {

        JsonNode jsonNode = mapper.readTree(json);

        if(code == 200){

            return new ResponseData(code,jsonNode.get(valueName).asText());

        }

        return new ResponseData(code,jsonNode.get("message").asText());


    }

}
