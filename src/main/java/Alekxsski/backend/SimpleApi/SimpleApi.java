package Alekxsski.backend.SimpleApi;

import Alekxsski.ProxyManager.ProxyManagement;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthData;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMap;
import Alekxsski.Utils.PlayerBased.KyoriAdevntureApi;
import Alekxsski.Utils.PlayerBased.SoundsEnum;
import Alekxsski.backend.DiscordHandler.DiscordUserManager;
import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import io.javalin.Javalin;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

//Class designed to handle api requests from discord and manages interface between user and server
public class SimpleApi {

    private Javalin simpleApiJavalin;
    private final DiscordUserManager discordUserManager;
    private final Config config;
    private final ProxyManagement proxyManagement;
    private final ConcurrentHashMap<String, UUID> discordCodeHashMap;
    private final ConcurrentHashMap<UUID, DiscordAuthData> discordAuthHashMap;
    private final KyoriAdevntureApi adventureApi;

    @Inject
    public SimpleApi(DiscordUserManager discordUserManager, Config config, DiscordAuthHashMap discordAuthHashMap,
                     ProxyManagement proxyManagement, KyoriAdevntureApi adventureApi){

        this.discordUserManager = discordUserManager;

        this.config = config;

        this.discordCodeHashMap = discordAuthHashMap.getDiscordAuthCodeHashMap();

        this.discordAuthHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();

        this.proxyManagement = proxyManagement;

        this.adventureApi = adventureApi;

    }

    private void setUp() throws ClassNotFoundException {

        Class.forName("com.Alexsski.libs.jetty.io.ManagedSelector$CloseConnections");

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

                        //remove code from hashmap
                        discordCodeHashMap.remove(state);

                        //remove player from hashmap
                        discordAuthHashMap.remove(player_uuid);

                        //handle OAuth2
                        return discordUserManager.HandleOAuth2(code,player_uuid)
                                .thenAccept(responseData -> {

                                    Player player = proxyManagement.getPlayer(player_uuid);

                                    //send player message in mc about status of action
                                    if (player != null) {

                                        adventureApi.PlaySoundVerication(player, responseData.code() == 200 ? SoundsEnum.SUCCESS : SoundsEnum.FAIL);
                                        player.sendMessage(KyoriAdevntureApi.MinimessagePlain( responseData.code() == 200 ? config.getVerifySuccess() : config.getErrorCodeMessage()));

                                    }

                                    if (responseData.code() == 200){

                                        ctx.redirect(responseData.value());

                                    }
                                    else{

                                        ctx.status(responseData.code()).result(responseData.value());

                                    }

                                })
                                .exceptionally(ex ->{

                                    Player player = proxyManagement.getPlayer(player_uuid);

                                    //send player message in mc about status of action
                                    if (player != null) {

                                        adventureApi.PlaySoundVerication(player, SoundsEnum.CRITICAL);
                                        player.sendMessage(KyoriAdevntureApi.MinimessagePlain(config.getErrorCodeMessage()));

                                    }

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

    public void simpleApiHook() throws ClassNotFoundException {

        //Start javalin server if needed and set api port
        if(!config.isRedirectExternally() && config.isDiscordOAuth2()){

            setUp();
            simpleApiJavalin.start(config.getWebApiHost(), config.getWebApiPort());

        }


    }

    public void stop(){

        if(simpleApiJavalin != null) {
            discordUserManager.stop();
            simpleApiJavalin.stop();
        }

    }


}
