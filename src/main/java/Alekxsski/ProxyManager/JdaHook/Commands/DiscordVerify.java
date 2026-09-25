package Alekxsski.ProxyManager.JdaHook.Commands;

import com.google.inject.Inject;
import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.Utils.Config;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthData;
import Alekxsski.Utils.DiscordAuth.Utils.DiscordAuthHashMap;
import Alekxsski.ProxyManager.JdaHook.RolesManager.RolesManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DiscordVerify extends ListenerAdapter {

    private final HashMap<UUID, DiscordAuthData> discordAuthDataHashMap;

    private final DatabaseDiscordMethods databaseDiscordMethods;

    private final RolesManager rolesManager;

    private final String VerifyName;

    private final String VerifyCodeVarName;

    private final String VerifyUUIDVarName;

    private final String VerifyNoCode;

    private final String VerifyInvalidCode;

    private final String VerifySuccessCode;






    @Inject
    public DiscordVerify(DatabaseDiscordMethods databaseDiscordMethods, DiscordAuthHashMap discordAuthHashMap, Config config, RolesManager rolesManager){

        this.databaseDiscordMethods = databaseDiscordMethods;

        this.rolesManager = rolesManager;

        this.discordAuthDataHashMap = discordAuthHashMap.getDiscordAuthDataHashMap();

        this.VerifyName = config.getVerifyName();

        this.VerifyCodeVarName = config.getVerifyCodeVarName();

        this.VerifyUUIDVarName = config.getVerifyUUIDVarName();

        this.VerifyNoCode = config.getVerifyNoCode();

        this.VerifyInvalidCode = config.getVerifyInvalidCode();

        this.VerifySuccessCode = config.getVerifySuccessCode();

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        if (event.getName().equals(VerifyName)){

            String userId = event.getUser().getId();
            String code = Objects.requireNonNull(event.getOption(VerifyCodeVarName)).getAsString();
            UUID uuid = UUID.fromString(Objects.requireNonNull(event.getOption(VerifyUUIDVarName)).getAsString());

            String message = isCodeValid(code,uuid, userId);

            event.reply(message).setEphemeral(true).queue();

        }


    }

    private String isCodeValid(String code, UUID uuid, String userId){

        if(discordAuthDataHashMap.containsKey(uuid)){

            if(Objects.equals(discordAuthDataHashMap.get(uuid).getCode(), code)){


                databaseDiscordMethods.executeStatement("INSERT INTO player_discord (uuid, discord_user_id) VALUES (?, ?)", List.of(uuid.toString(),userId),null)
                        .thenRun(() ->{

                            rolesManager.MemberUpdateUUID(userId,uuid);
                            discordAuthDataHashMap.remove(uuid);

                        });

                return VerifySuccessCode;

            }

            else {

                return VerifyInvalidCode;

            }

        }

        return VerifyNoCode;

    }


}
