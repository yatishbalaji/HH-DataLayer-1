package com.headhonchos.similarity;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richa on 30/4/14.
 */
public class SaveSimilarProfileQueryDal {

    List<String> list = new ArrayList<String>();
    String db = "abc_resume_skill";
    DatabaseOps databaseOpsSlave = null, databaseOpsMaster = null;
    DBConnection dbConnection = null;

    public SaveSimilarProfileQueryDal(){
        dbConnection = new DBConnection();
        databaseOpsSlave = dbConnection.getSlaveConnection(db);
        databaseOpsMaster = dbConnection.getMasterConnection(db);
    }

    public boolean dbStorage(String query, String js_login_id){
        boolean update = false;
        String existQry = "select id from similar_profiles where js_login_id = "  + js_login_id;
        boolean exist = databaseOpsSlave.recordExists(existQry);
        String dbQry = "";
        if(exist == true)
            dbQry = "update similar_profiles set similar_profiles_query = '" + query + "' where js_login_id = " + js_login_id ;
        else
            dbQry = "insert into similar_profiles(js_login_id,similar_profiles_query) values(" + js_login_id + ",'" + query + "')";
        try {
            databaseOpsMaster.update(dbQry);
            update = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }

    public void close(){
        databaseOpsMaster.close();
        databaseOpsSlave.close();
    }

}
