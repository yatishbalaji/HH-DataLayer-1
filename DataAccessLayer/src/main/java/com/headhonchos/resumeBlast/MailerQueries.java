package com.headhonchos.resumeBlast;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.List;

/**
 * Created by richa on 30/6/14.
 */
public class MailerQueries {

    String db_abc= "abc";
    String db_abc_large= "abc_large";
    DatabaseOps databaseOpsAbc = null;
    DatabaseOps databaseOpsAbcLarge = null;
    DBConnection dbConnection = null;

    public MailerQueries(){
        dbConnection = new DBConnection();
        databaseOpsAbc = dbConnection.getMasterConnection(db_abc);
        databaseOpsAbcLarge = dbConnection.getMasterConnection(db_abc_large);
    }

    public List<String> getJobSpecificDetails(){
        String query = "select cl_job_posting_id, GROUP_CONCAT(js_login_id) from resume_blast_recommendations group by cl_job_posting_id ";
        List<String> data = databaseOpsAbcLarge.getAllStrings(query, 2);
        return data;
    }

    public List<String> getJobDetails(int cl_job_posting_id){
        String query = "select ClJobPosting.id,job_title,mask_location,ClLogin.id," +
                "ClLogin.first_name,ClLogin.last_name,ClLogin.emailid," +
                "ClOrganization.name " +
                "from cl_job_postings as ClJobPosting" +
                "inner join cl_logins as ClLogin " +
                "on ClLogin.id = ClJobPosting.cl_login_id " +
                "inner join cl_organizations as ClOrganization " +
                "on ClOrganization.cl_login_id = ClLogin.id " +
                "where ClJobPosting.id = " + cl_job_posting_id ;
        List<String> data = databaseOpsAbc.getAllStrings(query, 8);
        return data;
    }


    public List<String> getJobLocations(int cl_job_posting_id) {
        String query = "select MasterCity.name from cl_job_posting_locations as ClJobPostingLocation " +
                "inner join master_cities as MasterCity " +
                "on MasterCity.id = ClJobPostingLocation.master_city_id " +
                "where cl_job_posting_id = " + cl_job_posting_id;
        List<String> data = databaseOpsAbc.getAllStrings(query, 1);
        return data;
    }

    public List<String> getJobSeekerDetails(String js_login_id) {
        String query = "select id,first_name,last_name,JsProfile.master_year_id," +
                "JsProfile.mask_total_experience,JsProfile.master_month_id," +
                "JsProfile.key_skills,JsProfile.mask_key_skills,resume_blast_count " +
                "from js_logins as JsLogin " +
                "inner join js_profiles on JsProfile " +
                "on JsProfile.js_login_id= JsLogin.id " +
                "where JsLogin.id = " + js_login_id;
        List<String> data = databaseOpsAbc.getAllStrings(query, 9);
        return data;
    }



}
