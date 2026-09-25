package Alekxsski.Commands;

import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.Interfaces.ReloadBehaviour;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.DiscordAuth;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import Alekxsski.Utils.PlayerBased.Minimessage;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DiscordVerify implements SimpleCommand, ReloadBehaviour {

    private final DiscordAuth discordAuth;

    private final DatabaseDiscordMethods databaseDiscordMethods;

    private final Config config;

    private final RolesManager rolesManager;

    private String AlreadyConnectedMessage;

    private String NotConnectedMessage;

    private String HintUsage;

    private String DisconnectedMessage;

    @Inject
    public DiscordVerify(DiscordAuth discordAuth, DatabaseDiscordMethods databaseDiscordMethods, RolesManager rolesManager, Config config){

        this.discordAuth = discordAuth;

        this.databaseDiscordMethods = databaseDiscordMethods;

        this.config = config;

        this.rolesManager = rolesManager;

        gettingData();

    }

    @Override
    public void reload() {

        gettingData();

    }

    @Override
    public void gettingData() {

        AlreadyConnectedMessage = config.getAlreadyConnectedMessage();

        NotConnectedMessage = config.getNotConnectedMessage();

        HintUsage = config.getHintUsage();

        DisconnectedMessage = config.getDisconnectedMessage();

    }

    @Override
    public void execute(Invocation invocation) {

        if (invocation.source() instanceof Player player){

            String[] args = invocation.arguments();

            if (args.length == 0){

                   player.sendMessage(Minimessage.MinimessagePlain(HintUsage));

            }
            else {


                UUID player_uuid = player.getUniqueId();


                databaseDiscordMethods.executeStatement("SELECT discord_user_id FROM minecraft_to_discord WHERE uuid = ?",
                        List.of(player_uuid.toString()), "discord_user_id").thenAccept(user_discord_id -> {

                    if(Objects.equals(args[0], "polacz")){

                        verificationConnect(player,player_uuid, user_discord_id);
    
                    }

                    else if (Objects.equals(args[0], "rozlacz")) {

                        verificationDisconnect(player,player_uuid, user_discord_id);

                    }
                    else {

                        player.sendMessage(Minimessage.MinimessagePlain(HintUsage));

                    }

                });
            }

        }

    }

    private void verificationConnect(Player player,UUID player_uuid, String user_discord_id){

        if (user_discord_id != null) {

            player.sendMessage(Minimessage.MinimessagePlain(AlreadyConnectedMessage));


        } else {

            String url = discordAuth.MakeUrl(player_uuid);

            player.sendMessage(Minimessage.MinimessagePlain(url));

        }


    }

    private void verificationDisconnect(Player player,UUID player_uuid, String user_discord_id){

        if (user_discord_id != null) {

            databaseDiscordMethods.executeStatement("DELETE FROM player_discord WHERE uuid = ?",
                    List.of(player_uuid.toString()),null).thenRun(()-> {

                rolesManager.MemberBackToDefault(user_discord_id);

                player.sendMessage(Minimessage.MinimessagePlain(DisconnectedMessage));


            });


        } else {

            player.sendMessage(Minimessage.MinimessagePlain(NotConnectedMessage));

        }


    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {

        return CompletableFuture.completedFuture(List.of("polacz", "rozlacz"));

    }


}
