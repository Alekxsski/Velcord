package Alekxsski.ProxyManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

@Singleton
public class ProxyManagement {

    private final ProxyServer server;

    private final Logger logger;


    @Inject
    public ProxyManagement(ProxyServer server, Logger logger){

        this.server = server;
        this.logger = logger;
        checkLuckPerms();

    }

    public void shutDown(String error_message, Exception exception){
        logger.error(error_message);
        logger.error("Plugin has encountered critical error and will now shut down",exception);

        Optional<PluginContainer> container = server.getPluginManager().getPlugin("velcord");

        container.ifPresent(pluginContainer -> pluginContainer.getExecutorService().shutdown());
    }

    public void checkLuckPerms() {

        if (!server.getPluginManager().isLoaded("luckperms")){

            shutDown("LuckPerms is somehow not detected by plugin!",new Exception("LuckPerms couldn't be detected"));

        }

        logger.info("Successfully hooked into luckperms");

    }

    public boolean isPlayerActive(UUID player_uuid){

        return server.getPlayer(player_uuid).isPresent();

    }

    public Player getPlayer(UUID player_uuid){

        Optional<Player> player = server.getPlayer(player_uuid);

        return player.orElse(null);


    }


}

