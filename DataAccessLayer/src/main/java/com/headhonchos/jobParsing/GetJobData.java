package com.headhonchos.jobParsing;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by richa on 20/8/14.
 */
public class GetJobData {

    String db_abc = "abc";
    String db_abc_resume_skill = "abc_resume_skill";
    DatabaseOps databaseOpsAbc = null;
    DatabaseOps databaseOpsAbcResSkill = null;
    DBConnection dbConnection = null;

    Date dt = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public GetJobData() {
        dbConnection = new DBConnection();
    }

    public int getSource(String id)
    {
        // 0 naukri link paste
        // 1 jd paste or file added
        int flag;
        setAbcResumeSkillConnection();
        String selectQuery="select naukrilink from job_parsed_temps where id='"+id+"'";
        String result = databaseOpsAbcResSkill.getString(selectQuery);
        //naukri link added
        if(result!=null)
            flag = 0;
            // jd content paste or file added
        else
            flag = 1;
        closeAbcResumeSkill();
        return flag;
    }

    public String getNaukriLink(String id)
    {
        setAbcResumeSkillConnection();
        String selectQuery="select naukrilink from job_parsed_temps where id='"+id+"'";
        String url = databaseOpsAbcResSkill.getString(selectQuery);
        closeAbcResumeSkill();
        return url;
    }

    public void insertNaukriData(HashMap<Integer,String> map,int noOfColumns, String id)
    {
        dt = new Date();
        String currentTime = sdf.format(dt);
        setAbcResumeSkillConnection();
        PreparedStatement pst = databaseOpsAbcResSkill.preparePst("update job_parsed_temps set status=3,job_title=?,experience=?,location=?,salary=?,job_description=?,industry=?,function=?,sub_function=?,designation=?,keyskills=?,education1=?,education2=?,company_name=?,company_profile=?,work_experience_min=?,work_experience_max=?,annual_salary_min=?,annual_salary_max=?,modified='"+currentTime+"' where id='"+id+"'");
        String[] updateValues = new String[noOfColumns];
        for(int i=0;i<noOfColumns;i++)
            updateValues[i]=map.get(i+1);
        databaseOpsAbcResSkill.pstUpdate(pst, updateValues);
        try {
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeAbcResumeSkill();
    }

    public List<String> getJdData(String id)
    {
        setAbcResumeSkillConnection();
        //db_abc_temp = new DatabaseOps(abc_CONNECTION_URL_temp, abc_CONNECTION_USERNAME_temp, abc_CONNECTION_PASSWORD_temp);
        String selectQuery = "select IFNULL(job_title,'NULL'),IFNULL(designation,'NULL'),IFNULL(job_description,'NULL') from job_parsed_temps where id='"+id+"';";
        List<String> values = databaseOpsAbcResSkill.getAllStrings(selectQuery,3);
        closeAbcResumeSkill();
        return values;
    }

    public void updateSkills(String enriched_skills, String job_skills, String id)
    {
        dt = new Date();
        String currentTime = sdf.format(dt);
        setAbcResumeSkillConnection();
        PreparedStatement pst = databaseOpsAbcResSkill.preparePst("update job_parsed_temps set status=1, modified='"+currentTime+"', enriched_skills=? ,skills=? where id=?");
        String[] updateValues = {enriched_skills, job_skills, id};
        databaseOpsAbcResSkill.pstUpdate(pst, updateValues);
        try {
            pst.close();
        } catch (Exception e) {
            System.out.println("exception in closing prepare statement");
            e.printStackTrace();
        }
        closeAbcResumeSkill();
    }

    private void setAbcResumeSkillConnection() {
        databaseOpsAbcResSkill = dbConnection.getSlaveConnection(db_abc_resume_skill);
    }

    private void setAbcConnection() {
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
    }

    private void closeAbc(){
        databaseOpsAbc.close();
    }

    private void closeAbcResumeSkill(){
        databaseOpsAbcResSkill.close();
    }


}
