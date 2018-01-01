package com.headhonchos.learning;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.List;

/**
 * Created by richa on 6/6/14.
 */
public class LearningCandidates {

    String db_abc_large_temp = "abc_large_temp";
    DatabaseOps databaseOpsAbcLargeTemp = null, databaseOpsAbcLargeTempWrite = null;
    DBConnection dbConnection = null;

    public LearningCandidates(){
        dbConnection = new DBConnection();
        databaseOpsAbcLargeTemp = dbConnection.getSlaveConnection(db_abc_large_temp);
        databaseOpsAbcLargeTempWrite = dbConnection.getMasterConnection(db_abc_large_temp);
    }

    public String getCount(){
        String getCountQry = "select count(*) " +
                "from all_learning_candidates as AllLearningCandidate " +
                "left join abc_large.course_alert_temps cat " +
                "on cat.js_login_id = AllLearningCandidate.id " +
                "where  cat.js_login_id is null AND AllLearningCandidate.alternate_weekly=0";
        String count = databaseOpsAbcLargeTemp.getString(getCountQry);
        setAlternateWeekly(0,"");
        close();
        return count;
    }

    public void setAlternateWeekly(int flag, String learningId){
        String condition = "";
        if(!learningId.equals(""))
            condition = " where id = " + learningId;
        else
            condition = " where alternate_weekly = 1";
        String setQuery = "update all_learning_candidates set alternate_weekly = " + flag + condition;
        databaseOpsAbcLargeTempWrite.update(setQuery);
    }

    public List<String> getIds(String lowerLimit, String upperLimit){
        String getIdQry = "select AllLearningCandidate.id " +
                "from all_learning_candidates as AllLearningCandidate " +
                "left join abc_large.course_alert_temps cat " +
                "on cat.js_login_id=AllLearningCandidate.id " +
                "where cat.js_login_id is null limit " + lowerLimit + "," + upperLimit ;
        List<String> ids = databaseOpsAbcLargeTemp.getAllStrings(getIdQry, 1);
        close();
        return ids;
    }

    public void close(){
        databaseOpsAbcLargeTemp.close();
        databaseOpsAbcLargeTempWrite.close();
    }

}
