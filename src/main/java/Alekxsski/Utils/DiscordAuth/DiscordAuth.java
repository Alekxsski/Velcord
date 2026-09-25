package Alekxsski.Utils.DiscordAuth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.Interfaces.ReloadBehaviour;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthData;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMap;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Singleton
public class DiscordAuth implements ReloadBehaviour {

    private final Config config;
    private final DatabaseDiscordMethods discordAuthData;
    private final HashMap <UUID, DiscordAuthData> discordAuthDataHashMap;

    private String DiscordApiBaseUrl;

    private String RedirectUrl;

    private String ClientId;

    private String VerifyCodeVarName;

    private String VerifyUUIDVarName;

    private String VerifyName;

    private boolean DiscordOAuth2;


    private String ClickMessage;
    private String BeforeClickMessage;

    @Inject
    public DiscordAuth(Config config, DatabaseDiscordMethods databaseDiscordMethods, DiscordAuthHashMap discordAuthHashMap){

        this.config = config;
        this.discordAuthDataHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();
        this.discordAuthData = databaseDiscordMethods;

        gettingData();

    }

    @Override
    public void reload() {

        gettingData();

    }

    @Override
    public void gettingData() {

        DiscordApiBaseUrl = config.getDiscordApiBaseUrl();

        RedirectUrl = config.getRedirectUrl();

        ClientId = config.getClientId();

        DiscordOAuth2 = config.getDiscordOAuth2();

        VerifyCodeVarName = config.getVerifyCodeVarName();

        VerifyUUIDVarName = config.getVerifyUUIDVarName();

        VerifyName = config.getVerifyName();

        ClickMessage = config.getClickMessage();

        BeforeClickMessage = config.getBeforeClickMessage();

    }

    public String MakeUrl(UUID player_uuid){

            if (discordAuthDataHashMap.containsKey(player_uuid)){

                return discordAuthDataHashMap.get(player_uuid).getAuth_url();

            }

            String code = generateCode();

        String url = getUrl(player_uuid, code);

        discordAuthDataHashMap.put(player_uuid,new DiscordAuthData(url,code));

            if(DiscordOAuth2) discordAuthData.executeStatement("INSERT INTO auth_codes(uuid,code) VALUES (?, ?)", List.of(player_uuid.toString(), code),null);

            return url;

    }

    private @NonNull String getUrl(UUID player_uuid, String code) {

        String click_string = String.format("/%s %s:%s %s:%s ", VerifyName, VerifyUUIDVarName , player_uuid, VerifyCodeVarName, code);

        String action = "copy_to_clipboard";

        if (DiscordOAuth2) {

            click_string = String.format("%s%s&redirect_uri=%s&response_type=code&scope=identify%%20guilds.join&state=%s",DiscordApiBaseUrl, ClientId, RedirectUrl, code);
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
