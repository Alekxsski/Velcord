package Alekxsski.Database.PlayerManagment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import Alekxsski.Database.DatabaseManager;
import org.slf4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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

        return CompletableFuture.supplyAsync(() -> {

            String value = null;

            try (Connection connection = databaseManager.getConnection()) {

                try (PreparedStatement statement = connection.prepareStatement(sql)) {

                    if (parameters != null) {
                        for (int i = 0; i < parameters.size(); i++) {
                            statement.setString(i + 1, parameters.get(i));
                        }
                    }

                    if (column_name != null) {
                        try (ResultSet result = statement.executeQuery()) {

                            if (result.next()) {
                                value = result.getString(column_name);
                            }

                        }
                    } else {
                        statement.executeUpdate();

                    }
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new CompletionException(e);
            }

            return value;

        }, databaseManager.getDbThreads());

    }


}
