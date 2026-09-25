package Alekxsski.Database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import Alekxsski.ProxyManager.ProxyManagement;
import Alekxsski.Utils.Config;
import org.slf4j.Logger;

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

    private final HikariDataSource database;

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

    @Inject
    public DatabaseManager(ProxyManagement proxyManagement, Logger logger, Config config){

        this.proxyManagement = proxyManagement;

        this.logger = logger;

        this.config = config;

        gettingData();

        dbThreads = setUpThreads();

        this.database = establishDatabase();

        initializeDatabase();

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

    private ExecutorService setUpThreads(){

        return Executors.newFixedThreadPool(MaximumPoolSize);

    }

    private HikariDataSource establishDatabase(){

        HikariConfig config = new HikariConfig();

        String driver;

        String mysql = "com.mysql.cj.jdbc.Driver";
        String mariadb = "org.mariadb.jdbc.Driver";

        if(Driver.equals("mysql")) driver = mysql;

        else driver = mariadb;

        config.setDriverClassName(driver);

        String url_base = "jdbc:%s://%s:%s/%s";

        config.setJdbcUrl(String.format(url_base,Driver,DatabaseAddress,DatabasePort,DatabaseName));

        config.setUsername(username);

        config.setPassword(password);

        config.setMaximumPoolSize(MaximumPoolSize);

        config.setMinimumIdle(MinimumIdle);

        config.setIdleTimeout(IdleTimeout);

        config.setMaxLifetime(MaxLifetime);

        config.setPoolName("Huzuni_Velocity_Pool");

        logger.info("Plugin successfully connected to the database");

        return new HikariDataSource(config);

    }

    public void shutDown(){

        database.close();
        dbThreads.shutdown();
        logger.info("Database has successfully shut down");

    }

    public Connection getConnection() throws SQLException {

        return database.getConnection();

    }

    public void initializeDatabase() {

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {

            String statement1 = "CREATE TABLE IF NOT EXISTS player_discord(uuid varchar(64),discord_user_id varchar(20),PRIMARY KEY (uuid))";
            String statement2 = "DROP TABLE IF EXISTS auth_codes";
            String statement3 = "CREATE TABLE auth_codes(uuid varchar(64),code varchar(16),PRIMARY KEY (uuid))";
            String statement4 = "CREATE VIEW IF NOT EXISTS minecraft_to_discord AS SELECT r.uuid, r.discord_user_id,u.username, u.primary_group FROM player_discord r LEFT JOIN luckperms_players u ON r.uuid = u.uuid";

            statement.executeUpdate(statement1);
            statement.executeUpdate(statement2);
            statement.executeUpdate(statement3);
            statement.executeUpdate(statement4);

            logger.info("Huzuni Velocity Plugin Successfully initialized database!");

        }catch (SQLException e){

            proxyManagement.shutDown("Huzuni has Failed to connect to initialize database",e);

        }
    }



}
