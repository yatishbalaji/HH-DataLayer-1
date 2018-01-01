package com.headhonchos.jobSeeker;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

/**
 * Created by richa on 28/5/14.
 */
public class SaveGuestData {

    static String db = "abc_resume_skill";
    static DatabaseOps databaseOpsAbcResSkill = null;
    static DBConnection dbConnection = null;

    public SaveGuestData(){
        dbConnection = new DBConnection();
        databaseOpsAbcResSkill = dbConnection.getMasterConnection(db);
    }



    public void dbStorage(String[] data){
        String qry = "insert into guest_parsed_datas(session_id,resume_text,resume_path,skills,skill_cloud) values (" + data[0] + ",'" + data[1].replaceAll("'","''") + "','" + data[2] +"','" + data[3] + "','" + data[4] + "')";
        try {
            databaseOpsAbcResSkill.update(qry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close(){
        databaseOpsAbcResSkill.close();
    }
}
