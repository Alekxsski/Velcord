package Alekxsski.Commands;

import Alekxsski.Utils.PlayerBased.SoundsEnum;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.Interfaces.ReloadBehaviour;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.DiscordAuth;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import Alekxsski.Utils.PlayerBased.KyoriAdevntureApi;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class DiscordVerify implements SimpleCommand, ReloadBehaviour {

    private final DiscordAuth discordAuth;

    private final DatabaseDiscordMethods databaseDiscordMethods;

    private final Config config;

    private final RolesManager rolesManager;

    private String AlreadyConnectedMessage;

    private String NotConnectedMessage;

    private String HintUsage;

    private String DisconnectedMessage;

    private final String Connect;

    private final String Disconnect;

    private final KyoriAdevntureApi adevntureApi;

    @Inject
    public DiscordVerify(DiscordAuth discordAuth, DatabaseDiscordMethods databaseDiscordMethods, RolesManager rolesManager,
                         Config config, KyoriAdevntureApi adevntureApi){

        this.discordAuth = discordAuth;

        this.databaseDiscordMethods = databaseDiscordMethods;

        this.config = config;

        this.rolesManager = rolesManager;

        this.Disconnect = config.getDisconnect();

        this.Connect = config.getConnect();

        this.adevntureApi = adevntureApi;

        setUp();

    }

    public void setUp(){

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

                   player.sendMessage(KyoriAdevntureApi.MinimessagePlain(HintUsage));

            }
            else {


                UUID player_uuid = player.getUniqueId();

                //Check if user is already registered in database
                databaseDiscordMethods.executeStatement("SELECT discord_user_id FROM player_discord WHERE uuid = ?",
                        List.of(player_uuid.toString()), "discord_user_id")
                        .thenAccept(user_discord_id -> {

                                if (Objects.equals(args[0], Connect)) {

                                    verificationConnect(player, user_discord_id);

                                } else if (Objects.equals(args[0], Disconnect)) {

                                    verificationDisconnect(player, player_uuid, user_discord_id);

                                } else {

                                    player.sendMessage(KyoriAdevntureApi.MinimessagePlain(HintUsage));

                                }
                            });

            }

        }

    }

    private void verificationConnect(Player player, String user_discord_id){

        if (user_discord_id != null) {

            player.sendMessage(KyoriAdevntureApi.MinimessagePlain(AlreadyConnectedMessage));

            adevntureApi.PlaySoundVerication(player,SoundsEnum.DENY);


        } else {

            String url = discordAuth.MakeUrl(player);

            player.sendMessage(KyoriAdevntureApi.MinimessagePlain(url));

            adevntureApi.PlaySoundVerication(player,SoundsEnum.VERIFY);


        }


    }

    private void verificationDisconnect(Player player,UUID player_uuid, String user_discord_id){

        if (user_discord_id != null) {

            databaseDiscordMethods.executeStatement("DELETE FROM player_discord WHERE uuid = ?",
                    List.of(player_uuid.toString()),null).thenRun(()-> {

                rolesManager.setMemberBackToDefault(user_discord_id);

                player.sendMessage(KyoriAdevntureApi.MinimessagePlain(DisconnectedMessage));

                adevntureApi.PlaySoundVerication(player,SoundsEnum.DISCONNECT);


            });


        } else {

            player.sendMessage(KyoriAdevntureApi.MinimessagePlain(NotConnectedMessage));
            adevntureApi.PlaySoundVerication(player,SoundsEnum.DENY);

        }


    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {

        return CompletableFuture.completedFuture(List.of(Connect, Disconnect));

    }


}
