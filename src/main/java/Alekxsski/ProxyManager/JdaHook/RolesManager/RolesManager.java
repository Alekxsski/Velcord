package Alekxsski.ProxyManager.JdaHook.RolesManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Setter;
import Alekxsski.Database.PlayerManagment.DatabaseDiscordMethods;
import Alekxsski.Utils.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;

import java.util.*;

@Singleton
public class RolesManager {


    private final Logger logger;

    private final DatabaseDiscordMethods databaseDiscordMethods;

    private final ProxyServer server;

    private final Map<String, String> RolesToSync;

    private Map<String, Role> RolesToSyncConverted;

    private final Boolean DefaultRole;

    private final Boolean UserLeaveNickname;

    @Setter
    private Guild guild;

    @Inject
    public RolesManager(Config config, Logger logger, DatabaseDiscordMethods databaseDiscordMethods, ProxyServer server){

        this.logger = logger;

        this.databaseDiscordMethods = databaseDiscordMethods;

        this.server = server;

        this.RolesToSync = config.getRolesToSync();

        this.DefaultRole = config.getDefaultRole();

        this.UserLeaveNickname = config.getUserLeaveNickname();

    }

    public void discordRolesConversion(){

        Map<String, Role> discordRoles = new HashMap<>();

        RolesToSync.forEach((k,v) ->{

            Role role = guild.getRoleById(v);

            if(role != null){

                logger.info("Role {} has been successfully registered as value of key {} ",role.getName(),k);

            }
            else{

                logger.info("Role couldn't be found at point {} and won't be used in synchronization",k);

            }

            discordRoles.put(k,role);

        });

        RolesToSyncConverted = discordRoles;

    }

    private void addBaseRoleToList(List<Role> MemberActiveRoles){

        Role base_role = RolesToSyncConverted.get("base_role");

        if (base_role != null) MemberActiveRoles.add(base_role);

    }


    private List<Role> getMemberOtherRoles(List<Role> MemberRoles){

        MemberRoles.removeIf(role ->
                RolesToSyncConverted.containsValue(role)
        );

        return MemberRoles;

    }

    private void checkMemberNick(Member member, String user_mc_name){

        String member_nick = member.getNickname();

        if(!Objects.equals(member_nick, user_mc_name)){

            guild.modifyNickname(member,user_mc_name).queue();

        }

    }

    private void checkMemberRoles(Member member, String user_discord_id){

        List<Role> MemberActiveRoles = getMemberOtherRoles(new ArrayList<>(member.getRoles()));

        databaseDiscordMethods.executeStatement("SELECT * FROM minecraft_to_discord WHERE discord_user_id = ?",List.of(user_discord_id),"primary_group").thenAccept(primary_group ->{

            if(primary_group != null){

                Role primary_role = RolesToSyncConverted.get(primary_group);

                givePrimaryRole(primary_role,MemberActiveRoles,member);

            }

            else{

                logger.warn("Primary group wasn't found");

            }

        });

    }

    public void MemberBackToDefault(String user_discord_id){

        Member member = guild.getMemberById(user_discord_id);

        if (member != null) {

            if (!UserLeaveNickname) guild.modifyNickname(member,null).queue();

            List<Role> MemberActiveRoles = getMemberOtherRoles(new ArrayList<>(member.getRoles()));

            addBaseRoleToList(MemberActiveRoles);

            guild.modifyMemberRoles(member,MemberActiveRoles).queue();

        }

    }


    public void MemberUpdateUUID(String user_discord_id, UUID uuid){

        Optional<Player> player = server.getPlayer(uuid);

        player.ifPresent(value -> MemberUpdateName(user_discord_id, value.getUsername()));

    }

    public void MemberUpdateName(String user_discord_id,String user_mc_name){

        Member member = guild.getMemberById(user_discord_id);

        if (member != null) {

            checkMemberNick(member,user_mc_name);

            checkMemberRoles(member,user_discord_id);

        }

    }

    public void start(){

        databaseDiscordMethods.getlinkedMembers().thenAcceptAsync(user_map ->{

            user_map.forEach((user_discord_id, linked_user) ->{

                Member member = guild.getMemberById(user_discord_id);

                if(member != null){

                    List<Role> roles = member.getRoles();

                    Role primary_role = RolesToSyncConverted.get(linked_user.primary_group());

                    if(!roles.contains(primary_role)){

                        List<Role> other_roles = getMemberOtherRoles(new ArrayList<>(roles));
                        givePrimaryRole(primary_role,other_roles,member);

                    }

                    checkMemberNick(member, linked_user.username());

                }


            });

        });

    }

    private void givePrimaryRole(Role primary_role, List<Role> MemberOtherRoles, Member member){

        Role default_role = RolesToSyncConverted.get("default");

        if(primary_role != null){

            logger.info("user {} has been given role {}", member.getEffectiveName(), primary_role.getName());

            if (primary_role == default_role){

                MemberOtherRoles.add(default_role);

            }

            else {

                MemberOtherRoles.add(primary_role);

                if(DefaultRole) MemberOtherRoles.add(default_role);

            }

        }
        guild.modifyMemberRoles(member,MemberOtherRoles).queue();
    }


}
