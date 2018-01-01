package com.headhonchos.prediction;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by richa on 21/8/14.
 */
public class ClassificationOperations {

    String db_abc = "abc";
    String db_abc_resume_skill = "abc_resume_skill";
    DatabaseOps databaseOpsAbc = null;
    DatabaseOps databaseOpsAbcResSkill = null;
    DatabaseOps databaseOpsAbcResSkillWrite = null;
    DBConnection dbConnection = null;

    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ClassificationOperations() {
        dbConnection = new DBConnection();
    }

    public List<String> getIndusFunctionId(boolean industry) {
        String query = "";
        if(industry == true){
            query = "select master_industry_id,js_login_id from js_employments where currently_working=1 and master_industry_id is not NULL and js_login_id is not null order by js_login_id";
        }
        else {
            query = "select master_functional_area_id,js_login_id from js_employments where currently_working=1 and master_functional_area_id is not NULL and js_login_id is not null order by js_login_id";
        }
        setAbcConnection();
        List<String> data = databaseOpsAbc.getAllStrings(query,2);
        closeAbcConnection();
        return data;
    }

    public List<String> getIndusFuncGroup(boolean industry) {
        String query = "";
        if(industry == true){
            query = "select id,name from broad_industry_grouping where category != 'XXXX' order by id asc";
        }
        else {
            query = "select id,name from broad_function_grouping where category != 'XXXX' order by id asc";
        }
        setAbcResumeSkillConnection();
        List<String> data = databaseOpsAbc.getAllStrings(query,2);
        closeAbcResumeSkillConnection();
        return data;
    }

    public List<String> getAllSkillls() {
        setAbcResumeSkillConnection();
        String query = "Select name,id from jobseeker_skills";
        List<String> data = databaseOpsAbcResSkill.getAllStrings(query,2);
        closeAbcResumeSkillConnection();
        return data;
    }

    public String getJobClassifySkill(String cl_job_posting_id) {
        setAbcResumeSkillConnection();
        String query = "select job_skills from job_parsed where cl_job_posting_id=" + cl_job_posting_id;
        String skill = databaseOpsAbcResSkill.getString(query);
        closeAbcResumeSkillConnection();
        return skill;
    }

    public String getResumeClassifySkill(String query) {
        setAbcResumeSkillConnection();
        String skill = databaseOpsAbcResSkill.getString(query);
        closeAbcResumeSkillConnection();
        return skill;
    }

    public void updateIndusrtyFunctionPredicted(String id, String[] func, int noOfFunction, String[] indus, int noOfIndustry) {
        setAbcResumeSkillWriteConnection();
        dt = new Date();
        String currentTime = sdf.format(dt);
        String query = "update job_parsed_temps set status=2,modified='" + currentTime + "', f1=? , f2=? , i1=? , i2=? , i3=? , i4=? , i5=? where id=?";
        PreparedStatement pst = databaseOpsAbcResSkillWrite.preparePst(query);
        int columnCount = 1;
        int columnLimit = noOfFunction;
        for (int i=0;i<func.length;i++) {
            try {
                pst.setString(columnCount, func[i]);
            }catch(SQLException e){
            }
            columnCount++;
        }
        //for industry
        columnLimit = columnLimit + noOfIndustry;
        for (int i=0;i<indus.length;i++) {
            try{
                pst.setString(columnCount, indus[i]);
            }catch(SQLException e){
            }
            columnCount++;
        }
        try {
            pst.setString(columnCount, id);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        try{
            pst.execute();
        }catch(SQLException e){
        }
        closeAbcResumeSkillWriteConnection();
    }

    private void setAbcResumeSkillConnection() {
        databaseOpsAbcResSkill = dbConnection.getSlaveConnection(db_abc_resume_skill);
    }

    private void setAbcConnection() {
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
    }

    private void setAbcResumeSkillWriteConnection() {
        databaseOpsAbcResSkillWrite = dbConnection.getMasterConnection(db_abc_resume_skill);
    }
    private void closeAbcConnection(){
        databaseOpsAbc.close();
    }

    private void closeAbcResumeSkillConnection(){
        databaseOpsAbcResSkill.close();
    }

    private void closeAbcResumeSkillWriteConnection(){
        databaseOpsAbcResSkillWrite.close();
    }
}
