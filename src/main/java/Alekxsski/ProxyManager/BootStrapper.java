package Alekxsski.ProxyManager;

import Alekxsski.Database.DatabaseManager;
import Alekxsski.ProxyManager.JdaHook.JdaHook;
import Alekxsski.ProxyManager.StartingServices.CommandManagerVelcord;
import Alekxsski.ProxyManager.StartingServices.TaskManager;
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
    private final DiscordAuth discordAuth;

    @Inject
    public BootStrapper(Logger logger, JdaHook jdaHook, CommandManagerVelcord commandManagerVelcord,
                        TaskManager taskManager, DatabaseManager databaseManager, DiscordAuth discordAuth) {

        this.logger = logger;
        this.jdaHook = jdaHook;
        this.commandManagerVelcord = commandManagerVelcord;
        this.taskManager = taskManager;
        this.databaseManager = databaseManager;
        this.discordAuth = discordAuth;

    }

    public void start(Velcord plugin) throws InterruptedException {

        discordAuth.setUp();

        commandManagerVelcord.registerCommands(plugin);

        databaseManager.initialize();

        jdaHook.setUp();

        taskManager.startTasks(plugin);

        logger.info("Huzuni_velocity started");

    }

    public void shutDown(){

        databaseManager.shutDown();
        jdaHook.stop();

    }

}
