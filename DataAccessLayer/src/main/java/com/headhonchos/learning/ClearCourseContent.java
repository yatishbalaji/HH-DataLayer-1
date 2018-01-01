package com.headhonchos.learning;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by richa on 6/6/14.
 */
public class ClearCourseContent {

    String db_abc_education = "abc_education";
    String db_abc_large = "abc_large";
    DatabaseOps databaseOpsAbcEducation = null;
    DatabaseOps databaseOpsAbcLarge = null;
    DBConnection dbConnection = null;
    PreparedStatement pst = null;
    String trCourseContentQry = "insert into tr_course_contents(id,tr_login_id,tr_institute_id,title,course_content,is_featured_course,course_content_text) values (?,?,?,?,?,?,?)";

    public ClearCourseContent(){
        dbConnection = new DBConnection();
        databaseOpsAbcEducation = dbConnection.getSlaveConnection(db_abc_education);
        databaseOpsAbcLarge = dbConnection.getMasterConnection(db_abc_large);
        pst = databaseOpsAbcLarge.preparePst(trCourseContentQry);
    }

    public void clearCourseContent(){
        String getCoursesQry = "select id,tr_login_id,tr_institute_id,title,course_content,is_featured_course " +
                "from tr_courses where status=1 AND is_active=1 AND is_deleted=0";
        List<String> result = databaseOpsAbcEducation.getAllStrings(getCoursesQry, 6);
        int count = 0;
        if(!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                String id = result.get(i);
                i++;
                String tr_login_id = result.get(i);
                i++;
                String tr_institute_id = result.get(i);
                i++;
                String title = result.get(i);
                i++;
                String course_content = result.get(i);
                i++;
                String is_featured_course = result.get(i);
                String course_content_text = course_content.replaceAll("\\<.*?\\>", "").trim();
                String values[] = new String[]{id, tr_login_id, tr_institute_id, title, course_content, is_featured_course, course_content_text};
                databaseOpsAbcLarge.addToBatch(pst, values);
                if (count == 1000) {
                    databaseOpsAbcLarge.executeBatch(pst);
                    clear();
                    count = 0;
                }
            }
            databaseOpsAbcLarge.executeBatch(pst);
            result.clear();
        }
        clear();
        close();
    }

    private void clear(){
        try {
            pst.clearBatch();
        } catch (SQLException e) {
            System.err.println("Error in clear batch statement in ClearCourseContent..!");
            e.printStackTrace();
        }
    }

    private void close(){
        databaseOpsAbcEducation.close();
        databaseOpsAbcLarge.close();
    }

}
