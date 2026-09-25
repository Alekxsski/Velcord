package Alekxsski.ProxyManager;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import Alekxsski.Velcord;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMapCleaner;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

public class TaskManager {

    private final RolesManager rolesManager;
    private final DiscordAuthHashMapCleaner discordAuthHashMapCleaner;
    private final ProxyServer server;
    private final Logger logger;
    private final Long SyncTimeOut;

    @Inject
    public TaskManager(RolesManager rolesManager, DiscordAuthHashMapCleaner discordAuthHashMapCleaner, ProxyServer server, Logger logger, Config config){

        this.discordAuthHashMapCleaner = discordAuthHashMapCleaner;
        this.rolesManager = rolesManager;
        this.server = server;
        this.logger = logger;
        this.SyncTimeOut = config.getSyncTimeOut();

    }

    public void startTasks(Velcord plugin){


        createTask(plugin,15L,5L,discordAuthHashMapCleaner::start);

        if(syncTime(SyncTimeOut)) createTask(plugin,SyncTimeOut,10L,rolesManager::start);


    }

    private void createTask(Velcord plugin, Long repeat, Long delay, Runnable action){

        server.getScheduler()
                .buildTask(plugin, action)
                .repeat(repeat, TimeUnit.SECONDS)
                .delay(delay, TimeUnit.SECONDS)
                .schedule();

    }

    private boolean syncTime(long repeat){
        if(repeat > 0L) {

            if(repeat < 15L) {

                logger.warn("Are you sure you want to sync all members that frequently? such task can be very cpu demanding." +
                        " Consider changing time value to at least 15s. Task will be still enabled it's just a recommendation");

            }

            return true;

        }

        logger.info("Role synchronization has been disabled!");
        return false;

    }

}
