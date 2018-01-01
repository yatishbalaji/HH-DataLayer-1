package com.headhonchos.similarity;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;

import java.util.List;

/**
 * Created by richa on 25/7/14.
 */
public class CourseData {

    String db_abc_education = "abc_education";
    String db_abc_large = "abc_large";
    String db_abc = "abc";
    DatabaseOps databaseOpsAbcEducation = null, databaseOpsAbcEducationWrite = null, databaseOpsAbcLargeWrite = null;
    DatabaseOps databaseOpsAbc =null;
    DBConnection dbConnection = null;

    public CourseData(){
        dbConnection = new DBConnection();
        databaseOpsAbcEducation = dbConnection.getSlaveConnection(db_abc_education);
        databaseOpsAbcEducationWrite = dbConnection.getMasterConnection(db_abc_education);
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
    }

    public List<String> getAllCourseIds() {
        String query = "SELECT id FROM tr_courses where skills_query IS null OR skills IS null";
        List<String> data = databaseOpsAbcEducation.getAllStrings(query, 1);
        return data;
    }

    public List<String> getActiveCourseIds() {
        String query = "SELECT tr_courses.id FROM tr_courses " +
                "left JOIN tr_batches " +
                "ON tr_batches.tr_course_id=tr_courses.id " +
                "where tr_courses.status='1' and tr_courses.is_active=1 and tr_courses.is_deleted=0 " +
                "and tr_batches.status='1' and tr_batches.is_active=1 and tr_batches.is_deleted=0 group by tr_courses.id";
        List<String> data = databaseOpsAbcEducation.getAllStrings(query, 1);
        return data;
    }

    public List<String> getCourseContent(String course_id) {
        String query = "select course_content,desired_candidate_profile from " +
                "tr_courses where id = " + course_id;
        List<String> data = databaseOpsAbcEducation.getAllStrings(query,2);
        return data;
    }

    public void updateSkills(String course_id, String skills_query, String skill){
        String qry = "update tr_courses set skills_query = '" + skills_query + "' , skills = '" + skill +"' where id = " + course_id ;
        databaseOpsAbcEducationWrite.update(qry);
    }

    public List<String> getActiveCourseAndBatchIds() {
        String query = "SELECT tr_batches.id,tr_courses.id FROM tr_courses " +
                "left JOIN tr_batches " +
                "ON tr_batches.tr_course_id=tr_courses.id " +
                "where tr_courses.status='1' and tr_courses.is_active=1 and tr_courses.is_deleted=0 " +
                "and tr_batches.status='1' and tr_batches.is_active=1 and tr_batches.is_deleted=0 and tr_courses.id in (select tr_course_id from tr_course_groups) group by tr_courses.id";
        List<String> data = databaseOpsAbcEducation.getAllStrings(query, 2);
        return data;
    }

    public void insertRec(String[] values) {
        databaseOpsAbcLargeWrite = dbConnection.getMasterConnection(db_abc_large);
        String query = "insert into abc_large.similar_courses_output_temp(course_id,batch_id,course_title,start_date,duration,duration_unit,institute_name,city,rec_course_1,start_date_1,duration_1,duration_unit_1,institute_name_1,city_1,rec_course_2,start_date_2,duration_2,duration_unit_2,institute_name_2,city_2,rec_course_3,start_date_3,duration_3,duration_unit_3,institute_name_3,city_3,rec_course_4,start_date_4,duration_4,duration_unit_4,institute_name_4,city_4,rec_course_5,start_date_5,duration_5,duration_unit_5,institute_name_5,city_5) values(" +
                values[0] + "," + values[1] + ",'" + values[2] + "','" + values[3] + "'," + values[4] + ",'" + values[5] + "','" + values[6] + "','" + values[7] + "'," +
                "'" + values[8] + "','" + values[9] + "'," + values[10] + ",'" + values[11] + "','" + values[12] + "','" + values[13] + "'," +
                "'" + values[14] + "','" + values[15] + "'," + values[16] + ",'" + values[17] + "','" + values[18] + "','" + values[19] + "'," +
                "'" + values[20] + "','" + values[21] + "'," + values[22] + ",'" + values[23] + "','" + values[24] + "','" + values[25] + "'," +
                "'" + values[26] + "','" + values[27] + "'," + values[28] + ",'" + values[29] + "','" + values[30] + "','" + values[31] + "'," +
                "'" + values[32] + "','" + values[33] + "'," + values[34] + ",'" + values[35] + "','" + values[36] + "','" + values[37] + "'" +
                 ")";
        databaseOpsAbcLargeWrite.update(query);
        databaseOpsAbcLargeWrite.close();
    }

    public List<String> getAppliedCourses(String emailId) {
        String query = "select tr_leads.tr_course_id from tr_leads " +
                "where tr_leads.emailid ='" + emailId + "' AND date(tr_leads.created)>=date_sub(date(now()),interval 45 day)";
        List<String> appliedCourses = databaseOpsAbcEducation.getAllStrings(query, 1);
        return appliedCourses;
    }


    public int getExp(String emailid){
        String s = "select jsp.master_year_id as exp " +
                "    from js_logins jsl " +
                "    left join js_profiles jsp on jsl.id=jsp.js_login_id " +
                "    where jsl.emailid='" + emailid+"'";
        String string = databaseOpsAbc.getString(s);
        int i=0;
        if(string != null && !string.isEmpty()){
            try {
                i = Integer.parseInt(string);
            }
            catch (NumberFormatException nfe){
                System.err.println("can't convert min exp " + string + " to integer value");
            }
        }
        return i;
    }

    public void close(){
        databaseOpsAbc.close();
        databaseOpsAbcEducation.close();
        databaseOpsAbcEducationWrite.close();
    }
}
