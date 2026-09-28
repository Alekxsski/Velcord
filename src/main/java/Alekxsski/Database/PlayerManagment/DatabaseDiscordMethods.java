package Alekxsski.Database.PlayerManagment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.Database.DatabaseManager;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
public class DatabaseDiscordMethods {

    private final DatabaseManager databaseManager;
    private final Logger logger;

    @Inject
    public DatabaseDiscordMethods(DatabaseManager databaseManager, Logger logger){

        this.databaseManager = databaseManager;
        this.logger = logger;

    }

    public CompletableFuture<String> executeStatement(String sql, List<String> parameters, String column_name){

        return CompletableFuture.supplyAsync( () ->{

            String optional_value_from_query = null;

            try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

                if (parameters != null){

                    int index = 0;

                    while(parameters.size() > index){

                        try {
                            statement.setString(index+1, parameters.get(index));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        index++;

                    }
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

                return "error";
            }

            return optional_value_from_query;

        },databaseManager.getDbThreads());

    }


}
