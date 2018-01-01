package com.headhonchos.jobSeeker;

import com.headhonchos.DatabaseOps;
import java.util.List;

/**
 * Created by ishu on 17/4/14.
 */
public class ProfileDal {

    private int maxyr;
    private String profile, captiondate, profiledate;
    private static DatabaseOps db_abc = null;
    
    public ProfileDal(DatabaseOps db){
        db_abc = db;
    }

    public String getProfile(String id) {
        profile = null;
        String query1 = "select caption,"
                + "concat_ws(' , ',key_skills,group_concat(ml.name)," +
                "group_concat(ms.name),group_concat(mq.name)," +
                "group_concat(je.other_specialization)) as key_skills, "
                + "master_year_id "
                + "from abc.js_profiles  jp "
                + "left join abc.js_educations je on je.js_login_id=jp.js_login_id "
                + "left join master_specializations ms on ms.id=je.master_specialization_id "
                + "left join master_qualifications mq on mq.id=je.master_qualification_id "
                + "left join master_levels ml on ml.id=je.master_level_id "
                + "where jp.js_login_id=" + id + " group by jp.js_login_id";

        List<String> result1 = db_abc.getAllStrings(query1, 3);
        String caption = "", keySkill = "";
        if (!result1.isEmpty()) {
            for (int i = 0; i < result1.size(); i = i + 3) {
                try {
                    caption = result1.get(i);
                    keySkill = result1.get(i + 1);
                    //System.out.println("maxExp-" + maxyr);
                    maxyr = Integer.parseInt(result1.get(i + 2));
                    //System.out.println(id + " maxExp- " + maxyr);
                } catch (Exception e) {
                    System.out.println("Exception in processing id: " + id);
                }
                if (caption == null && keySkill == null) {
                } else {
                    if (caption != null && keySkill != null) {
                        profile = getCaptionDate(maxyr, db_abc) + "\n" + caption + "\n" + keySkill;
                    } else {
                        if (caption != null) {
                            profile = getCaptionDate(maxyr, db_abc) + "\n" + caption;
                        }
                        if (keySkill != null) {
                            profile = getCaptionDate(maxyr, db_abc) + "\n" + keySkill;
                        }

                    }
                }
            }
            // System.out.println("caption  "+caption);
            // System.out.println("keyskill  "+keySkill);
            String query2 = "SELECT  duration_to,duration_from,job_profile,master_functional_areas.name as functions,  master_industries.name as industries, " + "if(master_designation_id = '0' or master_designation_id = '1', " + "if(other_designation is null,' ',other_designation),master_designations.name) as designations " + "from   js_employments " + "left join master_functional_areas on master_functional_areas.id=js_employments.master_functional_area_id " + "left join master_industries on master_industries.id=js_employments.master_industry_id " + "left join master_designations on master_designations.id=js_employments.master_designation_id " + "WHERE js_employments.js_login_id = " + id;
            List<String> result2 = db_abc.getAllStrings(query2, 6);
            String duration_to = "", duration_from = "", jobprofile = "", function = "", industry = "", designation = "", res = null;
            for (int i = 0; i < result2.size(); i = i + 6) {
                try {
                    duration_to = result2.get(i);
                    duration_from = result2.get(i + 1);
                    jobprofile = result2.get(i + 2);
                    function = result2.get(i + 3);
                    industry = result2.get(i + 4);
                    designation = result2.get(i + 5);
                } catch (Exception e) {
                    System.out.println("Inside process second query exception:  " + id);
                }
                if (jobprofile == null) {
                    jobprofile = "";
                }
                if (designation != null) {
                    designation = designation.replaceAll("\\<.*?>", "");
                } else {
                    designation = "";
                }
                if (function != null) {
                    function = function.replaceAll("\\<.*?>", "");
                } else {
                    function = "";
                }
                if (industry != null) {
                    industry = industry.replaceAll("\\<.*?>", "");
                } else {
                    industry = "";
                }
                // System.out.println("to "+duration_to+"  from  "+duration_from+"  profile  "+jobprofile);
                // System.out.println("fun  "+function+"  indus  "+industry+"  desg  "+designation);
                // System.out.println(duration_from.isEmpty()+"  "+duration_to.isEmpty()+"  "+jobprofile.isEmpty());
                //System.out.println("duration_from "+);

                if (duration_from != null && !duration_from.isEmpty() && duration_to != null && !duration_to.isEmpty()) {
                    res = getProfileDate(duration_from, duration_to, db_abc);
                }
                // logger.debug("Profile--"+profile+"\nres--"+res+"\njobprofile--"+jobprofile+"\nindustry--"+industry+"\ndesignation--"+designation);
                if (res != null) {
                    profile = profile + "\n" + res + "\n" + jobprofile.replaceAll("\\<.*?>", "") + "\n" + designation;
                }
                // System.out.println("res  "+res);
            }

        }
        return profile;

    }

    /**
     * returns the maximun experience
     *
     * @return maximum no of years of experience
     */
    public int getMaxYear() {
        return maxyr;
    }

    /**
     * duration (since max experience to till now) format dd-mm-yyyy till now
     *
     * @param year maximum experience
     * @param conn DatabaseOps Object
     * @return duartion for caption and keyskills
     */
    public String getCaptionDate(int year, DatabaseOps conn) {
        // String query="SELECT curdate(),curdate() - interval " + year +
        // " year from dual";
        String query = "SELECT date_format(curdate() - interval " + "1" + " year , '%d-%m-%Y') from dual";
        List<String> result = db_abc.getAllStrings(query, 1);
        if (!result.isEmpty()) {
            String prev = "";
            for (int i = 0; i < result.size(); i = i + 1) {
                // String cur=result.get(i);
                try {
                    prev = result.get(i);
                } catch (Exception e) {
                    System.out.println("Exception in getCaptionDate having year:  " + year);
                }
                captiondate = prev + " till now";
            }
        }
        return captiondate;
    }

    /**
     * duartion of job profile in a format dd-mm-yyyy to dd-mm--yyyy or
     * dd-mm-yyyy till now
     *
     * @param prev duration_from
     * @param cur duration_to
     * @param conn DatabaseOps object
     * @return duration of fro each job profile
     */
    public String getProfileDate(String prev, String cur, DatabaseOps conn) {
        try {
            int curr = 0, org = 0;
            String prevdate = null, curdate = null, pryr = null;
            // System.out.println("prev "+prev+"  cur  "+cur);
            String query1 = "select year(\"" + prev + "\"" + "), year(\"" + cur + "\"" + "), year(curdate()),date_format(\"" + prev + "\",'%d-%m-%Y'),date_format(\"" + cur + "\",'%d-%m-%Y') from dual";
            List<String> result1 = db_abc.getAllStrings(query1, 5);
            if (!result1.isEmpty()) {
                for (int i = 0; i < result1.size(); i = i + 5) {
                    try {
                        pryr = result1.get(i);
                        String yr = result1.get(i + 1);
                        org = Integer.parseInt(yr);
                        curr = Integer.parseInt(result1.get(i + 2));
                        prevdate = result1.get(i + 3);
                        curdate = result1.get(i + 4);
                    } catch (Exception e) {
                        System.out.println("Exception in getProfileDate having curr and previous date : " + e.getMessage() + prev + "  " + cur);
                    }
                }
                if (pryr.trim().equals("1900")) {
                    return "";
                } else {
                    if (org > curr) {
                        curdate = " till now";
                        profiledate = prevdate + curdate;
                    } else {
                        profiledate = prevdate + " to " + curdate;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profiledate;
    }
}
