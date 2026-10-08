package Alekxsski.Utils.DiscordAuth;

import Alekxsski.LuckPerms.LuckPermsManager;
import Alekxsski.LuckPerms.LuckPlayerData;
import Alekxsski.Utils.PlayerBased.KyoriAdevntureApi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.Interfaces.ReloadBehaviour;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthData;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMap;
import com.velocitypowered.api.proxy.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
//Class handles url requests sends them to db and stores them locally
public class DiscordAuth implements ReloadBehaviour {

    private final Config config;
    private final DatabaseDiscordMethods discordAuthData;
    private final ConcurrentHashMap<UUID, DiscordAuthData> discordAuthDataHashMap;
    private final ConcurrentHashMap<String, UUID> discordAuthCodeHashMap;
    private final LuckPermsManager luckPermsManager;

    private final String DiscordApiBaseUrl;

    private final String RedirectUrl;

    private final String ClientId;

    private String VerifyCodeVarName;

    private String VerifyUUIDVarName;

    private String VerifyName;

    private String ErrorCodeMessage;

    private final boolean RedirectExternally;

    private final boolean DiscordOAuth2;


    private String ClickMessage;
    private String BeforeClickMessage;

    @Inject
    public DiscordAuth(Config config, DatabaseDiscordMethods databaseDiscordMethods, DiscordAuthHashMap discordAuthHashMap, LuckPermsManager luckPermsManager){

        this.config = config;
        this.discordAuthDataHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();
        this.discordAuthCodeHashMap = discordAuthHashMap.getDiscordAuthCodeHashMap();
        this.discordAuthData = databaseDiscordMethods;
        this.luckPermsManager = luckPermsManager;

        this.DiscordApiBaseUrl = config.getDiscordApiBaseUrl();

        this.RedirectUrl = config.getRedirectUrl();

        this.ClientId = config.getClientId();

        this.DiscordOAuth2 = config.isDiscordOAuth2();

        this.RedirectExternally = config.isRedirectExternally();

        setUp();

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

        VerifyCodeVarName = config.getVerifyCodeVarName();

        VerifyUUIDVarName = config.getVerifyUUIDVarName();

        VerifyName = config.getVerifyName();

        ClickMessage = config.getClickMessage();

        BeforeClickMessage = config.getBeforeClickMessage();

        ErrorCodeMessage = config.getErrorCodeMessage();

    }

    public @NonNull String MakeUrl(Player player ){

        UUID player_uuid = player.getUniqueId();

            //If player has stored code return them
            if (discordAuthDataHashMap.containsKey(player_uuid)){

                return discordAuthDataHashMap.get(player_uuid).getAuth_url();

            }

            String code = generateCode();

            String url = getUrl(player_uuid, code);

            //store code locally
            discordAuthDataHashMap.put(player_uuid,new DiscordAuthData(url,code));

                if(DiscordOAuth2 && RedirectExternally) {

                    LuckPlayerData playerData = luckPermsManager.getActivePlayerData(player_uuid);

                        if(playerData != null){

                            //store data in db
                            discordAuthData.executeStatement("INSERT INTO auth_codes(uuid ,username ,primary_group ,code) VALUES (?, ?, ?, ?)",
                                    List.of(player_uuid.toString(), playerData.playerName(), playerData.primaryGroup(), code),null);

                        }

                        else{

                            player.sendMessage(KyoriAdevntureApi.MinimessagePlain(ErrorCodeMessage));

                        }



                } else if (DiscordOAuth2) {

                    //store code if webapi will be used
                    discordAuthCodeHashMap.put(code,player_uuid);

                }

        return url;

    }

    private @NonNull String getUrl(UUID player_uuid, String code) {

        String click_string = String.format("/%s %s:%s %s:%s ", VerifyName, VerifyUUIDVarName , player_uuid, VerifyCodeVarName, code);

        String action = "copy_to_clipboard";

        if (DiscordOAuth2) {

            click_string = String.format("%soauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=identify%%20guilds.join&state=%s",DiscordApiBaseUrl, ClientId, RedirectUrl, code);
            action = "open_url";

        }


        return String.format("%s<click:%s:'%s'>%s</click>",BeforeClickMessage,action, click_string, ClickMessage);

    }

    private String generateCode(){

        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16);
    }


}
