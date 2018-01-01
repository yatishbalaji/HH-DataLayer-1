package com.headhonchos;

/**
 * Created by richa on 22/5/14.
 */
public class DBConnection {

    DatabaseOps databaseOps = null;

    public DatabaseOps getMasterConnection(String databaseName) {
        try {
            if (databaseName.trim().equalsIgnoreCase("abc")) {
                databaseOps = new DatabaseOps(GlobalInstances.MASTER_ABC, GlobalInstances.MASTER_ABC_USER, GlobalInstances.MASTER_ABC_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_resume_skill")) {
                databaseOps = new DatabaseOps(GlobalInstances.MASTER_ABC_RESUME_SKILL, GlobalInstances.MASTER_ABC_RESUME_SKILL_USER, GlobalInstances.MASTER_ABC_RESUME_SKILL_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_large")) {
                databaseOps = new DatabaseOps(GlobalInstances.MASTER_ABC_LARGE, GlobalInstances.MASTER_ABC_LARGE_USER, GlobalInstances.MASTER_ABC_LARGE_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_education")) {
                databaseOps = new DatabaseOps(GlobalInstances.MASTER_ABC_EDUCATION, GlobalInstances.MASTER_ABC_EDUCATION_USER, GlobalInstances.MASTER_ABC_EDUCATION_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_large_temp")) {
                databaseOps = new DatabaseOps(GlobalInstances.MASTER_ABC_LARGE_TEMP, GlobalInstances.MASTER_ABC_LARGE_TEMP_USER, GlobalInstances.MASTER_ABC_LARGE_TEMP_PASSWORD);
            }
        } catch (Exception e) {
            System.err.println("Database Connection error occurred..Exiting JVM...");
            e.printStackTrace();
        }
        return databaseOps;
    }

    public DatabaseOps getSlaveConnection(String databaseName) {
        try {
            if (databaseName.trim().equalsIgnoreCase("abc")) {
                databaseOps = new DatabaseOps(GlobalInstances.SLAVE_ABC, GlobalInstances.SLAVE_ABC_USER, GlobalInstances.SLAVE_ABC_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_resume_skill")) {
                databaseOps = new DatabaseOps(GlobalInstances.SLAVE_ABC_RESUME_SKILL, GlobalInstances.SLAVE_ABC_RESUME_SKILL_USER, GlobalInstances.SLAVE_ABC_RESUME_SKILL_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_large")) {
                databaseOps = new DatabaseOps(GlobalInstances.SLAVE_ABC_LARGE, GlobalInstances.SLAVE_ABC_LARGE_USER, GlobalInstances.SLAVE_ABC_LARGE_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_education")) {
                databaseOps = new DatabaseOps(GlobalInstances.SLAVE_ABC_EDUCATION, GlobalInstances.SLAVE_ABC_EDUCATION_USER, GlobalInstances.SLAVE_ABC_EDUCATION_PASSWORD);
            } else if (databaseName.trim().equalsIgnoreCase("abc_large_temp")) {
                databaseOps = new DatabaseOps(GlobalInstances.SLAVE_ABC_LARGE_TEMP, GlobalInstances.SLAVE_ABC_LARGE_TEMP_USER, GlobalInstances.SLAVE_ABC_LARGE_TEMP_PASSWORD);
            }
        } catch (Exception e) {
            System.err.println("Database Connection error occurred..Exiting JVM...");
            e.printStackTrace();
        }
        return databaseOps;
    }

    public void close(){
        try {
            databaseOps.close();
        }catch(Exception e){

        }
    }

}
