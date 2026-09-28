package Alekxsski.ProxyManager.JdaHook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.ProxyManager.ProxyManagement;
import Alekxsski.Utils.Config;
import Alekxsski.ProxyManager.JdaHook.Commands.DiscordVerify;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Singleton
public class JdaHook {

    private JDA jda;

    private final RolesManager rolesManager;

    private final ProxyManagement proxyManagement;

    private final Config config;

    private final DiscordVerify discordVerify;

    private Guild guild;

    private String VerifyName;

    private String VerifyDescription;

    private String VerifyCodeVarName;

    private String VerifyCodeVarDescription;

    private String VerifyUUIDVarName;

    private String VerifyUUIDVarDescription;



    @Inject
    public JdaHook(Config config, DiscordVerify discordVerify, RolesManager rolesManager, ProxyManagement proxyManagement){

        this.discordVerify = discordVerify;

        this.config = config;

        this.rolesManager = rolesManager;

        this.proxyManagement = proxyManagement;

    }

    public void setUp() throws InterruptedException {

        VerifyName = config.getVerifyName();

        VerifyDescription = config.getVerifyDescription();

        VerifyCodeVarName = config.getVerifyCodeVarName();

        VerifyCodeVarDescription = config.getVerifyCodeVarDescription();

        VerifyUUIDVarName = config.getVerifyUUIDVarName();

        VerifyUUIDVarDescription = config.getVerifyUUIDVarDescription();

        jda = JDABuilder.createDefault(config.getBotToken())
                .enableIntents(GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MESSAGES)
                .disableCache(CacheFlag.ACTIVITY,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ONLINE_STATUS,
                        CacheFlag.VOICE_STATE
                )
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(discordVerify).build();

        jda.awaitReady();

        guild = jda.getGuildById(config.getGuildId());

        if (guild == null) proxyManagement.shutDown("Guild not found",
                new Exception("Check config to make sure your guild id is valid and application is registered on this guild"));

        syncRolesStart();

        registerCommands();

    }

    public void stop(){

        jda.shutdown();

    }


    private void registerCommands(){

        guild.updateCommands().addCommands(Commands.slash(VerifyName,VerifyDescription)
                .addOption(OptionType.STRING,VerifyUUIDVarName,VerifyUUIDVarDescription,true)
                .addOption(OptionType.STRING,VerifyCodeVarName,VerifyCodeVarDescription,true)).queue();

    }

    private void syncRolesStart(){

        rolesManager.setGuild(guild);

        rolesManager.setSelf(guild.getSelfMember());

        rolesManager.discordRolesConversion();

    }

}




