package Alekxsski.Utils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.route.Route;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Alekxsski.Interfaces.ReloadBehaviour;
import Alekxsski.ProxyManager.ProxyManagement;
import org.slf4j.Logger;

@Singleton
public class Config implements ReloadBehaviour {

    private YamlDocument config;

    private final Logger logger;

    private final ProxyManagement proxyManagement;

    private final Path dataDirectory;

    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private  int MaximumPoolSize;

    @Getter
    private  int MinimumIdle;

    @Getter
    private int IdleTimeout;

    @Getter
    private int MaxLifetime;

    @Getter
    private String DiscordApiBaseUrl;

    @Getter
    private String RedirectUrl;

    @Getter
    private String ClientId;

    @Getter
    private String ClickMessage;

    @Getter
    private String BeforeClickMessage;

    @Getter
    private String ExpiredCodeMessage;

    @Getter
    private String AlreadyConnectedMessage;

    @Getter
    private String ReloadMessage;

    @Getter
    private String NoPermission;

    @Getter
    private String NotConnectedMessage;

    @Getter
    private String HintUsage;

    @Getter
    private String DisconnectedMessage;

    @Getter
    private String Driver;

    @Getter
    private String DatabaseAddress;

    @Getter
    private String DatabasePort;

    @Getter
    private String DatabaseName;

    @Getter
    private Boolean DiscordOAuth2;

    @Getter
    private String BotToken;

    @Getter
    private String GuildId;

    @Getter
    private String VerifyCodeVarName;

    @Getter
    private String VerifyCodeVarDescription;

    @Getter
    private String VerifyUUIDVarName;

    @Getter
    private String VerifyUUIDVarDescription;

    @Getter
    private String VerifyName;

    @Getter
    private String VerifyDescription;

    @Getter
    private String VerifyNoCode;

    @Getter
    private String VerifyInvalidCode;

    @Getter
    private String VerifySuccessCode;

    @Getter
    private Map<String,String> RolesToSync;

    @Getter
    private Boolean DefaultRole;

    @Getter
    private Boolean UserLeaveNickname;

    @Getter
    private Long SyncTimeOut;

    @Inject
    public Config(Logger logger, ProxyManagement proxyManagement, @DataDirectory Path dataDirectory){

        this.logger = logger;

        this.proxyManagement = proxyManagement;

        this.dataDirectory = dataDirectory;

        make();

        gettingData();

        getStaticValues();

        logger.info("Data was successfully retrieved from config.yml");

    }


    private void make(){

        try {

            File configFile = new File(dataDirectory.toFile(), "config.yml");

            config = YamlDocument.create(
                    configFile, // Path to config.yml
                    Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")), // Default config in JAR
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder()
                            .setVersioning(new BasicVersioning("file-version"))
                            .setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS)
                            .build()
            );

            config.update();
            config.save();

        }
        catch (IOException e) {

            proxyManagement.shutDown("Failed to load or create config.yml",e);

        }

    }

    private void getStaticValues() {

        Driver = config.getString(Route.from("driver")).toLowerCase();

        DatabaseAddress = config.getString(Route.from("database_address")).toLowerCase();

        DatabasePort = config.getString(Route.from("database_port"));

        DatabaseName = config.getString(Route.from("database_name"));

        username = config.getString(Route.from("username"));

        password = config.getString(Route.from("password"));

        MaximumPoolSize = config.getInt(Route.from("MaximumPoolSize"));

        MinimumIdle = config.getInt(Route.from("MinumumIdle"));

        IdleTimeout = config.getInt(Route.from("IdleTimeout"));

        MaxLifetime = config.getInt(Route.from("MaxLifetime"));

        DiscordOAuth2 = config.getBoolean(Route.from("DiscordOAuth2"));

        BotToken = config.getString(Route.from("BotToken"));

        GuildId = config.getString(Route.from("GuildId"));

        VerifyCodeVarName = config.getString(Route.from("VerifyCodeVarName"));

        VerifyCodeVarDescription = config.getString(Route.from("VerifyCodeVarDescription"));

        VerifyUUIDVarName = config.getString(Route.from("VerifyUUIDVarName"));

        VerifyUUIDVarDescription = config.getString(Route.from("VerifyUUIDVarName"));

        VerifyName = config.getString(Route.from("VerifyName"));

        VerifyDescription = config.getString(Route.from("VerifyDescription"));

        VerifyNoCode = config.getString(Route.from("VerifyNoCode"));

        VerifyInvalidCode = config.getString(Route.from("VerifyInvalidCode"));

        VerifySuccessCode = config.getString(Route.from("VerifySuccessCode"));

        DefaultRole = config.getBoolean(Route.from("DefaultRole"));

        UserLeaveNickname = config.getBoolean(Route.from("UserLeaveNickname"));

        SyncTimeOut = config.getLong(Route.from("SyncTimeOut"));

        RolesToSync = settingRolesValues(config.getMapList("RolesToSync").getFirst());

    }


    @Override
    public void gettingData(){

        DiscordApiBaseUrl = config.getString(Route.from("DiscordApiBaseUrl"));

        RedirectUrl = config.getString(Route.from("RedirectUrl"));

        ClientId = config.getString(Route.from("ClientId"));

        ClickMessage = config.getString(Route.from("click_message"));

        BeforeClickMessage = config.getString(Route.from("before_click_message"));

        ExpiredCodeMessage = config.getString(Route.from("expired_code_message"));

        AlreadyConnectedMessage = config.getString(Route.from("already_connected_message"));

        ReloadMessage = config.getString(Route.from("reload_message"));

        NoPermission = config.getString(Route.fromString("no_permission"));

        NotConnectedMessage = config.getString(Route.fromString("not_connected_message"));

        HintUsage = config.getString(Route.from("hint_usage"));

        DisconnectedMessage = config.getString(Route.from("disconnected_message"));

    }

    private Map<String, String> settingRolesValues(Map<?,?> rolemap){

        Map<String,String> roles = new HashMap<>() {
        };

        rolemap.forEach((k,v) -> {

            roles.put((String) k, (String) v);

        });

        return roles;
    }

    @Override
    public void reload(){

        make();
        gettingData();
        logger.info("Plugins data was successfully reloaded");

    }


}
