package Alekxsski.Database.PlayerManagment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.Database.DatabaseManager;
import Alekxsski.ProxyManager.JdaHook.Utils.LinkedUser;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Singleton
public class DatabaseDiscordMethods {

    private final DatabaseManager databaseManager;
    private final Logger logger;
    private final Executor dbThreads;

    @Inject
    public DatabaseDiscordMethods(DatabaseManager databaseManager, Logger logger){

        this.databaseManager = databaseManager;
        this.logger = logger;
        this.dbThreads = databaseManager.getDbThreads();

    }

    public CompletableFuture<String> executeStatement(String sql, List<String> parameters, String column_name){

        return CompletableFuture.supplyAsync( () ->{

            String optional_value_from_query = null;

            try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

                int index = 0;

                while(parameters.size() > index){

                    try {
                        statement.setString(index+1, parameters.get(index));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    index++;

                }

                if (column_name != null) {

                    ResultSet res = statement.executeQuery();

                    if (res.next()){

                        optional_value_from_query = res.getString(column_name);

                    }

                }

                else statement.executeUpdate();

            }catch (SQLException e){

                logger.error("Yep Error's here",e);
            }

            return optional_value_from_query;

        },dbThreads);

    }

    public CompletableFuture<HashMap<String,LinkedUser>> getlinkedMembers(){

        return CompletableFuture.supplyAsync( ()-> {

            HashMap<String,LinkedUser> users = new HashMap<>();

            try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT discord_user_id, username, primary_group FROM minecraft_to_discord")) {

                ResultSet result = statement.executeQuery();

                while (result.next()){

                    String discord_user_id = result.getString("discord_user_id");
                    String username = result.getString("username");
                    String primary_group = result.getString("primary_group");

                    users.put(discord_user_id,new LinkedUser(username,primary_group));

                }
                return users;


            }catch (SQLException e){

                logger.error("Yep Error's here",e);
                return users;
            }

        },dbThreads);

    }

}
