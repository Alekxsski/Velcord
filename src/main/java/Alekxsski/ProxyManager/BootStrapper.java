package Alekxsski.ProxyManager;

import Alekxsski.Database.DatabaseManager;
import Alekxsski.ProxyManager.JdaHook.Commands.DiscordVerify;
import Alekxsski.ProxyManager.JdaHook.JdaHook;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import Alekxsski.ProxyManager.StartingServices.CommandManagerVelcord;
import Alekxsski.ProxyManager.StartingServices.TaskManager;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.DiscordAuth;
import Alekxsski.Velcord;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

@Singleton
public class BootStrapper {

    private final Logger logger;
    private final JdaHook jdaHook;
    private final CommandManagerVelcord commandManagerVelcord;
    private final TaskManager taskManager;
    private final DatabaseManager databaseManager;
    private final Config config;
    private final DiscordAuth discordAuth;
    private final DiscordVerify discordVerify;
    private final RolesManager rolesManager;

    @Inject
    public BootStrapper(Logger logger, JdaHook jdaHook, CommandManagerVelcord commandManagerVelcord,
                        TaskManager taskManager, DatabaseManager databaseManager, Config config,
                        DiscordAuth discordAuth, DiscordVerify discordVerify, RolesManager rolesManager) {

        this.logger = logger;
        this.jdaHook = jdaHook;
        this.commandManagerVelcord = commandManagerVelcord;
        this.taskManager = taskManager;
        this.databaseManager = databaseManager;
        this.config = config;
        this.discordAuth = discordAuth;
        this.discordVerify = discordVerify;
        this.rolesManager = rolesManager;

    }

    public void start(Velcord plugin) throws InterruptedException {

        config.setUp();

        discordAuth.setUp();

        discordVerify.setUp();

        commandManagerVelcord.registerCommands(plugin);

        databaseManager.initialize();

        rolesManager.setup();

        jdaHook.setUp();

        taskManager.startTasks(plugin);

        logger.info("Huzuni_velocity started");

    }

    public void shutDown(){

        databaseManager.shutDown();
        jdaHook.stop();

    }

}
