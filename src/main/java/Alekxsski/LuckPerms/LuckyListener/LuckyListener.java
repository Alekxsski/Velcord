package Alekxsski.LuckPerms.LuckyListener;

import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.LuckPerms.LuckPermsManager;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import Alekxsski.Velcord;
import com.google.inject.Inject;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.user.User;

import java.util.List;

public class LuckyListener {

    private final LuckPermsManager luckPermsManager;

    private final RolesManager rolesManager;

    private final DatabaseDiscordMethods databaseDiscordMethods;


    @Inject
    public LuckyListener(LuckPermsManager luckPermsManager, RolesManager rolesManager, DatabaseDiscordMethods databaseDiscordMethods){

        this.luckPermsManager = luckPermsManager;
        this.rolesManager = rolesManager;
        this.databaseDiscordMethods = databaseDiscordMethods;

    }

    public void hookLuckListener(Velcord plugin){

        EventBus eventBus = luckPermsManager.getLuckApi().getEventBus();

        eventBus.subscribe(plugin, NodeMutateEvent.class, event -> {

            if (event.getTarget() instanceof User user) {


                databaseDiscordMethods.executeStatement("SELECT discord_user_id FROM player_discord WHERE uuid =?", List.of(String.valueOf(user.getUniqueId())), "discord_user_id")
                        .thenAccept(discordUserId -> {

                            if (discordUserId != null) {

                                try {
                                    rolesManager.checkExistingMember(discordUserId, null, user.getPrimaryGroup());
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }

                            }

                        });


            }

        });


    }

}
