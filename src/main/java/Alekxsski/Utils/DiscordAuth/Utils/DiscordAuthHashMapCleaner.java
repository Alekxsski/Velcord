package Alekxsski.Utils.DiscordAuth.Utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.Interfaces.ReloadBehaviour;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.PlayerBased.Minimessage;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DiscordAuthHashMapCleaner implements ReloadBehaviour {

    private final ConcurrentHashMap<UUID, DiscordAuthData> discordAuthDataHashMap;

    private final ConcurrentHashMap<String, UUID> discordCodeHashMap;

    private final DatabaseDiscordMethods databaseDiscordMethods;

    private final ProxyServer server;

    private final Config config;

    private String ExpiredCodeMessage;

    private final boolean DiscordOAuth2;

    private final boolean RedirectExternally;

    @Inject
    public DiscordAuthHashMapCleaner( ProxyServer server, DatabaseDiscordMethods databaseDiscordMethods, DiscordAuthHashMap discordAuthHashMap, Config config){

        this.discordAuthDataHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();

        this.discordCodeHashMap = discordAuthHashMap.getDiscordAuthCodeHashMap();

        this.databaseDiscordMethods = databaseDiscordMethods;

        this.server = server;

        this.config = config;

        this.DiscordOAuth2 = config.isDiscordOAuth2();

        this.RedirectExternally = config.isRedirectExternally();

    }

    public void setUp(){

        gettingData();

    }

    @Override
    public void reload() {

        gettingData();

    }

    @Override
    public void gettingData() {

        ExpiredCodeMessage = config.getExpiredCodeMessage();

    }


    public void clean(){

            long timenow = Instant.now().getEpochSecond();

            for(Iterator<Map.Entry<UUID, DiscordAuthData>> it = discordAuthDataHashMap.entrySet().iterator(); it.hasNext(); ) {

                Map.Entry<UUID, DiscordAuthData> entry = it.next();

                if(entry.getValue().getTime() <= timenow) {

                    UUID uuid = entry.getKey();

                    Optional<Player> player = server.getPlayer(uuid);

                    if(DiscordOAuth2 && RedirectExternally){
                        
                        databaseDiscordMethods.executeStatement("SELECT * FROM player_discord WHERE uuid = ?",List.of(uuid.toString()),"discord_user_id")
                                .thenAccept(discord_user_id ->{

                                    if (discord_user_id == null){

                                        databaseDiscordMethods.executeStatement("DELETE FROM auth_codes WHERE uuid = ?",List.of(uuid.toString()),null)
                                                .thenRun(() -> player.ifPresent(p-> p.sendMessage(Minimessage.MinimessagePlain(ExpiredCodeMessage))));

                                    }
                                });

                        it.remove();
                        return;

                    } else if (DiscordOAuth2) {

                        discordCodeHashMap.remove(entry.getValue().getCode());
                        
                    }

                    player.ifPresent(p-> p.sendMessage(Minimessage.MinimessagePlain(ExpiredCodeMessage)));

                    it.remove();

                }
            }

    }

}
