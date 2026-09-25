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

@Singleton
public class DiscordAuthHashMapCleaner implements ReloadBehaviour {

    private final HashMap<UUID, DiscordAuthData> discordAuthDataHashMap;

    private final DatabaseDiscordMethods databaseDiscordMethods;

    private final ProxyServer server;

    private final Config config;

    private String ExpiredCodeMessage;

    private Boolean DiscordOAuth2;

    @Inject
    public DiscordAuthHashMapCleaner( ProxyServer server, DatabaseDiscordMethods databaseDiscordMethods, DiscordAuthHashMap discordAuthHashMap, Config config){

        this.discordAuthDataHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();

        this.databaseDiscordMethods = databaseDiscordMethods;

        this.server = server;

        this.config = config;

        gettingData();

    }

    @Override
    public void reload() {

        gettingData();

    }

    @Override
    public void gettingData() {

        ExpiredCodeMessage = config.getExpiredCodeMessage();
        DiscordOAuth2 = config.getDiscordOAuth2();

    }


    public void start(){

            long timenow = Instant.now().getEpochSecond();

            for(Iterator<Map.Entry<UUID, DiscordAuthData>> it = discordAuthDataHashMap.entrySet().iterator(); it.hasNext(); ) {

                Map.Entry<UUID, DiscordAuthData> entry = it.next();

                if(entry.getValue().getTime() <= timenow) {

                    UUID uuid = entry.getKey();

                    Optional<Player> player = server.getPlayer(uuid);

                    if(DiscordOAuth2){



                        databaseDiscordMethods.executeStatement("SELECT * FROM player_discord WHERE uuid = ?",List.of(uuid.toString()),"discord_user_id")
                                .thenAccept(discord_user_id ->{

                                    if (discord_user_id == null){

                                        databaseDiscordMethods.executeStatement("DELETE FROM auth_codes WHERE uuid = ?",List.of(uuid.toString()),null)
                                                .thenRun(() -> player.ifPresent(p->{

                                                    p.sendMessage(Minimessage.MinimessagePlain(ExpiredCodeMessage));

                                                }));

                                    }
                                });

                        it.remove();
                        return;
                    }

                    player.ifPresent(p->{

                        p.sendMessage(Minimessage.MinimessagePlain(ExpiredCodeMessage));

                    });

                    it.remove();

                }
            }

    }

}
