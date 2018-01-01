package com.headhonchos.jobSeeker;

import com.headhonchos.DBConnectionManager.ConnectionManager;
import com.headhonchos.GlobalInstances;
import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.paresdResume.Designation;
import com.headhonchos.skill.ResumeSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by aman on 23/5/14.
 */
public class JSProfileData2 {

    public static final Logger logger = LoggerFactory.getLogger(JSProfileData2.class);
    private static String APPS = System.getProperty("APPS");
    static {
        logger.info("Load application variables from configs/datalayer_solr.txt file");
        Properties properties = new Properties();
        try {
            String solrPath = APPS + "configs/datalayer_solr.txt";
            properties.load(new FileReader(solrPath));
            GlobalInstances.SOLR_URL = properties.getProperty("SOLR_URL");
        } catch (IOException e) {
            logger.debug("static block error,"+ e);
        }
    }

    private int js_login_id;
    private List<ResumeSkill> resumeSkillList;
    private Function currentFunction;
    private Function previousFunction;
    private List<Function> preferredFunction;
    private Industry currentIndustry;
    private Industry previousIndustry;
    private List<Industry> preferredIndustry;
    private Designation currentDesignation;
    private List<Location> location;
    private int experience;
    private int salary;
    private boolean foreigner;
    private boolean anyLocation;
    private List<String> qualification;

    public JSProfileData2(int js_login_id) {
        logger.info("Inside js Profile data");
        this.js_login_id = js_login_id;
        Connection abcConnection = ConnectionManager.getConnection("abc");
        Connection abcResumeSkillConnection = ConnectionManager.getConnection("abc_resume_skill");
        try {
            String queryAbc = "select jsp.js_login_id," +
                    "jsp.master_lakh_id as salary, " +
                    "jsp.master_year_id as exp, " +
                    "jsp.master_functional_area_id as current_function_id, " +
                    "jsp.master_industry_id as current_industry_id, " +
                    "mi.name as industry, " +
                    "je.master_designation_id, " +
                    "md.name as designation, " +
                    "je.other_designation, " +
                    "mfa.name as 'function', " +
                    "group_concat(distinct pref_ind.master_industry_id) as preferred_industry, " +
                    "group_concat( distinct pref_func.master_functional_area_id) as 'preferred_function', " +
                    "group_concat(distinct pref_loc.master_city_id) as preferred_city " +
                    "from js_logins jsl " +
                    "left join js_profiles jsp on jsl.id=jsp.js_login_id " +
                    "left join js_preferences_industries as pref_ind on jsl.id=pref_ind.js_login_id " +
                    "left join js_preferences_functions as pref_func on jsl.id=pref_func.js_login_id " +
                    "left join js_preferences_locations as pref_loc on jsl.id=pref_loc.js_login_id " +
                    "left join js_employments as je on je.js_login_id = pref_loc.js_login_id " +
                    "left JOIN js_employments je2 ON (je2.js_login_id = je.js_login_id AND je.duration_to < je2.duration_to) " +
                    "left join master_functional_areas mfa on je.master_functional_area_id=mfa.id " +
                    "left join master_industries mi on je.master_industry_id=mi.id " +
                    "left join master_designations md on je.master_designation_id=mi.id " +
                    "where  jsl.id = " + this.js_login_id + " " +
                    " group by jsl.id;";

            PreparedStatement selectAbcQuery = abcConnection.prepareStatement(queryAbc);
            logger.debug(queryAbc);

            String queryAbcResumeSkill = "select enriched_skills from abc_resume_skill.process_resumes where js_login_id = " + this.js_login_id;
            PreparedStatement selectAbcResumeQuery = abcResumeSkillConnection.prepareStatement(queryAbcResumeSkill);
            logger.debug(queryAbcResumeSkill);

            ResultSet resultAbc = selectAbcQuery.executeQuery();
            ResultSet resultAbcResumeSkill = selectAbcResumeQuery.executeQuery();

            logger.info("setting enriched skills");
            //Set resume skill value
            resumeSkillList = new LinkedList<ResumeSkill>();

            while (resultAbcResumeSkill.next()) {
                String temp_skills = resultAbcResumeSkill.getString("enriched_skills");
                if(temp_skills != null && !temp_skills.isEmpty()) {
                    String[] skillsUnformatted = temp_skills.split(",");
                    for (String s : skillsUnformatted) {
                        //skills unformatted have skills in form of "skill_name|E1|L1 where E and L are experience and last used respectively"
                        String[] splittedValuesOfSkill = s.split("\\|");

                        String skillName = splittedValuesOfSkill[0];

                        String experienceNdLastUsed = splittedValuesOfSkill[1];
                        experienceNdLastUsed = experienceNdLastUsed.replace("L", ",L");
                        String[] split_loc_exp = experienceNdLastUsed.split(",");


                        String experience = split_loc_exp[0];
                        experience = experience.replace('E', ' ');
                        experience = experience.trim();
                        int experienceVal = Integer.parseInt(experience);

                        String lastUsed = split_loc_exp[1];
                        lastUsed = lastUsed.replace('L', ' ');
                        lastUsed = lastUsed.trim();
                        int lastUsedVal = Integer.parseInt(lastUsed);
                        resumeSkillList.add(new ResumeSkill(skillName, experienceVal, lastUsedVal));
                    }
                }

            }
            logger.info("set skills");
            //get info about an user like current function, previous function, preferred function ,designation ...etc.
            preferredFunction = new LinkedList<Function>();
            List<String> listLocation = new LinkedList<String>();
            while (resultAbc.next()) {

                //Functions
                String master_functional_area_id = resultAbc.getString("current_function_id");
                if (currentFunction != null) {
                    currentFunction = new Function(master_functional_area_id);
                }
                //String function = resultAbc.getString("function");
                String pref_function = resultAbc.getString("preferred_function");
                if (pref_function != null) {
                    String[] pref_func_array = pref_function.split(",");
                    for (String id : pref_func_array) {
                        Function f = new Function(id);
                        preferredFunction.add(f);
                    }
                }

                //Industry
                String current_industry_id = resultAbc.getString("current_industry_id");
                currentIndustry = new Industry(current_industry_id);
                String pref_industry = resultAbc.getString("preferred_industry");
                preferredIndustry = new LinkedList<Industry>();
                if (pref_industry != null) {
                    String[] pref_ind_array = pref_industry.split(",");
                    for (String id : pref_ind_array) {
                        preferredIndustry.add(new Industry(id));
                    }
                }

                String exp = resultAbc.getString("exp");
                if (exp != null) {
                    experience = Integer.parseInt(exp);
                }
                String sal = resultAbc.getString("salary");
                if (sal != null) {
                    salary = Integer.parseInt(sal);
                }

                String designation_id = resultAbc.getString("master_designation_id");
                if(designation_id!=null) {
                    currentDesignation = new Designation(designation_id);
                }

                String pref_city = resultAbc.getString("preferred_city");
                if (pref_city != null) {
                    String[] city_id_array = pref_city.split(",");
                    for (String id : city_id_array) {
                        if (id.trim().equals("-1")) {
                            anyLocation = true;
                            break;
                        }
                        listLocation.add(id);
                    }
                }
            }

            location = new LinkedList<Location>();
            for (String id : listLocation) {
                ResultSet latAndLng = selectAbcQuery.executeQuery("select lat,lng from master_cities where id=" + id);
                if (latAndLng.next()) {
                    double lat = Double.parseDouble(latAndLng.getString("lat"));
                    double lng = Double.parseDouble(latAndLng.getString("lng"));
                    if(lat!= 0 & lng !=0) {
                        Location loc = new Location(id);
                        loc.setLatitude(lat);
                        loc.setLongitude(lng);
                        location.add(loc);
                    }
                }
            }

            ResultSet prev_ind_nd_func = selectAbcQuery.executeQuery("select js_login_id,duration_from,duration_to,master_industry_id, master_functional_area_id " +
                    "from js_employments where " +
                    " js_login_id=" + this.js_login_id + " " +
                    " order by duration_to desc limit 1 OFFSET 1;");
            if (prev_ind_nd_func.next()) {
                String master_functional_area_id = prev_ind_nd_func.getString("master_functional_area_id");
                logger.debug("Previous function fetched - "+master_functional_area_id);
                if(master_functional_area_id != null){
                    previousFunction = new Function(master_functional_area_id);
                }

                String master_industry_id = prev_ind_nd_func.getString("master_industry_id");
                logger.debug("Proevioud industry - "+master_industry_id);
                if(master_industry_id!=null) {
                    previousIndustry = new Industry(master_industry_id);
                }
            }

            ResultSet resultQualifications = selectAbcQuery.executeQuery("select  " +
                    " group_concat(distinct master_qualifications.name) as qualifications " +
                    " from " +
                    "    js_educations " +
                    "        Inner join " +
                    "    master_qualifications " +
                    " where " +
                    "    js_educations.master_qualification_id = master_qualifications.id AND js_educations.js_login_id = " + this.js_login_id);

            if (resultQualifications.next()) {
                String tempQualifications = resultQualifications.getString("qualifications");
                if (tempQualifications != null) {
                    qualification = new LinkedList<String>();
                    String[] qual_array = tempQualifications.split(",");
                    for (String name : qual_array) {
                        qualification.add(name);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("SqlException - "+e);
        }

    }

    public List<ResumeSkill> getResumeSkill() {
        return this.resumeSkillList;
    }

    public Function getCurrentFunction() {
        return this.currentFunction;
    }

    public Function getPreviousFunction() {
        return this.previousFunction;
    }

    public List<Function> getPreferredFunction() {
        return this.preferredFunction;
    }

    public Industry getCurrentIndustry() {
        return this.currentIndustry;
    }

    public Industry getPreviousIndustry() {
        return this.previousIndustry;
    }

    public List<Industry> getPreferredIndustry() {
        return this.preferredIndustry;
    }

    public Designation getCurrentDesignation() {
        return this.currentDesignation;
    }

    public List<Location> getLocation() {
        return this.location;
    }

    public int getExperience() {
        return experience;
    }

    public int getSalary() {
        return salary;
    }

    public boolean isForeigner() {
        return this.foreigner;
    }

    public boolean isAnyLocation() {
        return this.anyLocation;
    }

    public List<String> getQualification() {
        return this.qualification;
    }
}
