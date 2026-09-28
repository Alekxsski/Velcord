package Alekxsski.ProxyManager;

import Alekxsski.Database.DatabaseManager;
import Alekxsski.LuckPerms.LuckPermsManager;
import Alekxsski.LuckPerms.LuckyListener.LuckyListener;
import Alekxsski.ProxyManager.JdaHook.JdaHook;
import Alekxsski.ProxyManager.StartingServices.CommandManagerVelcord;
import Alekxsski.ProxyManager.StartingServices.TaskManager;
import Alekxsski.Utils.DiscordAuth.DiscordAuth;
import Alekxsski.Velcord;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;

@Singleton
public class BootStrapper {

    private final Logger logger;
    private final JdaHook jdaHook;
    private final CommandManagerVelcord commandManagerVelcord;
    private final TaskManager taskManager;
    private final DatabaseManager databaseManager;
    private final LuckyListener luckyListener;

    @Inject
    public BootStrapper(Logger logger, JdaHook jdaHook, CommandManagerVelcord commandManagerVelcord,
                        TaskManager taskManager, DatabaseManager databaseManager, LuckyListener luckyListener) {

        this.logger = logger;
        this.jdaHook = jdaHook;
        this.commandManagerVelcord = commandManagerVelcord;
        this.taskManager = taskManager;
        this.databaseManager = databaseManager;
        this.luckyListener = luckyListener;

    }

    public void Hook(Velcord plugin) throws InterruptedException {

        commandManagerVelcord.registerCommands(plugin);

        databaseManager.initialize();

        jdaHook.setUp();

        taskManager.startTasks(plugin);

        luckyListener.hookLuckListener(plugin);

        logger.info("Velcord successfuly enabled all addons");

    }

    public void shutDown(){

        databaseManager.shutDown();
        jdaHook.stop();

    }

}
