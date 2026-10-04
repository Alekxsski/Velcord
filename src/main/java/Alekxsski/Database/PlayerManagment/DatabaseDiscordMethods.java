package Alekxsski.Database.PlayerManagment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.Database.DatabaseManager;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
public class DatabaseDiscordMethods {

    private final DatabaseManager databaseManager;

    @Inject
    public DatabaseDiscordMethods(DatabaseManager databaseManager){

        this.databaseManager = databaseManager;

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

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return optional_value_from_query;

        },databaseManager.getDbThreads());

    }


}
