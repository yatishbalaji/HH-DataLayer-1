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

/**
 * Created by aman on 23/5/14.
 */
public class ConnectionManager {
    public static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private static String APPS = System.getProperty("APPS");

    static {
        Properties properties = new Properties();
        try {
            logger.info("Load DB variables from configs/datalayer_db.txt file");
            String dbPath = APPS + "configs/datalayer_db.txt";
            properties.load(new FileReader(new File(dbPath)));
            GlobalInstances.DRIVER = properties.getProperty("DRIVER");
            GlobalInstances.MASTER_ABC = properties.getProperty("MASTER_ABC");
            GlobalInstances.MASTER_ABC_USER = properties.getProperty("MASTER_ABC_USER");
            GlobalInstances.MASTER_ABC_PASSWORD = properties.getProperty("MASTER_ABC_PASSWORD");
            GlobalInstances.MASTER_ABC_RESUME_SKILL = properties.getProperty("MASTER_ABC_RESUME_SKILL");
            GlobalInstances.MASTER_ABC_RESUME_SKILL_USER = properties.getProperty("MASTER_ABC_RESUME_SKILL_USER");
            GlobalInstances.MASTER_ABC_RESUME_SKILL_PASSWORD = properties.getProperty("MASTER_ABC_RESUME_SKILL_PASSWORD");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("ABC DATABASE - "+GlobalInstances.MASTER_ABC);
        logger.debug("ABC_RESUME_SKILL_DATA -"+GlobalInstances.MASTER_ABC_RESUME_SKILL);
    }

    public static Connection getConnection(String databaseName) {

        Connection readOnlyDatabaseConnection = null;
        try {
          Class.forName(GlobalInstances.DRIVER).newInstance();

            if (databaseName.trim().equalsIgnoreCase("abc")) {
                readOnlyDatabaseConnection = DriverManager.getConnection(
                        GlobalInstances.SLAVE_ABC,
                        GlobalInstances.SLAVE_ABC_USER,
                        GlobalInstances.SLAVE_ABC_PASSWORD
                );
            } else if (databaseName.trim().equalsIgnoreCase("abc_resume_skill")) {
                readOnlyDatabaseConnection = DriverManager.getConnection(
                        GlobalInstances.SLAVE_ABC_RESUME_SKILL,
                        GlobalInstances.SLAVE_ABC_RESUME_SKILL_USER,
                        GlobalInstances.SLAVE_ABC_RESUME_SKILL_PASSWORD
                );
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return readOnlyDatabaseConnection;
    }

}
