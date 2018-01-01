package com.headhonchos.DBConnectionManager;

import com.headhonchos.GlobalInstances;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger((Class)PersistentConnectionManager.class);
    private static Connection abcReadConnection;
    private static Connection abcResumeSkillConnection;
    private static Connection abcWriteConnection;
    private static Connection abcLargeConnection;
    private static Connection abcLargeTempWriteConnection;
    private static String APPS = System.getProperty("APPS");

    public static Connection getAbcLargeConnection() {
        boolean isConnectionAlive = PersistentConnectionManager.checkConnectionStatus(abcLargeConnection);
        if (!isConnectionAlive) {
            abcLargeConnection = PersistentConnectionManager.createNewConnection(GlobalInstances.SLAVE_ABC_LARGE, GlobalInstances.SLAVE_ABC_LARGE_USER, GlobalInstances.SLAVE_ABC_LARGE_PASSWORD);
        }
        return abcLargeConnection;
    }

    public static Connection getAbcLargeTempWriteConnection() {
        boolean isConnectionAlive = PersistentConnectionManager.checkConnectionStatus(abcLargeTempWriteConnection);
        if (!isConnectionAlive) {
            abcLargeTempWriteConnection = PersistentConnectionManager.createNewConnection(GlobalInstances.MASTER_ABC_LARGE_TEMP, GlobalInstances.MASTER_ABC_LARGE_TEMP_USER, GlobalInstances.MASTER_ABC_LARGE_TEMP_PASSWORD);
        }
        return abcLargeTempWriteConnection;
    }

    public static Connection getAbcReadConnection() {
        boolean isConnectionAlive = PersistentConnectionManager.checkConnectionStatus(abcReadConnection);
        if (!isConnectionAlive) {
            abcReadConnection = PersistentConnectionManager.createNewConnection(GlobalInstances.SLAVE_ABC, GlobalInstances.SLAVE_ABC_USER, GlobalInstances.SLAVE_ABC_PASSWORD);
        }
        return abcReadConnection;
    }

    public static Connection getAbcWriteConnection() {
        boolean isConnectionAlive = PersistentConnectionManager.checkConnectionStatus(abcWriteConnection);
        if (!isConnectionAlive) {
            abcWriteConnection = PersistentConnectionManager.createNewConnection(GlobalInstances.MASTER_ABC, GlobalInstances.MASTER_ABC_USER, GlobalInstances.MASTER_ABC_PASSWORD);
        }
        return abcWriteConnection;
    }

    public static Connection getAbcResumeSkillConnection() {
        boolean isConnectionAlive = PersistentConnectionManager.checkConnectionStatus(abcResumeSkillConnection);
        if (!isConnectionAlive) {
            abcResumeSkillConnection = PersistentConnectionManager.createNewConnection(GlobalInstances.MASTER_ABC_RESUME_SKILL, GlobalInstances.MASTER_ABC_RESUME_SKILL_USER, GlobalInstances.MASTER_ABC_RESUME_SKILL_PASSWORD);
        }
        return abcResumeSkillConnection;
    }

    private static Connection createNewConnection(String url, String userName, String password) {
        logger.debug("Creating new connection.url={},user={}", (Object)url, (Object)userName);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, password);
        }
        catch (SQLException e) {
            logger.error("Error creating new Database Connection.", (Throwable)e);
        }
        return connection;
    }

    private static boolean checkConnectionStatus(Connection connection) {
        boolean isConnectionAlive = false;
        try {
            isConnectionAlive = connection != null && !connection.isClosed();
            logger.trace("isConnectionAlive status - {}", (Object)isConnectionAlive);
        }
        catch (SQLException e) {
            logger.error("Error checking database connection status.", (Throwable)e);
        }
        return isConnectionAlive;
    }

    public static void cleanup() {
        logger.info("Cleaning Database");
        try {
            if (abcLargeConnection != null && !abcLargeConnection.isClosed()) {
                logger.info("closing - AbcLargeConnection");
                abcLargeConnection.close();
            }
        }
        catch (SQLException e) {
            logger.warn("error closing AbcLargeConnection.");
        }
        try {
            if (abcLargeTempWriteConnection != null && !abcLargeTempWriteConnection.isClosed()) {
                logger.info("closing - AbcLargeTempWriteConnection");
                abcLargeTempWriteConnection.close();
            }
        }
        catch (SQLException e) {
            logger.warn("error closing AbcLargeTempWriteConnection.");
        }
        try {
            if (abcWriteConnection != null && !abcWriteConnection.isClosed()) {
                logger.info("closing - AbcWriteConnection");
                abcWriteConnection.close();
            }
        }
        catch (SQLException e) {
            logger.warn("error closing AbcWriteConnection.");
        }
        try {
            if (abcResumeSkillConnection != null && !abcResumeSkillConnection.isClosed()) {
                logger.info("closing - AbcResumeSkillConnection");
                abcResumeSkillConnection.close();
            }
        }
        catch (SQLException e) {
            logger.warn("error closing AbcResumeSkillConnection.");
        }
        try {
            if (abcReadConnection != null && !abcReadConnection.isClosed()) {
                logger.info("closing - AbcReadConnection");
                abcReadConnection.close();
            }
        }
        catch (SQLException e) {
            logger.warn("error closing AbcReadConnection.");
        }
    }

    static {
        logger.debug("Init PersistentConnectionManager Class.");
        Properties properties = new Properties();
        try {
            logger.info("Load DB variables from configs/datalayer_db.txt file");
            String dbPath = APPS + "configs/datalayer_db.txt";
            properties.load(new FileReader(new File(dbPath)));
            GlobalInstances.DRIVER = properties.getProperty("DRIVER");
            GlobalInstances.SLAVE_ABC = properties.getProperty("SLAVE_ABC");
            GlobalInstances.SLAVE_ABC_USER = properties.getProperty("SLAVE_ABC_USER");
            GlobalInstances.SLAVE_ABC_PASSWORD = properties.getProperty("SLAVE_ABC_PASSWORD");
            GlobalInstances.MASTER_ABC = properties.getProperty("MASTER_ABC");
            GlobalInstances.MASTER_ABC_USER = properties.getProperty("MASTER_ABC_USER");
            GlobalInstances.MASTER_ABC_PASSWORD = properties.getProperty("MASTER_ABC_PASSWORD");
            GlobalInstances.MASTER_ABC_RESUME_SKILL = properties.getProperty("MASTER_ABC_RESUME_SKILL");
            GlobalInstances.MASTER_ABC_RESUME_SKILL_USER = properties.getProperty("MASTER_ABC_RESUME_SKILL_USER");
            GlobalInstances.MASTER_ABC_RESUME_SKILL_PASSWORD = properties.getProperty("MASTER_ABC_RESUME_SKILL_PASSWORD");
            GlobalInstances.SLAVE_ABC_LARGE = properties.getProperty("SLAVE_ABC_LARGE");
            GlobalInstances.SLAVE_ABC_LARGE_USER = properties.getProperty("SLAVE_ABC_LARGE_USER");
            GlobalInstances.SLAVE_ABC_LARGE_PASSWORD = properties.getProperty("SLAVE_ABC_LARGE_PASSWORD");
        }
        catch (IOException e) {
            logger.error("Exception Reading Property File", (Throwable)e);
        }
        logger.debug("ABC READ DATABASE {} ", (Object)GlobalInstances.SLAVE_ABC);
        logger.debug("ABC WRITE DATABASE {} ", (Object)GlobalInstances.MASTER_ABC);
        logger.debug("ABC_RESUME_SKILL_MASTER {}", (Object)GlobalInstances.MASTER_ABC_RESUME_SKILL);
        logger.debug("ABC_SLAVE_ABC_LARGE {}", (Object)GlobalInstances.SLAVE_ABC_LARGE);
        try {
            logger.debug("Logging Driver {}", (Object)GlobalInstances.DRIVER);
            Class.forName(GlobalInstances.DRIVER).newInstance();
        }
        catch (InstantiationException e) {
            logger.error("Error in Driver Loading.", (Throwable)e);
        }
        catch (IllegalAccessException e) {
            logger.error("Error in Driver Loading.", (Throwable)e);
        }
        catch (ClassNotFoundException e) {
            logger.error("Error in Driver Loading.", (Throwable)e);
        }
    }
}