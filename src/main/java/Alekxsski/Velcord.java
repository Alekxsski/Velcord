package Alekxsski;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import Alekxsski.Database.DatabaseManager;
import Alekxsski.ProxyManager.CommandManagerVelcord;
import Alekxsski.ProxyManager.JdaHook.JdaHook;
import Alekxsski.ProxyManager.TaskManager;
import org.slf4j.Logger;

@Plugin(id = "velcord", name = "velcord", version = "1.0-alpha", dependencies =
@Dependency(id = "luckperms")
)
public class Velcord {

    private final CommandManagerVelcord commandManagerVelcord;

    private final DatabaseManager databaseManager;

    private final TaskManager taskManager;


    @Inject
    public Velcord(Logger logger, CommandManagerVelcord commandManagerVelcord, TaskManager taskManager, DatabaseManager databaseManager, JdaHook jdaHook){

        this.commandManagerVelcord = commandManagerVelcord;

        this.taskManager = taskManager;

        this.databaseManager = databaseManager;

        logger.info("Huzuni_velocity started");

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {


        commandManagerVelcord.registerCommands(this);

        taskManager.startTasks(this);

    }


    @Subscribe
    public void proxyShutdownEvent(ProxyShutdownEvent event){

        databaseManager.shutDown();

    }

}
