package com.headhonchos.learning;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by richa on 6/6/14.
 */
public class JobSeekerDetail {

    String db_abc = "abc";
    String db_abc_education = "abc_education";
    String db_abc_large = "abc_large";
    DatabaseOps databaseOpsAbc = null;
    DatabaseOps databaseOpsAbcEducation = null;
    DatabaseOps databaseOpsAbcLargeRead = null;
    DatabaseOps databaseOpsAbcLargeWrite = null;
    DBConnection dbConnection = null;
    List<String> featuredCoursesList = new ArrayList<String>();
    List<String> nonFeaturedCoursesList = new ArrayList<String>();
    PreparedStatement preparedStatement = null;
    int leadDays = 7;

    public JobSeekerDetail(){
        dbConnection = new DBConnection();
        System.out.println("creating - edu,large, large");
        databaseOpsAbcEducation = dbConnection.getSlaveConnection(db_abc_education);
        databaseOpsAbcLargeRead = dbConnection.getSlaveConnection(db_abc_large);
        databaseOpsAbcLargeWrite = dbConnection.getMasterConnection(db_abc_large);
    }

    public List<String> getJSScreenedList(String id){
        //System.out.println(GlobalInstances.SLAVE_ABC + "\t" + GlobalInstances.SLAVE_ABC_USER);
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
        String jsDetailQry = "select JsLogin.id,JsLogin.emailid,JsLogin.first_name,JsLogin.last_name," +
                "MasterFunctionalArea.category,JsProfile.master_year_id,JsProfile.master_city_id " +
                "from js_logins as JsLogin " +

                "left join js_profiles as JsProfile " +
                "on JsProfile.js_login_id = JsLogin.id " +

                "left join js_settings as JsSetting " +
                "on JsProfile.js_login_id = JsSetting.js_login_id " +

                "left join js_employments as JsEmployment " +
                "on JsEmployment.js_login_id = JsLogin.id " +

                "left join master_functional_areas as MasterFunctionalArea " +
                "on JsEmployment.master_functional_area_id = MasterFunctionalArea.id " +

                "left join js_employments as JsEmployment2 " +
                "on (JsEmployment2.js_login_id = JsEmployment.js_login_id) " +
                "AND (JsEmployment.duration_to < JsEmployment2.duration_to) " +

                "where JsSetting.learning_recommendation=1 and JsSetting.email_alerts_status=1 and " +
                "JsLogin.status=1 and JsEmployment.js_login_id IS NOT NULL and JsEmployment2.id is null and " +
                "JsLogin.id IN (" + id + ") " +

                "group by JsLogin.id";

        //System.out.println("getscreened list qry-- " + jsDetailQry);
        List<String> jsData = databaseOpsAbc.getAllStrings(jsDetailQry, 7);
        try {
            databaseOpsAbc.close();
        } catch(Exception e){
            System.out.println("Exception in closing abc database connection-- " + e.getMessage());
            e.printStackTrace();
        }
        return jsData;
    }

    public List<String> getCourseAlertCollectionCourseIds(String js_login_id){
        String alertCourseIdQry = "select id,course_id,count from course_alert_collections where js_login_id = " + js_login_id;
        List<String> alertCourseIds = databaseOpsAbcLargeRead.getAllStrings(alertCourseIdQry, 3);
        return alertCourseIds;
    }

    public List<String> getSentCourses(String js_login_id){
        String sentCourseQry = "select js_login_id,group_concat(course_id) from course_alert_collections where js_login_id IN (" + js_login_id + ") group by js_login_id";
        List<String> sentCourses = databaseOpsAbcLargeRead.getAllStrings(sentCourseQry, 2);
        return sentCourses;
    }

    public void setStatement(){
        String upQuery = "insert into course_alert_collections(js_login_id,course_id,count,is_featured_course,leads_closing_date) values(?,?,?,?,?)";
        preparedStatement = databaseOpsAbcLargeWrite.preparePst(upQuery);
    }

    public void executeBatch(){
        databaseOpsAbcLargeWrite.executeBatch(preparedStatement);
        try {
            preparedStatement.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCourseAlertCollections(String id, String js_login_id, String course_id, int count, int featured_course, int newData, String lead_date){
        String values[] = new String[]{js_login_id, course_id, count + "", featured_course + "", lead_date};
        if(newData == 0) {
            String delQry = "delete from course_alert_collections where id = " + id ;
            databaseOpsAbcLargeWrite.update(delQry);
            //System.out.println("main del qry-- "+delQry);
        }
        databaseOpsAbcLargeWrite.addToBatch(preparedStatement,values);
    }

    public List<String> getAlreadySentCourses(String js_login_id, int is_featured_course, int limit,String mfa_category,String jsprof_master_city_id, String jsprof_master_year_id, boolean featuredCourses,boolean locationBased){
        String featured_qry = "";
        if(is_featured_course == 1)
            featured_qry = " is_featured_course = 1 ";
        else
            featured_qry = " (is_featured_course != 1 OR is_featured_course IS NULL) ";
        String query = "select course_id from course_alert_collections " +
                "where js_login_id = '" + js_login_id + "' and " + featured_qry +
                " and course_alert_collections.count < 4 and leads_closing_date >= DATE_ADD(curdate() , interval " + leadDays + " day) " +
                "order by course_alert_collections.count asc, id asc ";
            //+         "limit " + limit;
        //System.out.println("already sent courses query-- " + query);
        List<String> data = databaseOpsAbcLargeRead.getAllStrings(query, 1);

        List<String> courseIds = null;
        if(!data.isEmpty() && data.size()>0) {
            courseIds = new ArrayList<String>();
            for (String s : data) {
                if(s != null)
                    courseIds.add(s);
            }
        }
        return courseIds;
    }

    public String functionCourseCount(int viewAll){
        String courseCountQuery = "select count(*) from tr_course_functional_areas as TrCourseFunctionalArea " +
                "left join tr_courses as TrCourse " +
                "on TrCourseFunctionalArea.tr_course_id = TrCourse.id " +
                "where TrCourseFunctionalArea.master_functional_area_id = " + viewAll +
                " and TrCourse.is_deleted=0 and TrCourse.status=1 and TrCourse.is_active=1 and " +
                "(date(TrCourse.screening_date) != curdate() or TrCourse.screening_date is null)";
        String courseCount = databaseOpsAbcEducation.getString(courseCountQuery);
        //System.out.println("func course content qry--- " + courseCountQuery);
        return courseCount;
    }

    public void saveCourseAlert(List<String> retData, List<String> jsData, String tableName){
        String values = "'", insert = "";
        int c = 1;
        //if(tableName.equalsIgnoreCase("course_alerts")) {
            for (int i = 0; i < retData.size(); i++) {
                insert = insert + "course_id" + c + ",";
                values = values + retData.get(i) + "','";
                i++;
                insert = insert + "is_featured_course" + c + ",";
                values = values + retData.get(i) + "','";
                i++;
                insert = insert + "course_headline" + c + ",";
                values = values + retData.get(i).replaceAll("'", "''") + "','";
                i++;
                insert = insert + "course_title" + c + ",";
                values = values + retData.get(i).replaceAll("'", "''") + "','";
                c++;
            }
        //}

        String jsid = "";
        for(int j=0;j<jsData.size();j++){
            insert = insert + "js_login_id" + ",";
            jsid = jsData.get(j);
            values = values + jsData.get(j) + "','";
            j++;
            insert = insert + "first_name" + ",";
            values = values + jsData.get(j) + "','";
            j++;
            insert = insert + "last_name" + ",";
            values = values + jsData.get(j) + "','";
            j++;
            insert = insert + "emailid" + ",";
            values = values + jsData.get(j) + "','";
            j++;
            insert = insert + "view_all" + ",";
            values = values + jsData.get(j) + "','";
            j++;
            insert = insert + "num_count" + ",";
            values = values + jsData.get(j) + "','";
            j++;
            insert = insert + "type" + ",";
            values = values + jsData.get(j) + "','";
            j++;
            insert = insert + "hh_courses_count" + ",";
            values = values + jsData.get(j) + "','";
        }
        insert = insert.substring(0,insert.length()-1);
        values = values.substring(0,values.length()-2);
        String query = "";
        query = "insert into course_alerts(" + insert + ") values(" + values + ")";
        databaseOpsAbcLargeWrite.update(query);
        query = "insert into course_alert_temps(js_login_id) values(" + jsid + ")";
        //query = "insert into course_alert_temps(" + insert + ") values(" + values + ")";
        databaseOpsAbcLargeWrite.update(query);
        //if(tableName.equalsIgnoreCase("course_alerts"))
            //query = "insert into course_alerts(" + insert + ") values(" + values + ")";
        /*else
            query = "insert into course_alert_temps(js_login_id) values(" + jsid + ")";*/
        //System.out.println("mainqry---\n" + query);
        //databaseOpsAbcLarge.update(query);
    }

    public List<String> getAppliedCourses(String js_login_id){
        String query = "select tr_leads.tr_course_id from abc_education.tr_leads " +
                "left join abc.js_logins " +
                "on tr_leads.emailid=js_logins.emailid " +
                "where date(tr_leads.created)>=date_sub(date(now()),interval 45 day) and " +
                "js_logins.id is not null and (js_logins.id = " + js_login_id  +
                " OR tr_leads.js_login_id = "+js_login_id+" )";
        List<String> appliedCourses = databaseOpsAbcEducation.getAllStrings(query, 1);
        return appliedCourses;
    }

    public List<String> getCourseDetail(String mfa_category, String jsprof_master_year_id, List<String> courseIds, boolean sentCourse){
        String courseCondition = "",sentCourseCondition = "";
        //get data for particular courses
        if(sentCourse == true)
            sentCourseCondition = " order by FIELD(TrCourse.id," + StringUtils.join(courseIds,",") + ") ";

        if(courseIds.size() > 0)
        {
            courseCondition = " and TrCourse.id IN(" + StringUtils.join(courseIds,",") +")";
        }

        String courseQry = "select TrCourse.id,TrCourse.title,TrCourse.tr_institute_id," +
                "TrCourse.is_featured_course,TrInstitute.name,TrBatch.leads_closing_date " +
                "from tr_courses as TrCourse " +

                "inner join tr_institutes as TrInstitute " +
                "on TrCourse.tr_institute_id = TrInstitute.id " +

                "inner join tr_batches as TrBatch " +
                "on TrCourse.id = TrBatch.tr_course_id " +

                "inner join master_cities as MasterCity " +
                "on TrInstitute.master_city_id = MasterCity.id " +

                "inner join tr_course_functional_areas as TrCourseFunctionalArea " +
                "on TrCourse.id = TrCourseFunctionalArea.tr_course_id " +

                "inner join master_functional_areas as MasterFunctionalArea " +
                "on TrCourseFunctionalArea.master_functional_area_id = MasterFunctionalArea.id " +

                "where MasterFunctionalArea.category='" + mfa_category + "' and " +
                "work_experience_min<=" + jsprof_master_year_id + " and " +
                "TrCourse.status='1' and TrCourse.is_active='1' and TrCourse.is_deleted='0' and " +
                "(date(TrCourse.screening_date) != curdate() or TrCourse.screening_date is null) and " +
                "TrBatch.status = '1' and " + "TrBatch.is_active = '1' and " +
                "TrBatch.leads_closing_date >= DATE_ADD(curdate() , interval " + leadDays + " day) " +
                courseCondition +
                " group by TrCourse.id " + sentCourseCondition;
                // +
        //"order by is_featured_course desc,registration_close_date ";
        //System.out.println("courseQry========\n" + courseQry);
        List<String> courseData = databaseOpsAbcEducation.getAllStrings(courseQry, 6);
//        setCourseCount(courseData,featuredCourses);
        return courseData;
    }

    public List<String> getAllCourseIds(String mfa_category, String jsprof_master_city_id, String jsprof_master_year_id, boolean featuredCourses, boolean locationBased){
        String featuredCoursesCondition = "", locationCondition = "", notCondition = "";
        //get featured courses maximum 4
        if(featuredCourses==true){
            featuredCoursesCondition = " and TrCourse.is_featured_course = 1 ";
        }
        else{
            featuredCoursesCondition = " and (TrCourse.is_featured_course != 1 OR TrCourse.is_featured_course IS NULL) ";
        }

        //first case  On FunctionalArea, Experience and Location Basis
        if(locationBased == true){
            locationCondition = " and TrInstitute.master_city_id='" + jsprof_master_city_id + "' ";
        }
        //second case  On FunctionalArea and Experience Basis
        // so not for course id which we get from first case
        //else {
        if(featuredCourses == true){
            for(int i=0;i<featuredCoursesList.size();i++){
                notCondition = notCondition + ",'" + featuredCoursesList.get(i) + "'";
            }
        }
        else{
            for(int i=0;i<nonFeaturedCoursesList.size();i++){
                notCondition = notCondition + ",'" + nonFeaturedCoursesList.get(i) + "'";
            }
        }
        //remove first comma
        if(notCondition.length() > 1)
            notCondition = " and TrCourse.id NOT IN(" + notCondition.substring(1) + ") ";
        //}

        String courseQry = "select TrCourse.id " +
                "from tr_courses as TrCourse " +

                "inner join tr_institutes as TrInstitute " +
                "on TrCourse.tr_institute_id = TrInstitute.id " +

                "inner join tr_batches as TrBatch " +
                "on TrCourse.id = TrBatch.tr_course_id " +

                "inner join master_cities as MasterCity " +
                "on TrInstitute.master_city_id = MasterCity.id " +

                "inner join tr_course_functional_areas as TrCourseFunctionalArea " +
                "on TrCourse.id = TrCourseFunctionalArea.tr_course_id " +

                "inner join master_functional_areas as MasterFunctionalArea " +
                "on TrCourseFunctionalArea.master_functional_area_id = MasterFunctionalArea.id " +

                "where MasterFunctionalArea.category='" + mfa_category + "'" + locationCondition +
                "and work_experience_min<=" + jsprof_master_year_id + " and " +
                "TrCourse.status='1' and TrCourse.is_active='1' and TrCourse.is_deleted='0' and " +
                "(date(TrCourse.screening_date) != curdate() or TrCourse.screening_date is null) and " +
                "TrBatch.status = '1' and " + "TrBatch.is_active = '1' and " +
                "TrBatch.leads_closing_date >= DATE_ADD(curdate() , interval " + leadDays + " day) " +
                featuredCoursesCondition + notCondition +
                " group by TrCourse.id " +
                "order by rand(NOW()) ";// + limit;
                //"order by registration_close_date ";// + limit;
        //System.out.println("AllcourseQry========\n" + courseQry);
        List<String> courseData = databaseOpsAbcEducation.getAllStrings(courseQry, 1);
        return courseData;
    }

    public void setCourseCount(String courseId, String featuredCourses){
        if(featuredCourses.equals("1")){
            featuredCoursesList.add(courseId);
        }
        else{
            nonFeaturedCoursesList.add(courseId);
        }
    }

    public String[] moreGenericCourses(String[] tr_course_ids, String mfa_category, int trCourseIdsCount){
        //setting order condition of based on type of function
        String newTrCourseIds[] = null;
        if(trCourseIdsCount>0){
            String orderCondition = "", trCourseIds = "";
            for(int i=0;i<trCourseIdsCount;i++){
                trCourseIds = trCourseIds + "," +tr_course_ids[i];
            }
            trCourseIds = trCourseIds.substring(1);
            if(mfa_category.equals("All Senior Management Grouping")){
                orderCondition = "counts desc";
            } else {
                orderCondition = "counts asc";
            }

            //finding course ids which are more generlized in case of function category - 'All Senior Management Grouping' and more specific in other function area.
            String genericCourseId = "select TrCourseFunctionalArea.tr_course_id, count(TrCourseFunctionalArea.tr_course_id) as counts " +
                    "from tr_course_functional_areas as TrCourseFunctionalArea " +
                    "where TrCourseFunctionalArea.tr_course_id IN (" + trCourseIds + ") " +
                    "group by TrCourseFunctionalArea.tr_course_id " +
                    "order by " + orderCondition;
            //System.out.println("generic course query-- " + orderCondition +" --\n"+genericCourseId);
            List<String> result = databaseOpsAbcEducation.getAllStrings(genericCourseId, 2);
            int resSize = result.size();
            newTrCourseIds = new String[resSize/2];
            int c = 0;
            for(int j=0;j<result.size();j= j + 2){
                newTrCourseIds[c] = result.get(j);
                c++;
            }
            result.clear();
            return newTrCourseIds;
        }
        else
            return newTrCourseIds;
    }

    public List<String> allMoreGenericCourses(List<String> tr_course_ids, String mfa_category){
        //setting order condition of based on type of function
        List<String> newTrCourseIds = null;
        if(tr_course_ids.size() > 0){
            String orderCondition = "", trCourseIds = "";
            for(String tcid : tr_course_ids){
                trCourseIds = trCourseIds + "," + tcid;
            }
            trCourseIds = trCourseIds.substring(1);
            if(mfa_category.equals("All Senior Management Grouping")){
                orderCondition = "counts desc";
            } else {
                orderCondition = "counts asc";
            }

            //finding course ids which are more generlized in case of function category - 'All Senior Management Grouping' and more specific in other function area.
            String genericCourseId = "select TrCourseFunctionalArea.tr_course_id, count(TrCourseFunctionalArea.tr_course_id) as counts " +
                    "from tr_course_functional_areas as TrCourseFunctionalArea " +
                    "where TrCourseFunctionalArea.tr_course_id IN (" + trCourseIds + ") " +
                    "group by TrCourseFunctionalArea.tr_course_id " +
                    "order by " + orderCondition;
            //System.out.println("generic course query-- " + orderCondition +" --\n"+genericCourseId);
            List<String> result = databaseOpsAbcEducation.getAllStrings(genericCourseId, 2);
            newTrCourseIds = new ArrayList<String>();
            for(int j=0;j<result.size();j= j + 2){
                newTrCourseIds.add(result.get(j));
            }
            result.clear();
            return newTrCourseIds;
        }
        else
            return newTrCourseIds;
    }

    public void clear(){
        featuredCoursesList.clear();
        nonFeaturedCoursesList.clear();
    }

    public void close(){
        System.out.println("closing - edu,large, large");
        databaseOpsAbcLargeRead.close();
        databaseOpsAbcLargeWrite.close();
        databaseOpsAbcEducation.close();
    }
}