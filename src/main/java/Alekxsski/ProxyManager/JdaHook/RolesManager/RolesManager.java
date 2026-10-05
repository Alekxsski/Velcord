package Alekxsski.ProxyManager.JdaHook.RolesManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Setter;
import Alekxsski.Utils.Config;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;

import java.util.*;


@Singleton
public class RolesManager {


    private final Logger logger;

    private final ProxyServer server;

    private final Map<String, String> RolesToSync;

    private Map<String, Role> RolesToSyncConverted;

    private final Boolean DefaultRole;

    private final Boolean UserLeaveNickname;

    @Setter
    private Guild guild;

    @Setter
    private Member self;

    @Inject
    public RolesManager(Config config, Logger logger, ProxyServer server){

        this.logger = logger;

        this.server = server;

        RolesToSync = config.getRolesToSync();

        DefaultRole = config.isDefaultRole();

        UserLeaveNickname = config.getUserLeaveNickname();

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

    private void addPrimaryRolesToList(Role primary_role, List<Role> MemberRolesList){

        Role default_role = RolesToSyncConverted.get("default");

        addBaseRoleToList(MemberRolesList);

        if(primary_role != null){

            if (primary_role == default_role){

                MemberRolesList.add(default_role);

            }

            else {

                MemberRolesList.add(primary_role);

                if(DefaultRole) MemberRolesList.add(default_role);

            }

        }

    }


    private List<Role> getMemberUnsychronizedRoles(List<Role> MemberRoles){

        MemberRoles.removeIf(role ->
                RolesToSyncConverted.containsValue(role)
        );

        return MemberRoles;

    }

    public void checkExistingMemberRoles(Member member, String primary_group) throws Exception {

        List<Role> MemberActiveRoles = getMemberUnsychronizedRoles(new ArrayList<>(member.getRoles()));

        if(primary_group != null){

            Role primary_role = RolesToSyncConverted.get(primary_group);

            givePrimaryRole(primary_role,MemberActiveRoles,member);

        }

        else{

            throw new Exception("Primary group wasn't found");

        }


    }

    public void checkExistingMemberNick(Member member, String user_mc_name){

        String member_nick = member.getNickname();

        if(!Objects.equals(member_nick, user_mc_name)){

            guild.modifyNickname(member,user_mc_name).queue();

        }

    }

    public void checkExistingMember(String user_discord_id, String user_mc_name, String primary_role) throws Exception {

        Member member = guild.getMemberById(user_discord_id);
        if (member != null && canModify(member)){

            checkExistingMemberNick(member, user_mc_name);

            checkExistingMemberRoles(member,primary_role);


        }


    }

    private void addNewMember(UserSnowflake user,String user_mc_name,String primary_group, String accessToken) {

        List<Role> rolesToBeAdded = new ArrayList<>();

        Role primary_role = RolesToSyncConverted.get(primary_group);

        addPrimaryRolesToList(primary_role,rolesToBeAdded);

        guild.addMember(accessToken,user).setNickname(user_mc_name).setRoles(rolesToBeAdded).queue();

    }

    public void OAuth2Member(String user_discord_id,String user_mc_name,String primary_group, String accessToken) throws Exception {

        UserSnowflake user = UserSnowflake.fromId(user_discord_id);

        if(!guild.isMember(user)){

            addNewMember(user,user_mc_name,primary_group,accessToken);

        }
        else{

            checkExistingMember(user_discord_id,user_mc_name,primary_group);

        }

    }


    public void setMemberBackToDefault(String user_discord_id){

        Member member = guild.getMemberById(user_discord_id);

        if (member != null && canModify(member)) {

            if (!UserLeaveNickname) guild.modifyNickname(member,null).queue();

            List<Role> MemberActiveRoles = getMemberUnsychronizedRoles(new ArrayList<>(member.getRoles()));

            addBaseRoleToList(MemberActiveRoles);

            guild.modifyMemberRoles(member,MemberActiveRoles).queue();

        }

    }


    public void makeMemberUpdateUUID(String user_discord_id, UUID uuid){

        Optional<Player> player = server.getPlayer(uuid);

        player.ifPresent(value -> {
            try {
                makeMemberUpdatePlayerName(user_discord_id, value.getUsername());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void makeMemberUpdatePlayerName(String user_discord_id, String user_mc_name) throws Exception {

        Member member = guild.getMemberById(user_discord_id);

        if (member != null && canModify(member)) {

            checkExistingMemberNick(member,user_mc_name);

            checkExistingMemberRoles(member,user_discord_id);

        }

    }

    private void givePrimaryRole(Role primary_role, List<Role> MemberOtherRoles, Member member){

        addPrimaryRolesToList(primary_role,MemberOtherRoles);

        guild.modifyMemberRoles(member,MemberOtherRoles).queue();

    }


    private boolean canModify(Member member){

        return self.canInteract(member);

    }


}
