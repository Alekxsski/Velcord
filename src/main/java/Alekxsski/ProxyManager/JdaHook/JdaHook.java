package Alekxsski.ProxyManager.JdaHook;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
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

    private final JDA jda;

    private final RolesManager rolesManager;

    private final ProxyManagement proxyManagement;

    @Getter
    private final Guild guild;

    private final String VerifyName;

    private final String VerifyDescription;

    private final String VerifyCodeVarName;

    private final String VerifyCodeVarDescription;

    private final String VerifyUUIDVarName;

    private final String VerifyUUIDVarDescription;



    @Inject
    public JdaHook(Config config, DiscordVerify discordVerify, RolesManager rolesManager, ProxyManagement proxyManagement) throws InterruptedException {

        this.jda = JDABuilder.createDefault(config.getBotToken())
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

        this.guild = jda.getGuildById(config.getGuildId());

        this.rolesManager = rolesManager;

        this.proxyManagement = proxyManagement;

        this.VerifyName = config.getVerifyName();

        this.VerifyDescription = config.getVerifyDescription();

        this.VerifyCodeVarName = config.getVerifyCodeVarName();

        this.VerifyCodeVarDescription = config.getVerifyCodeVarDescription();

        this.VerifyUUIDVarName = config.getVerifyUUIDVarName();

        this.VerifyUUIDVarDescription = config.getVerifyUUIDVarDescription();

        syncRolesStart();

        registerCommands();

    }


    private void registerCommands(){

        guild.updateCommands().addCommands(Commands.slash(VerifyName,VerifyDescription)
                .addOption(OptionType.STRING,VerifyUUIDVarName,VerifyUUIDVarDescription,true)
                .addOption(OptionType.STRING,VerifyCodeVarName,VerifyCodeVarDescription,true)).queue();

    }

    private void syncRolesStart(){

        if(guild == null){

            proxyManagement.shutDown("Check your config and make sure application is in the right guild", new Exception("Guild wasn't found"));

        }

        rolesManager.setGuild(guild);

        rolesManager.discordRolesConversion();

    }

}




