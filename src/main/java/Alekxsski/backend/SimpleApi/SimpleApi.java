package Alekxsski.backend.SimpleApi;

import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthData;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMap;
import Alekxsski.backend.DiscordHandler.DiscordUserManager;
import com.google.inject.Inject;
import io.javalin.Javalin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



public class SimpleApi {

    private Javalin simpleApiJavalin;
    private final DiscordUserManager discordUserManager;
    private final Config config;
    private final ConcurrentHashMap<UUID, DiscordAuthData> discordAuthHashMap;

    @Inject
    public SimpleApi(DiscordUserManager discordUserManager, Config config, DiscordAuthHashMap discordAuthHashMap){

        this.discordUserManager = discordUserManager;

        this.config = config;

        this.discordAuthHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();

    }

    private void setUp(){

        //Callback api extension
        simpleApiJavalin = Javalin.create(config1 -> {
            config1.routes.get("/callback", ctx -> {

                String code = ctx.queryParam("code");
                String state = ctx.queryParam("state");

                ctx.future(() ->{

                    // If one of the queries is null return 400 status code and error message
                    if(state == null || code == null) {
                        ctx.status(400).result("Once of the required values wasn't provided!");
                        return null;
                    }

                    // Check for necessary UUID in existing hashmap to avoid expensive async operation with discord api.
                    if(discordAuthHashMap.containsKey(UUID.fromString(state))){

                        return discordUserManager.HandleOAuth2(code,UUID.fromString(state))
                                .thenAccept(responseData -> {

                                    if (responseData.code() == 200){

                                        ctx.redirect(responseData.value());

                                    }
                                    else{

                                        ctx.status(responseData.code()).result(responseData.value());

                                    }

                                })
                                .exceptionally(ex ->{

                                    ctx.status(500).result("Internal error" + ex.getMessage());
                                    return null;

                                });

                    }

                    // User UUID wasn't found return 404 status code and error message
                    ctx.status(404).result("Provided data isn't correct please contact administrators!");
                    return null;

                });


            });
        });

    }

    public void simpleApiHook(){

        //Start javalin server if needed and set api port
        if(!config.isRedirectExternally() && config.isDiscordOAuth2()){

            setUp();
            simpleApiJavalin.start("0.0.0.0", config.getWebApiPort());

        }


    }

    public void stop(){

        if(simpleApiJavalin != null) simpleApiJavalin.stop();

    }


}
