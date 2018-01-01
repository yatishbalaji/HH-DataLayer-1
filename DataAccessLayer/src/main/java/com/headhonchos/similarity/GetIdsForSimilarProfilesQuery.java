package com.headhonchos.similarity;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richa on 8/8/14.
 */
public class GetIdsForSimilarProfilesQuery {

    List<String> list = new ArrayList<String>();
    String db = "abc_resume_skill";
    DatabaseOps databaseOps = null;
    DBConnection dbConnection = null;

    public GetIdsForSimilarProfilesQuery(){
        dbConnection = new DBConnection();
        databaseOps = dbConnection.getSlaveConnection(db);
    }

    public List<String> getIds(){
        String query = "select distinct(js_login_id) from jc_recommendations where js_login_id NOT IN(select js_login_id from similar_profiles) order by js_login_id";
        System.out.println("query--- " + query);
        list.clear();
        list = databaseOps.getAllStrings(query, 1);
        close();
        return list;
    }

    public void close(){
        databaseOps.close();
    }

}
