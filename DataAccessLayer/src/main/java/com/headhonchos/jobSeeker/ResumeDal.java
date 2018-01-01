package com.headhonchos.jobSeeker;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;
import com.headhonchos.GlobalInstances;
import com.headhonchos.TikaParsing;

import java.io.File;
import java.io.IOException;

/**
 * Created by ishu on 17/4/14.
 */
public class ResumeDal {

    String db_abc = "abc";
    DatabaseOps databaseOpsAbc = null;
    DBConnection dbConnection = null;

    public ResumeDal(){
        dbConnection = new DBConnection();
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
    }

    public String getResumeText(String js_login_id) throws IOException {
        String file_path = getFilePathOnServer(js_login_id);
        String tika_text = "";
        boolean valid = fileExists(file_path);
        if(valid == true) {
            TikaParsing textExtractor = new TikaParsing();

            try {
                textExtractor.process(file_path);
                tika_text = textExtractor.getString();
            } catch (Exception e) {
                System.out.println("Tika failed to extract text. " + js_login_id + "---" + e.getCause());
            }
        }
        return tika_text;
    }

    private String getFilePathOnServer(String js_login_id) {
        int id = Integer.parseInt(js_login_id);
        int dirSuffix = (int) Math.floor(id / 25000);
        String primaryResumeName = databaseOpsAbc.getString("select Name from js_all_resumes where js_login_id = " + id + " and is_primary=1");
        String filePathOnServer = GlobalInstances.JOBSEEKER_RESUME_DIRECTORY + "/" + "resumes" + dirSuffix + "/" + id + "/" + (String) primaryResumeName;
        //System.out.println("resume path--- " + filePathOnServer);
        return filePathOnServer;
    }

    private boolean fileExists(String file_path){
        boolean exists = false;
        File file = new File(file_path);
        if(file.exists())
            exists = true;
        return exists;
    }

    public void close() {
        databaseOpsAbc.close();
    }
}
