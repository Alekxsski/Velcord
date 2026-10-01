package Alekxsski.LuckPerms;

import com.google.inject.Inject;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.slf4j.Logger;

import java.util.UUID;


public class LuckPermsManager {

    private LuckPerms luckApi = null;
    private final Logger logger;

    @Inject
    public LuckPermsManager(Logger logger){

        this.logger = logger;

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

            if(user == null) {

                logger.error("Player data couldn't be fetched from luckperms!");
                return null;

            }

            return new LuckPlayerData(user.getUsername(), user.getPrimaryGroup());


    }


}
