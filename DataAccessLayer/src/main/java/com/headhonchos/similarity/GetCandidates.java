package com.headhonchos.similarity;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.List;

/**
 * Created by richa on 1/7/14.
 */
public class GetCandidates {

    String db_abc = "abc";
    DatabaseOps databaseOpsAbc = null;
    DBConnection dbConnection = null;

    public GetCandidates(){
        dbConnection = new DBConnection();
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
    }

    public List<String> getCandidateIds() {
        String query = "select js_logins.id from js_logins " +
                "left join js_settings on js_settings.js_login_id=js_logins.id " +
                "where js_settings.email_alerts_status=1 and " +
                "js_logins.work_ex_finish=1 " +
                "and js_logins.education_finish=1 " +
                "and js_logins.status!=2 " +
                "and js_logins.status!=5 " +
                "and js_logins.status!=6 " +
                "and js_logins.id not in (SELECT js_login_id " +
                "FROM abc_large.mahout_similar_job_recommendations " +
                "where date(created) = date(now()) )";
        List<String> data = databaseOpsAbc.getAllStrings(query,1);
        return data;
    }

    public void close(){
        databaseOpsAbc.close();
    }
}
