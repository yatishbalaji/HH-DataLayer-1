package com.headhonchos.resumeBlast;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.List;

/**
 * Created by richa on 30/6/14.
 */
public class JobSeekerInfo {

    String db_abc= "abc";
    String db_abc_large= "abc_large";
    DatabaseOps databaseOpsAbc = null;
    DatabaseOps databaseOpsAbcLarge = null;
    DBConnection dbConnection = null;

    public JobSeekerInfo(){
        dbConnection = new DBConnection();
        databaseOpsAbc = dbConnection.getMasterConnection(db_abc);
        databaseOpsAbcLarge = dbConnection.getMasterConnection(db_abc_large);
    }

    public List<String> getResumeBlastCount() {
        String query = "select JsLogin.id,resume_blast_count from js_logins as JsLogin " +
                "inner join customer_products as CustomerProduct " +
                "on CustomerProduct.user_id= JsLogin.id " +
                "where JsLogin.status=1 and JsLogin.resume_blast_count < 2 and " +
                "CustomerProduct.is_active=1 and CustomerProduct.category=2 and " +
                "CustomerProduct.op_promotion_id=2 and CustomerProduct.op_product_id=2 and " +
                "CURRENT_DATE()  BETWEEN  CustomerProduct.start_date AND CustomerProduct.end_date ";
        List<String> result = databaseOpsAbc.getAllStrings(query,2);
        return result;
    }


    public String jobAlreadyExists(int jsid, int cjid){
        String query = "select id from resume_blast_recommendations " +
                "where cl_job_posting_id = " + cjid + " and js_login_id = " + jsid;
        String result = databaseOpsAbcLarge.getString(query);
        return result;
    }

    public void insertResumeBlastRecommendations(int jsid, int cl_job_posting_id, int cl_login_id) {
        String query = "insert into resume_blast_recommendations(js_login_id,cl_job_posting_id,cl_login_id) values (" + jsid + ", " + cl_job_posting_id + ", " + cl_login_id + ")";
        //System.out.println(query);
        databaseOpsAbcLarge.update(query);
    }

    public void updateJsLogins(int jsid) {
        String query = "update js_logins set resume_blast_count = resume_blast_count + 1 where id = " + jsid;
        //System.out.println(query);
        databaseOpsAbc.update(query);
    }

    public void close(){
        databaseOpsAbc.close();
        databaseOpsAbcLarge.close();
    }


}
