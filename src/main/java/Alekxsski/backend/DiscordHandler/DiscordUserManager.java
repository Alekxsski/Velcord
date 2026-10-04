package Alekxsski.backend.DiscordHandler;

import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.LuckPerms.LuckPermsManager;
import Alekxsski.LuckPerms.LuckPlayerData;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import Alekxsski.Utils.Config;
import Alekxsski.backend.DiscordHandler.DataUtils.ResponseData;
import com.google.inject.Inject;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Class manages OAuth2 flow and on that basis invites user to the guild grants roles and valid minecraft nickname
public class DiscordUserManager {

    private final RolesManager rolesManager;
    private final DiscordApi discordApi;
    private final Logger logger;
    private final ExecutorService DiscordUserManagerThreads;
    private final  LuckPermsManager luckPermsManager;
    private final DatabaseDiscordMethods databaseDiscordMethods;


    @Inject
    public DiscordUserManager(RolesManager rolesManager, DiscordApi discordApi, Logger logger, Config config,
                              LuckPermsManager luckPermsManager, DatabaseDiscordMethods databaseDiscordMethods){

        this.discordApi = discordApi;
        this.rolesManager = rolesManager;
        this.logger = logger;
        this.DiscordUserManagerThreads = Executors.newFixedThreadPool(config.getMaximumPoolSize());
        this.luckPermsManager = luckPermsManager;
        this.databaseDiscordMethods = databaseDiscordMethods;

    }

    public CompletableFuture<ResponseData> HandleOAuth2(String code, UUID player_uuid){

        return CompletableFuture.supplyAsync(()->{

            ResponseData response;

            try {
                response = discordApi.getUserAccessToken(code);

                if (response.code() != 200){

                    return new ResponseData(response.code(), "There was issue with obtaining access token please contact administrators");

                }

                response = discordApi.getMemberId(response.value());

                logger.info(response.value());

                LuckPlayerData luckPlayerData = luckPermsManager.getActivePlayerData(player_uuid);

                if(response.code() != 200){

                    databaseDiscordMethods.executeStatement("INSERT INTO player_discord (uuid, discord_user_id) VALUES (?, ?)",
                            List.of(player_uuid.toString(), response.value()),null);

                    rolesManager.checkMember(response.value(), luckPlayerData.playerName(), luckPlayerData.primaryGroup());

                }

                return new ResponseData(response.code(), response.code() == 200 ? "https://discord.com/channels/@me" : "There was issue with obtaining access token please contact administrators");

            } catch (Exception e) {

                throw new RuntimeException(e);
            }


        },DiscordUserManagerThreads);

    }

}
