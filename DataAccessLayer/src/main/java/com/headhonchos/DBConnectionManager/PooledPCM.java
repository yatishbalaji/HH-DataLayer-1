package com.headhonchos.DBConnectionManager;

import com.headhonchos.GlobalInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ishu on 13/11/14.
 * <p>
 * For pooled connection at following databases:
 * 1. MASTER_ABC
 * 2. SLAVE_ABC
 * 3. MASTER_ABC_RESUME_SKILL
 * 4. SLAVE_ABC_LARGE
 */
public class PooledPCM {
    private static final Logger logger = LoggerFactory.getLogger(PooledPCM.class);
    private static String APPS = System.getProperty("APPS");

    //static block reads database credentials and loads the jdbc driver
    static {
        logger.debug("Init " + PooledPCM.class);
        Properties properties = new Properties();
        try {
            logger.info("Load DB variables from configs/datalayer_db.txt file");
            String dbPath = APPS + "configs/datalayer_db.txt";
            properties.load(new FileReader(new File(dbPath)));
            //Read Driver
            GlobalInstances.DRIVER = properties.getProperty("DRIVER");

            //abc read only database
            GlobalInstances.SLAVE_ABC = properties.getProperty("SLAVE_ABC");
            GlobalInstances.SLAVE_ABC_USER = properties.getProperty("SLAVE_ABC_USER");
            GlobalInstances.SLAVE_ABC_PASSWORD = properties.getProperty("SLAVE_ABC_PASSWORD");

            //abc write database
            GlobalInstances.MASTER_ABC = properties.getProperty("MASTER_ABC");
            GlobalInstances.MASTER_ABC_USER = properties.getProperty("MASTER_ABC_USER");
            GlobalInstances.MASTER_ABC_PASSWORD = properties.getProperty("MASTER_ABC_PASSWORD");

            //abc_resume_skill database
            GlobalInstances.MASTER_ABC_RESUME_SKILL = properties.getProperty("MASTER_ABC_RESUME_SKILL");
            GlobalInstances.MASTER_ABC_RESUME_SKILL_USER = properties.getProperty("MASTER_ABC_RESUME_SKILL_USER");
            GlobalInstances.MASTER_ABC_RESUME_SKILL_PASSWORD = properties.getProperty("MASTER_ABC_RESUME_SKILL_PASSWORD");

            GlobalInstances.SLAVE_ABC_LARGE = properties.getProperty("SLAVE_ABC_LARGE");
            GlobalInstances.SLAVE_ABC_LARGE_USER = properties.getProperty("SLAVE_ABC_LARGE_USER");
            GlobalInstances.SLAVE_ABC_LARGE_PASSWORD = properties.getProperty("SLAVE_ABC_LARGE_PASSWORD");

        } catch (IOException e) {
            logger.error("Exception Reading Property File", e);
        }
        logger.debug("Success in reading file");
        logger.debug("ABC READ DATABASE {} ", GlobalInstances.SLAVE_ABC);
        logger.debug("ABC WRITE DATABASE {} ", GlobalInstances.MASTER_ABC);
        logger.debug("ABC_RESUME_SKILL_MASTER {}", GlobalInstances.MASTER_ABC_RESUME_SKILL);
        logger.debug("ABC_SLAVE_ABC_LARGE {}", GlobalInstances.SLAVE_ABC_LARGE);

        try {
            logger.debug("Registered Driver {}", GlobalInstances.DRIVER);
            Class.forName(GlobalInstances.DRIVER).newInstance();
        } catch (InstantiationException e) {
            logger.error("Error in Driver Loading.", e);
        } catch (IllegalAccessException e) {
            logger.error("Error in Driver Loading.", e);
        } catch (ClassNotFoundException e) {
            logger.error("Error in Driver Loading.", e);
        }
    }

    private static BlockingQueue<Connection> MASTER_ABC;
    private static BlockingQueue<Connection> SLAVE_ABC;
    private static BlockingQueue<Connection> MASTER_ABC_RESUME_SKILL;
    private static BlockingQueue<Connection> SLAVE_ABC_LARGE;
    private static BlockingQueue<Connection> MASTER_ABC_LARGE;

    public void init_MASTER_ABC(int poolSize) {
        //populate MASTER_ABC pool
        MASTER_ABC = new ArrayBlockingQueue<Connection>(poolSize);
        for (int i = 0; i < 9; i++) {
            MASTER_ABC.add(createNewConnection(GlobalInstances.MASTER_ABC,
                    GlobalInstances.MASTER_ABC_USER,
                    GlobalInstances.MASTER_ABC_PASSWORD));
        }
    }

    public void init_SLAVE_ABC(int poolSize) {
        //populate SLAVE_ABC pool
        SLAVE_ABC = new ArrayBlockingQueue<Connection>(poolSize);
        for (int i = 0; i < 9; i++) {
            SLAVE_ABC.add(createNewConnection(GlobalInstances.SLAVE_ABC,
                    GlobalInstances.SLAVE_ABC_USER,
                    GlobalInstances.SLAVE_ABC_PASSWORD));
        }
    }

    public void init_MASTER_ABC_RESUME_SKILL(int poolSize) {
        //populate MASTER_ABC_RESUME_SKILL pool
        MASTER_ABC_RESUME_SKILL = new ArrayBlockingQueue<Connection>(poolSize);
        for (int i = 0; i < 9; i++) {
            MASTER_ABC_RESUME_SKILL.add(createNewConnection(GlobalInstances.MASTER_ABC_RESUME_SKILL,
                    GlobalInstances.MASTER_ABC_RESUME_SKILL_USER,
                    GlobalInstances.MASTER_ABC_RESUME_SKILL_PASSWORD
            ));
        }
    }

    public void init_SLAVE_ABC_LARGE(int poolSize) {
        //populate SLAVE_ABC_LARGE pool
        SLAVE_ABC_LARGE = new ArrayBlockingQueue<Connection>(poolSize);
        for (int i = 0; i < 9; i++) {
            SLAVE_ABC_LARGE.add(createNewConnection(GlobalInstances.SLAVE_ABC_LARGE,
                    GlobalInstances.SLAVE_ABC_LARGE_USER,
                    GlobalInstances.SLAVE_ABC_LARGE_PASSWORD));
        }
    }

    public void init_MASTER_ABC_LARGE(int poolSize) {
        //populate SLAVE_ABC_LARGE pool
        MASTER_ABC_LARGE = new ArrayBlockingQueue<Connection>(poolSize);
        for (int i = 0; i < 9; i++) {
            MASTER_ABC_LARGE.add(createNewConnection(GlobalInstances.MASTER_ABC_LARGE_TEMP,
                    GlobalInstances.MASTER_ABC_LARGE_TEMP_USER,
                    GlobalInstances.MASTER_ABC_LARGE_TEMP_PASSWORD));
        }
    }

    public BlockingQueue<Connection> getAbcLargeConnectionPool() throws InterruptedException {
        return SLAVE_ABC_LARGE;
    }

    public BlockingQueue<Connection> getAbcLargeTempWritePool() throws InterruptedException {
        return MASTER_ABC_LARGE;
    }

    public BlockingQueue<Connection> getAbcReadConnectionPool() throws InterruptedException {
        return SLAVE_ABC;
    }

    public BlockingQueue<Connection> getAbcWriteConnectionPool() throws InterruptedException {
        return MASTER_ABC;
    }

    public BlockingQueue<Connection> getAbcResumeSkillConnectionPool() throws InterruptedException {
        return MASTER_ABC_RESUME_SKILL;
    }

    private static Connection createNewConnection(String url, String userName, String password) {
        logger.debug("Creating new connection.url={},user={}", url, userName);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            logger.error("Error creating new Database Connection.", e);
        }
        return connection;
    }

    private static boolean checkConnectionStatus(Connection connection) {
        logger.trace("Checking Connection status.");
        boolean isConnectionAlive = false;
        try {
            isConnectionAlive = connection != null && !connection.isClosed();
            logger.trace("isConnectionAlive status - {}", isConnectionAlive);
        } catch (SQLException e) {
            logger.error("Error checking database connection status.", e);
        }
        return isConnectionAlive;
    }
}
