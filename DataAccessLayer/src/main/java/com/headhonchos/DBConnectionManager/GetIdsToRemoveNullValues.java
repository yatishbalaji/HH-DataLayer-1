package com.headhonchos.DBConnectionManager;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by richa on 25/7/14.
 */
public class GetIdsToRemoveNullValues {

    String db_abc_large = "abc_large";
    DatabaseOps databaseOpsAbcLarge = null;
    DBConnection dbConnection = null;
    PreparedStatement prst = null;

    public GetIdsToRemoveNullValues(){
        dbConnection = new DBConnection();
        databaseOpsAbcLarge = dbConnection.getSlaveConnection(db_abc_large);
    }

    public List<String> getMahoutJobRecommendationIds(){
        String qry = "select id,concat_ws(',',IFNULL(cl_job_posting_id_1, 'NULL'),IFNULL(cl_job_posting_id_2, 'NULL'),IFNULL(cl_job_posting_id_3, 'NULL'),IFNULL(cl_job_posting_id_4, 'NULL'),IFNULL(cl_job_posting_id_5, 'NULL'),IFNULL(cl_job_posting_id_6, 'NULL'),IFNULL(cl_job_posting_id_7, 'NULL'),IFNULL(cl_job_posting_id_8, 'NULL'),IFNULL(cl_job_posting_id_9, 'NULL'),IFNULL(cl_job_posting_id_10, 'NULL'),IFNULL(cl_job_posting_id_11, 'NULL'),IFNULL(cl_job_posting_id_12, 'NULL'),IFNULL(cl_job_posting_id_13, 'NULL'),IFNULL(cl_job_posting_id_14, 'NULL'),IFNULL(cl_job_posting_id_15, 'NULL'),IFNULL(cl_job_posting_id_16, 'NULL'),IFNULL(cl_job_posting_id_17, 'NULL'),IFNULL(cl_job_posting_id_18, 'NULL'),IFNULL(cl_job_posting_id_19, 'NULL'),IFNULL(cl_job_posting_id_20, 'NULL')) as rec_id from mahout_similar_job_recommendations_temp";
        //String qry = "select id,concat_ws(',',cl_job_posting_id_1,cl_job_posting_id_2,cl_job_posting_id_3,cl_job_posting_id_4,cl_job_posting_id_5,cl_job_posting_id_6,cl_job_posting_id_7,cl_job_posting_id_8,cl_job_posting_id_9,cl_job_posting_id_10,cl_job_posting_id_11,cl_job_posting_id_12,cl_job_posting_id_13,cl_job_posting_id_14,cl_job_posting_id_15,cl_job_posting_id_16,cl_job_posting_id_17,cl_job_posting_id_18,cl_job_posting_id_19,cl_job_posting_id_20) as rec_id from mahout_similar_job_recommendations_temp";
        List<String> data = databaseOpsAbcLarge.getAllStrings(qry,2);
        return data;
    }

    public List<String> getJobRecommendationBasedOnSimilarProfilesIds() {
        String qry = "select id,concat_ws(',',IFNULL(cjid1, 'NULL'),IFNULL(cjid2, 'NULL'),IFNULL(cjid3, 'NULL'),IFNULL(cjid4, 'NULL'),IFNULL(cjid5, 'NULL'),IFNULL(cjid6, 'NULL'),IFNULL(cjid7, 'NULL'),IFNULL(cjid8, 'NULL'),IFNULL(cjid9, 'NULL'),IFNULL(cjid10, 'NULL'),IFNULL(cjid11, 'NULL'),IFNULL(cjid12, 'NULL'),IFNULL(cjid13, 'NULL'),IFNULL(cjid14, 'NULL'),IFNULL(cjid15, 'NULL'),IFNULL(cjid16, 'NULL'),IFNULL(cjid17, 'NULL'),IFNULL(cjid18, 'NULL'),IFNULL(cjid19, 'NULL'),IFNULL(cjid20, 'NULL')) as rec_id from job_rec_similar_profile_mail";
        List<String> data = databaseOpsAbcLarge.getAllStrings(qry,2);
        return data;
    }

    public String getMahoutJobRecQuery(int size, String id) {
        String qry = "";
        int left = 20 - size;
        for(int i=0; i<size; i++){
            qry = qry + "cl_job_posting_id_" + (i+1) + " = ? ," ;
        }
        size++;
        for(int i=0; i<left; i++){
            qry = qry + "cl_job_posting_id_" + size + " = null ," ;
            size++;
        }
        qry = qry.substring(0,qry.length() - 1);

        String query = "update mahout_similar_job_recommendations_temp set " + qry + " where id = " + id ;
        return query;
    }

    public String getPrepQryJobRecSimilarPorfiles(int size, String id) {
        String qry = "";
        int left = 20 - size;
        for(int i=0; i<size; i++){
            qry = qry + "cjid" + (i+1) + " = ? ," ;
        }
        size++;
        for(int i=0; i<left; i++){
            qry = qry + "cjid" + size + " = null ," ;
            size++;
        }
        qry = qry.substring(0,qry.length() - 1);

        String query = "update job_rec_similar_profile_mail set " + qry + " where id = " + id ;
        return query;
    }

   public void executePreparedStatement(String query, String[] values){
       prst = databaseOpsAbcLarge.preparePst(query);
       databaseOpsAbcLarge.pstUpdate(prst,values);
   }

    public void close(){
        databaseOpsAbcLarge.close();
    }
}
