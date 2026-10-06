package Alekxsski.LuckPerms;

import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;


public class LuckPermsManager {

    private LuckPerms luckApi = null;
    private final Logger logger;
    private final ProxyServer server;

    @Inject
    public LuckPermsManager(Logger logger, ProxyServer server){

        this.logger = logger;
        this.server = server;

    }


    public LuckPerms getLuckApi(){

        if (luckApi == null){

            luckApi = LuckPermsProvider.get();
            logger.info("Successfully hooked into luckperms");

        }

        return luckApi;

    }


    public LuckPlayerData getActivePlayerData(UUID player_uuid) {

            UserManager userManager = getLuckApi().getUserManager();

            User user = userManager.getUser(player_uuid);

            Optional<Player> player = server.getPlayer(player_uuid);

            if(user == null) {

                logger.error("Player data couldn't be fetched from luckperms!");
                return null;

            }

            String player_name = user.getUsername();

            if(player.isPresent()){

                player_name = player.get().getUsername();

            }

            else{

                logger.warn("Player wasn't on server while fetching username from proxy. Data will be fetched from luck perms api(username will be in lowercase)");

            }

            return new LuckPlayerData(player_name, user.getPrimaryGroup());


    }


}
