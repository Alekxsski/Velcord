package Alekxsski.ProxyManager.JdaHook.JdaListener;

import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import Alekxsski.Utils.Config;
import com.google.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

public class JdaListener implements EventListener{

    private final RolesManager rolesManager;
    private final Logger logger;
    private final Config config;

    private String VerifyName;

    private String VerifyDescription;

    private String VerifyCodeVarName;

    private String VerifyCodeVarDescription;

    private String VerifyUUIDVarName;

    private String VerifyUUIDVarDescription;

    @Inject
    public JdaListener(RolesManager rolesManager, Logger logger, Config config){

        this.rolesManager = rolesManager;
        this.logger = logger;
        this.config = config;

        setUp();

    }

    @Override
    public void onEvent(@NonNull GenericEvent event) {

        if (event instanceof ReadyEvent readyEvent){

            JDA jda = readyEvent.getJDA();

            Guild guild = jda.getGuildById(config.getGuildId());

            if (guild == null) {

                logger.error("Guild not found, Check config to make sure your guild id is valid and application is registered on this guild");
                return;

            }

            rolesManager.setGuild(guild);

            rolesManager.setSelf(guild.getSelfMember());

            registerCommands(guild);

        }

    }

    private void setUp(){

        VerifyName = config.getVerifyName();

        VerifyDescription = config.getVerifyDescription();

        VerifyCodeVarName = config.getVerifyCodeVarName();

        VerifyCodeVarDescription = config.getVerifyCodeVarDescription();

        VerifyUUIDVarName = config.getVerifyUUIDVarName();

        VerifyUUIDVarDescription = config.getVerifyUUIDVarDescription();

    }

    private void registerCommands(Guild guild){

        guild.updateCommands().addCommands(Commands.slash(VerifyName,VerifyDescription)
                .addOption(OptionType.STRING,VerifyUUIDVarName,VerifyUUIDVarDescription,true)
                .addOption(OptionType.STRING,VerifyCodeVarName,VerifyCodeVarDescription,true)).queue();

    }

}
