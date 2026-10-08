package Alekxsski.ProxyManager;

import Alekxsski.Database.DatabaseManager;
import Alekxsski.LuckPerms.LuckyListener.LuckyListener;
import Alekxsski.ProxyManager.JdaHook.JdaHook;
import Alekxsski.ProxyManager.StartingServices.CommandManagerVelcord;
import Alekxsski.ProxyManager.StartingServices.EventListenerManager;
import Alekxsski.ProxyManager.StartingServices.TaskManager;
import Alekxsski.Velcord;
import Alekxsski.backend.SimpleApi.SimpleApi;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;

@Singleton
//Class that manages start of the plugin and activates needed objects and starts services
public class BootStrapper {

    private final Logger logger;
    private final JdaHook jdaHook;
    private final CommandManagerVelcord commandManagerVelcord;
    private final TaskManager taskManager;
    private final DatabaseManager databaseManager;
    private final LuckyListener luckyListener;
    private final SimpleApi simpleApi;
    private final EventListenerManager eventListenerManager;

    @Inject
    public BootStrapper(Logger logger, JdaHook jdaHook, CommandManagerVelcord commandManagerVelcord,
                        TaskManager taskManager, DatabaseManager databaseManager,
                        LuckyListener luckyListener, SimpleApi simpleApi, EventListenerManager eventListenerManager) {

        this.logger = logger;
        this.jdaHook = jdaHook;
        this.commandManagerVelcord = commandManagerVelcord;
        this.taskManager = taskManager;
        this.databaseManager = databaseManager;
        this.luckyListener = luckyListener;
        this.simpleApi = simpleApi;
        this.eventListenerManager = eventListenerManager;

    }

    public void Hook(Velcord plugin) {

        commandManagerVelcord.registerCommands(plugin);

        eventListenerManager.registerListener(plugin);

        databaseManager.initialize();

        jdaHook.setUp();

        taskManager.startTasks(plugin);

        luckyListener.hookLuckListener(plugin);

        simpleApi.simpleApiHook();

        logger.info("Velcord successfuly enabled all addons");

    }

    public void shutDown(){

        simpleApi.stop();
        jdaHook.stop();
        databaseManager.shutDown();


    }

}
