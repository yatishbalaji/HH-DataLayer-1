package com.headhonchos.similarity;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.List;

/**
 * Created by richa on 6/5/14.
 */
public class GetDataForSimilarJob {

    String db_abc = "abc";
    String db_abc_resume_skill = "abc_resume_skill";
    DatabaseOps databaseOpsAbc = null;
    DatabaseOps databaseOpsAbcResSkill = null;
    DBConnection dbConnection = null;

    public GetDataForSimilarJob(){
        dbConnection = new DBConnection();
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
        databaseOpsAbcResSkill = dbConnection.getSlaveConnection(db_abc_resume_skill);
    }

    public String getQuery(String cl_job_posting_id){
        String query = "select main_query from job_parsed where cl_job_posting_id=" + cl_job_posting_id;
        String main_qry = "";
        try {
            main_qry = databaseOpsAbcResSkill.getString(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return main_qry;
    }

    public List<String> getSalaryExp(String cjid) {
        String qry = "select annual_salary_min,annual_salary_max,work_experience_min,work_experience_max from cl_job_postings where id=" + cjid;
        List<String> data = databaseOpsAbc.getAllStrings(qry, 4);
        return  data;
    }

    public void close(){
        databaseOpsAbc.close();
        databaseOpsAbcResSkill.close();
    }

}
