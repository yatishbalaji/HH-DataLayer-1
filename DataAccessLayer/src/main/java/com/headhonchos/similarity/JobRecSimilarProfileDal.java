package com.headhonchos.similarity;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richa on 30/4/14.
 */
public class JobRecSimilarProfileDal {

    List<String> list = new ArrayList<String>();
    String db = "abc_large";
    DatabaseOps databaseOpsMaster = null, databaseOpsSlave = null;
    DBConnection dbConnection = null;

    public JobRecSimilarProfileDal(){
        dbConnection = new DBConnection();
        databaseOpsMaster = dbConnection.getMasterConnection(db);
        databaseOpsSlave = dbConnection.getSlaveConnection(db);
    }

    public void execute(String id) {
        String qry = "select cl_job_posting_id_1, cl_job_posting_id_2, cl_job_posting_id_3, cl_job_posting_id_4, cl_job_posting_id_5, cl_job_posting_id_6, cl_job_posting_id_7, cl_job_posting_id_8, cl_job_posting_id_9, cl_job_posting_id_10, cl_job_posting_id_11, cl_job_posting_id_12, cl_job_posting_id_13, cl_job_posting_id_14, cl_job_posting_id_15, cl_job_posting_id_16, cl_job_posting_id_17, cl_job_posting_id_18, cl_job_posting_id_19, cl_job_posting_id_20 from mahout_similar_job_recommendations where js_login_id=" + id + " order by created desc limit 1";
        List<String> res = databaseOpsSlave.getAllStrings(qry, 20);
        int listSize = res.size();
        for (int i = 0; i < listSize; i++) {
            String jsid = res.get(i);
            if (jsid != null && !jsid.equals("null")) {
                if(!jsid.contains("."))
                    list.add(jsid);
            }
        }
    }

    public void dbStorage(String id, String[] cjid){
        String qry = "insert into job_rec_similar_profile_mail(jsid,cjid1,cjid2,cjid3,cjid4,cjid5,cjid6,cjid7,cjid8,cjid9,cjid10,cjid11,cjid12,cjid13,cjid14,cjid15,cjid16,cjid17,cjid18,cjid19,cjid20) values ("+ id + ",'"
                + cjid[0] + "','" + cjid[1] + "','" + cjid[2] + "','" + cjid[3] + "','" + cjid[4] + "','" + cjid[5] + "','"
                + cjid[6] + "','" + cjid[7] + "','" + cjid[8] + "','" + cjid[9] + "','" + cjid[10] + "','"
                + cjid[11] + "','" + cjid[12] + "','" + cjid[13] + "','" + cjid[14] + "','" + cjid[15] + "','"
                + cjid[16] + "','" + cjid[17] + "','" + cjid[18] + "','" + cjid[19] + "')";
        databaseOpsMaster.update(qry);
    }

    public List<String> getData(){
        return list;
    }

    public void clear(){
        list.clear();
    }

    public void close(){
        databaseOpsMaster.close();
        databaseOpsSlave.close();
    }

}
