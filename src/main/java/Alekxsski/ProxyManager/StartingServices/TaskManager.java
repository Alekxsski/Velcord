package Alekxsski.ProxyManager.StartingServices;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.ProxyServer;
import Alekxsski.Velcord;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMapCleaner;

import java.util.concurrent.TimeUnit;

//Class that generates and initiates tasks
public class TaskManager {

    private final DiscordAuthHashMapCleaner discordAuthHashMapCleaner;
    private final ProxyServer server;

    @Inject
    public TaskManager(DiscordAuthHashMapCleaner discordAuthHashMapCleaner, ProxyServer server){

        this.discordAuthHashMapCleaner = discordAuthHashMapCleaner;
        this.server = server;

    }

    public void startTasks(Velcord plugin){

        discordAuthHashMapCleaner.setUp();

        createTask(plugin,15L,5L,discordAuthHashMapCleaner::clean);


    }

    private void createTask(Velcord plugin, Long repeat, Long delay, Runnable action){

        server.getScheduler()
                .buildTask(plugin, action)
                .repeat(repeat, TimeUnit.SECONDS)
                .delay(delay, TimeUnit.SECONDS)
                .schedule();

    }

}
