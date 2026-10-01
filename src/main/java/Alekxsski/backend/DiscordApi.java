package Alekxsski.backend;

import Alekxsski.Utils.Config;
import com.google.inject.Inject;
import org.asynchttpclient.Response;
import Alekxsski.backend.DataUtils.ResponseData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DiscordApi {

    private final Config config;
    private final RestHandler restHandler;

    private String ClientId;
    private String ClientSecret;
    private String RedirectUrl;
    private String BotSecret;
    private String GuildId;
    private String DiscordApiBaseUrl;



    @Inject
    public DiscordApi(Config config,RestHandler restHandler){

        this.config = config;
        this.restHandler = restHandler;
        setUp();

    }

    private void setUp(){

        ClientId = config.getClientId();
        ClientSecret = config.getClientSecret();
        RedirectUrl = config.getRedirectUrl();
        BotSecret = config.getBotToken();
        GuildId = config.getGuildId();
        DiscordApiBaseUrl = config.getDiscordApiBaseUrl();

    }

    public ResponseData getUserAccessToken(String authorizationCode){

        Map<CharSequence,String> headers = Map.of("Content-Type", "application/x-www-form-urlencoded");

        Map<String,String> params = new HashMap<>();

        params.put("client_id",ClientId);
        params.put("client_secret",ClientSecret);
        params.put("grant_type","authorization_code");
        params.put("code",authorizationCode);
        params.put("redirect_uri",RedirectUrl);

        Optional<Response> responseOptional = restHandler.makeRequest("POST","oauth2/token", headers,params,null);

        if(responseOptional.isPresent()){

            Response response = responseOptional.get();

            return new ResponseData(response.getStatusCode(),response.getResponseBody());



        }

        return null;

    }

}
