package Alekxsski.backend.SimpleApi;

import Alekxsski.ProxyManager.ProxyManagement;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthData;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMap;
import Alekxsski.backend.DiscordHandler.DiscordUserManager;
import com.google.inject.Inject;
import io.javalin.Javalin;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;



public class SimpleApi {

    private Javalin simpleApiJavalin;
    private final DiscordUserManager discordUserManager;
    private final Config config;
    private final ProxyManagement proxyManagement;
    private final ConcurrentHashMap<String, UUID> discordCodeHashMap;
    private final ConcurrentHashMap<UUID, DiscordAuthData> discordAuthHashMap;

    @Inject
    public SimpleApi(DiscordUserManager discordUserManager, Config config, DiscordAuthHashMap discordAuthHashMap, ProxyManagement proxyManagement){

        this.discordUserManager = discordUserManager;

        this.config = config;

        this.discordCodeHashMap = discordAuthHashMap.getDiscordAuthCodeHashMap();

        this.discordAuthHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();

        this.proxyManagement = proxyManagement;


    }

    private void setUp(){

        //Callback api extension
        simpleApiJavalin = Javalin.create(config1 -> {

            config1.concurrency.useVirtualThreads = true;

            config1.routes.get("/callback", ctx -> {

                String code = ctx.queryParam("code");
                String state = ctx.queryParam("state");

                ctx.future(() ->{

                    // If one of the queries is null return 400 status code and error message
                    if(state == null || code == null) {
                        ctx.status(400).result("One of the required values wasn't provided!");
                        return CompletableFuture.completedFuture(null);

                    }

                    // Check for necessary UUID in existing hashmap to avoid expensive async operation with discord api.
                    if(discordCodeHashMap.containsKey(state)){

                        UUID player_uuid = discordCodeHashMap.get(state);

                        if(!proxyManagement.isPlayerActive(player_uuid)){

                            ctx.status(401).result("You have to be on minecraft server to use OAuth2");
                            return CompletableFuture.completedFuture(null);


                        }

                        discordCodeHashMap.remove(state);

                        discordAuthHashMap.remove(player_uuid);

                        return discordUserManager.HandleOAuth2(code,player_uuid)
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
                    return CompletableFuture.completedFuture(null);


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

        if(simpleApiJavalin != null) {
            discordUserManager.stop();
            simpleApiJavalin.stop();
        }

    }


}
