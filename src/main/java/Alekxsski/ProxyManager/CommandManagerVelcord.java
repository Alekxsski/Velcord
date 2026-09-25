package Alekxsski.ProxyManager;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import Alekxsski.Commands.DiscordVerify;
import Alekxsski.Commands.Reload;
import Alekxsski.Velcord;

public class CommandManagerVelcord {

    private final ProxyServer server;
    private final DiscordVerify discordVerify;
    private final Reload reload;

    @Inject
    public CommandManagerVelcord(DiscordVerify discordVerify, Reload reload, ProxyServer server){


        this.discordVerify = discordVerify;
        this.server = server;
        this.reload = reload;


    }

    public void registerCommands(Velcord plugin){

        registerCommand(plugin,"discord", "dc", discordVerify);
        registerCommand(plugin, "huzuni_velocity_reload","hvr", reload);

    }

    private void registerCommand(Velcord plugin, String alias, String aliases, Object command){

        com.velocitypowered.api.command.CommandManager commandManager = server.getCommandManager();

        CommandMeta reloadMeta = commandManager.metaBuilder(alias)

                .plugin(plugin)
                .aliases(aliases)
                .build();

        // Finally, you can register the command
        commandManager.register(reloadMeta, (SimpleCommand) command);

    }


}
