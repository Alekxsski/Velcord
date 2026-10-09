package Alekxsski.Database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import Alekxsski.ProxyManager.ProxyManagement;
import Alekxsski.Utils.Config;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class DatabaseManager {

    private final ProxyManagement proxyManagement;

    private final Config config;

    private final Logger logger;

    @Getter
    private ExecutorService dbThreads;

    private HikariDataSource database;

    private String username;

    private String password;

    private int MaximumPoolSize;

    private int MinimumIdle;

    private int IdleTimeout;

    private int MaxLifetime;

    private String Driver;

    private String DatabaseAddress;

    private String DatabasePort;

    private String DatabaseName;

    private final Path dataDirectory;

    @Inject
    public DatabaseManager(ProxyManagement proxyManagement, Logger logger, Config config,@DataDirectory Path dataDirectory){

        this.proxyManagement = proxyManagement;

        this.logger = logger;

        this.config = config;

        this.dataDirectory = dataDirectory;

    }

    public void initialize() throws IOException {

        gettingData();

        database = establishDatabase();

        configure();

    }

    public void gettingData(){

        username = config.getUsername();

        password = config.getPassword();

        MaximumPoolSize = config.getMaximumPoolSize();

        MinimumIdle = config.getMinimumIdle();

        IdleTimeout = config.getIdleTimeout();

        MaxLifetime = config.getMaxLifetime();

        Driver = config.getDriver();

        DatabaseAddress = config.getDatabaseAddress();

        DatabasePort = config.getDatabasePort();

        DatabaseName = config.getDatabaseName();

    }

    private void setUpThreads(int threads){

        dbThreads = Executors.newFixedThreadPool(threads);

    }

    private String getDriver() {

        return switch (Driver) {
            case "mysql" -> "com.mysql.cj.jdbc.Driver";
            case "mariadb" -> "org.mariadb.jdbc.Driver";
            default -> "org.sqlite.JDBC";
        };
    }

    private HikariDataSource establishDatabase() throws IOException {

        HikariConfig config = new HikariConfig();

        String driver = getDriver();

        config.setDriverClassName(driver);

        logger.info(Driver);

        if ( Driver.equals("mysql") || Driver.equals("mariadb") ){

            setUpThreads(MaximumPoolSize);

            config.setUsername(username);

            config.setPassword(password);

            config.setMaximumPoolSize(MaximumPoolSize);

            config.setMinimumIdle(MinimumIdle);

            config.setIdleTimeout(IdleTimeout);

            config.setMaxLifetime(MaxLifetime);

            String url_base = "jdbc:%s://%s:%s/%s";

            config.setJdbcUrl(String.format(url_base,Driver,DatabaseAddress,DatabasePort,DatabaseName));

        }

        else{

            createDbSqlite();
            setUpThreads(1);
            config.setMaximumPoolSize(1);
            config.setJdbcUrl("jdbc:sqlite:velcord.db");

        }

        config.setPoolName("Velcord_Pool");

        logger.info("Plugin successfully connected to the database");

        return new HikariDataSource(config);

    }

    private void createDbSqlite() throws IOException {

        File db = new File(dataDirectory.toFile(),"velcord.db");

        if(db.createNewFile()){

            logger.info("Database file was created and will be used");

        }

        else{

            logger.info("Database file was found and will be used");

        }


    }

    public void configure() {

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {

            String statement1 = "CREATE TABLE IF NOT EXISTS player_discord(uuid varchar(64),discord_user_id varchar(20),PRIMARY KEY (uuid))";
            String statement2 = "DROP TABLE IF EXISTS auth_codes";
            String statement3 = "CREATE TABLE auth_codes(uuid varchar(64),username varchar(16),primary_group varchar(36),code varchar(16),PRIMARY KEY (uuid))";

            statement.executeUpdate(statement1);
            statement.executeUpdate(statement2);
            statement.executeUpdate(statement3);

            logger.info("Velcord Velocity Plugin Successfully configured database!");

        }catch (SQLException e){

            proxyManagement.shutDown("Velcord has Failed to connect to configure database",e);

        }
    }

    public Connection getConnection() throws SQLException {

        return database.getConnection();

    }

    public void shutDown(){

        database.close();
        dbThreads.shutdown();
        logger.info("Database has successfully shut down");

    }



}
