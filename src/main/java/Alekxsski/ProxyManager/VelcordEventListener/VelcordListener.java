package Alekxsski.ProxyManager.VelcordEventListener;

import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;

import java.util.List;

public class VelcordListener{

    private final DatabaseDiscordMethods databaseDiscordMethods;
    private final RolesManager rolesManager;

    public VelcordListener(DatabaseDiscordMethods databaseDiscordMethods,RolesManager rolesManager){

        this.databaseDiscordMethods = databaseDiscordMethods;
        this.rolesManager = rolesManager;

    }

    @Subscribe
    public void OnPlayerJoin(ServerPostConnectEvent event){
        
        Player player = event.getPlayer();

        System.out.println("fired now");

        databaseDiscordMethods.executeStatement("SELECT discord_user_id FROM player_discord WHERE uuid =?", List.of(String.valueOf(player.getUniqueId())), "discord_user_id")
                .thenAccept(discordUserId -> {

                    if (discordUserId != null) {

                        try {
                            rolesManager.checkExistingMember(discordUserId, player.getUsername(), null);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }

                });

        
    }

}
