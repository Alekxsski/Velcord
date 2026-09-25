package Alekxsski.Commands;

import com.google.inject.Inject;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import Alekxsski.Interfaces.ReloadBehaviour;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.DiscordAuth;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMapCleaner;
import Alekxsski.Utils.PlayerBased.Minimessage;

public class Reload implements SimpleCommand, ReloadBehaviour {

    private final Config config;

    private final DiscordAuth discordAuth;

    private final DiscordVerify discordVerify;

    private final DiscordAuthHashMapCleaner discordAuthHashMapCleaner;

    private String ReloadMessage;

    private String NoPermission;

    @Inject
    public Reload(Config config, DiscordAuth discordAuth, DiscordVerify discordVerify, DiscordAuthHashMapCleaner discordAuthHashMapCleaner){

        this.config = config;

        this.discordAuth = discordAuth;

        this.discordVerify = discordVerify;

        this.discordAuthHashMapCleaner = discordAuthHashMapCleaner;

        gettingData();

    }

    @Override
    public void reload() {

        config.reload();

        discordAuth.reload();

        discordVerify.reload();

        discordAuthHashMapCleaner.reload();

        gettingData();

    }

    @Override
    public void gettingData() {

        ReloadMessage = config.getReloadMessage();

        NoPermission = config.getNoPermission();

    }

    @Override
    public void execute(Invocation invocation) {

        if(invocation.source() instanceof ConsoleCommandSource){

            reload();

        }

        else{

            Player player = (Player) invocation.source();

            if(player.hasPermission("Huzuni_Velocity.reload")){

                reload();
                player.sendMessage(Minimessage.MinimessagePlain(ReloadMessage));

            }

            else{

                player.sendMessage(Minimessage.MinimessagePlain(NoPermission));

            }

        }

    }

}
