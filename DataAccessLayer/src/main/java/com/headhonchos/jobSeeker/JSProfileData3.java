package com.headhonchos.jobSeeker;

import com.headhonchos.DBConnectionManager.PersistentConnectionManager;
import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.jobPosting.Qualification;
import com.headhonchos.paresdResume.Designation;
import com.headhonchos.skill.ResumeSkill;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class JSProfileData3 {
    private static Logger logger = LoggerFactory.getLogger((Class)JSProfileData3.class);
    private int js_login_id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Qualification> qualification;
    private Designation currentDesignation;
    private List<Location> location;
    private int experience;
    private int salary;
    private boolean foreigner;
    private boolean anyLocation;
    private List<ResumeSkill> resumeSkillList;
    private List<String> keywords;
    private Function currentFunction;
    private Function previousFunction;
    private List<Function> preferredFunction;
    private Industry currentIndustry;
    private Industry previousIndustry;
    private List<Industry> preferredIndustry;
    private Set<String> appliedJobs;
    private List<String> sentJobs;
    private String profileUpdateDate;

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setCurrentFunction(Function currentFunction) {
        this.currentFunction = currentFunction;
    }

    public void setCurrentIndustry(Industry currentIndustry) {
        this.currentIndustry = currentIndustry;
    }

    public void setLocation(List<String> location_ids) throws SQLException {
        this.setLocations(StringUtils.join((Object[])location_ids.toArray(), (String)","));
    }

    public int getJs_login_id() {
        return this.js_login_id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
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
        return this.experience;
    }

    public int getSalary() {
        return this.salary;
    }

    public boolean isForeigner() {
        return this.foreigner;
    }

    public boolean isAnyLocation() {
        return this.anyLocation;
    }

    public List<Qualification> getQualification() {
        return this.qualification;
    }

    public List<String> getSentJobs() {
        return this.sentJobs;
    }

    public Set<String> getAppliedJobs() {
        return this.appliedJobs;
    }

    public String getProfileUpdateDate() {
        return this.profileUpdateDate;
    }

    public void setProfileUpdateDate(String profileUpdateDate) {
        this.profileUpdateDate = profileUpdateDate;
    }

    public JSProfileData3(int js_login_id) {
        logger.trace("Inside JSProfileData");
        String currentAndPrefDataQuery = "select  JP.js_login_id as js_login_id, jsl.first_name as first_name, jsl.last_name as last_name, jsl.emailid as email, JP.master_year_id as experience, JP.last_profile_update as last_update, JP.key_skills as key_skills, JEmp.master_functional_area_id, JEmp.other_functional_area, JEmp.master_industry_id, JEmp.other_industry, JEmp.master_designation_id, JEmp.other_designation, JP.master_lakh_id as salary, group_concat(distinct(JEdu.master_qualification_id)) as education, JP.master_country_id as location_country, group_concat(distinct(JPI.master_industry_id)) as preferred_industry, group_concat(distinct(JPF.master_functional_area_id)) as preferred_function, group_concat(distinct(JPL.master_city_id)) as preferred_location from js_profiles as JP left join js_logins as jsl ON JP.js_login_id = jsl.id left join js_employments as JEmp ON JP.js_login_id = JEmp.js_login_id and JEmp.currently_working = 1 left join js_educations as JEdu ON JP.js_login_id = JEdu.js_login_id left join js_preferences_functions as JPF ON JP.js_login_id = JPF.js_login_id left join js_preferences_industries as JPI ON JP.js_login_id = JPI.js_login_id left join js_preferences_locations as JPL ON JP.js_login_id = JPL.js_login_id where  JP.js_login_id = " + js_login_id + " group by JP.js_login_id";
        String previousDataQuery = "select master_industry_id as previous_industry, master_functional_area_id as previous_function from js_employments where js_login_id = " + js_login_id + " and currently_working <> 1" + " order by duration_to desc" + " limit 1;";
        String skillsQuery = "select enriched_skills from abc_resume_skill.process_resumes where js_login_id =" + js_login_id;
        String appliedJobQuery = "SELECT group_concat(cl_job_posting_id) as applied_jobs FROM abc.job_applications where js_login_id= " + js_login_id + "    group by js_login_id";
        String previousRcmdJobsQuery = "SELECT job_id1, job_id2, job_id3, job_id4, job_id5, job_id6, job_id7, job_id8, job_id9, job_id10  FROM abc_large.job_send_alerts where js_login_id=" + js_login_id;
        Statement abcStatement = null;
        ResultSet currentDataResultSet = null;
        ResultSet previousDataRS = null;
        Statement abcLargeStatement = null;
        ResultSet previousSentJRRS = null;
        try {
            Connection abcConnection = PersistentConnectionManager.getAbcReadConnection();
            abcStatement = abcConnection.createStatement();
            logger.trace("Executing currentAndPrefDataQuery - " + currentAndPrefDataQuery);
            currentDataResultSet = abcStatement.executeQuery(currentAndPrefDataQuery);
            this.extractCurrentData(currentDataResultSet);
            logger.trace("Executing previous employment DataQuery - " + previousDataQuery);
            previousDataRS = abcStatement.executeQuery(previousDataQuery);
            this.setPreviousData(previousDataRS);
            logger.trace("Executing appliedJobsQuery - " + appliedJobQuery);
            ResultSet appliedJobsRS = abcStatement.executeQuery(appliedJobQuery);
            this.extractAppliedJobs(appliedJobsRS);
            Connection abcLargeConnection = PersistentConnectionManager.getAbcLargeConnection();
            abcLargeStatement = abcLargeConnection.createStatement();
            logger.trace("Executingting previous Rcmd Query - " + previousRcmdJobsQuery);
            previousSentJRRS = abcLargeStatement.executeQuery(previousRcmdJobsQuery);
            this.extractPreviousSentJobs(previousSentJRRS);
        }
        catch (SQLException sqe) {
            logger.error("Sql error abc @ currentAndPrevDataQuery.", (Throwable)sqe);
        }
        finally {
            try {
                logger.debug("Closing abc DS resources.");
                if (null != currentDataResultSet && !currentDataResultSet.isClosed()) {
                    currentDataResultSet.close();
                }
                logger.debug("curr data rs closed..");
                if (null != previousDataRS && !previousDataRS.isClosed()) {
                    previousDataRS.close();
                }
                logger.debug("prev data closed..");
                if (null != abcStatement && !abcStatement.isClosed()) {
                    abcStatement.close();
                }
                logger.debug("abc statement closed..");
                if (null != previousSentJRRS && !previousSentJRRS.isClosed()) {
                    previousSentJRRS.close();
                }
                logger.debug("previous sent closed..");
                if (null != abcLargeStatement && !abcLargeStatement.isClosed()) {
                    abcLargeStatement.close();
                }
                logger.debug("abc large statement closed..");
            }
            catch (Exception e) {
                logger.error("Database resources closing error", (Throwable)e);
            }
        }
        Statement abcResumeSkillStatement = null;
        ResultSet skillsDataResultSet = null;
        Connection abcResumeSkillConnection = null;
        try {
            abcResumeSkillConnection = PersistentConnectionManager.getAbcResumeSkillConnection();
            abcResumeSkillStatement = abcResumeSkillConnection.createStatement();
            logger.trace("Executing skill Query - " + skillsQuery);
            skillsDataResultSet = abcResumeSkillStatement.executeQuery(skillsQuery);
            this.setSkillData(skillsDataResultSet);
        }
        catch (SQLException sqe) {
            sqe.printStackTrace();
            logger.error("Sql error abc_resume_skill", (Throwable)sqe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            logger.debug("Closing abc_resume_skill DS resource.");
            try {
                if (null != skillsDataResultSet && !skillsDataResultSet.isClosed()) {
                    skillsDataResultSet.close();
                }
                if (null != abcResumeSkillStatement && !abcResumeSkillStatement.isClosed()) {
                    abcResumeSkillStatement.close();
                }
            }
            catch (Exception e) {
                logger.error("Database resources closing error", (Throwable)e);
            }
        }
    }

    public JSProfileData3() {
    }

    private void extractPreviousSentJobs(ResultSet previousSentJRRS) {
        if(previousSentJRRS != null) {
            this.sentJobs = new ArrayList<>();
            try {
                while (previousSentJRRS.next()) {
                    for (int i = 1; i <= 10; ++i) {
                        if(previousSentJRRS.getString(i) != null && !previousSentJRRS.getString(i).trim().equals(""))
                            this.sentJobs.add(previousSentJRRS.getString(i));
                    }
                }
            }
            catch (SQLException e) {
                logger.error("can't find previous sent jobs.");
            }
            this.sentJobs.removeAll(Collections.singleton(null));
        }

    }

    private void extractAppliedJobs(ResultSet appliedJobsData) {
        if (appliedJobsData != null) {
            this.appliedJobs = new HashSet<>();
            try {
                while (appliedJobsData.next()) {
                    String string = appliedJobsData.getString(1);
                    if(string != null && !string.trim().equals(""))
                        this.appliedJobs.addAll(Arrays.asList(string.split("\\s*,\\s*")));
                }
            }
            catch (SQLException e) {
                logger.error("error in extracting applied job data", (Throwable)e);
            }
            this.appliedJobs.removeAll(Collections.singleton(null));
        }
    }

    private void setSkillData(ResultSet skillsDataResultSet) throws SQLException {
        logger.trace("setting skill data");
        this.resumeSkillList = new LinkedList<ResumeSkill>();
        while (skillsDataResultSet.next()) {
            String[] enrichedSkillsArray;
            String enriched_skills = skillsDataResultSet.getString("enriched_skills");
            if (enriched_skills == null || enriched_skills.isEmpty()) continue;
            for (String s : enrichedSkillsArray = enriched_skills.split(",")) {
                String[] skillNameAndValues = s.split("\\|");
                String skillName = skillNameAndValues[0];
                String experienceAndLastUsed = skillNameAndValues[1];
                experienceAndLastUsed = experienceAndLastUsed.replace("L", ",L");
                String[] split_loc_exp = experienceAndLastUsed.split(",");
                String experience = split_loc_exp[0];
                int experienceVal = Integer.parseInt(experience.replace('E', ' ').trim());
                String lastUsed = split_loc_exp[1];
                int lastUsedVal = Integer.parseInt(lastUsed.replace('L', ' ').trim());
                this.resumeSkillList.add(new ResumeSkill(skillName, experienceVal, lastUsedVal));
            }
        }
        logger.trace("Skills data - " + this.resumeSkillList);
    }

    private void setPreviousData(ResultSet previousDataRS) throws SQLException {
        logger.trace("setting pRevious Function and Industry");
        while (previousDataRS.next()) {
            String previous_industry_id = previousDataRS.getString("previous_industry");
            if (previous_industry_id != null) {
                this.previousIndustry = new Industry(previous_industry_id);
            }
            String previous_function_id = previousDataRS.getString("previous_function");
            if (previous_industry_id == null) continue;
            this.previousFunction = new Function(previous_function_id);
        }
        logger.trace("Set previous industry - " + (Object)this.previousIndustry);
        logger.trace("Set previous Function - " + (Object)this.previousFunction);
    }

    private void extractCurrentData(ResultSet currentAndPrefDataRS) throws SQLException {
        while (currentAndPrefDataRS.next()) {
            String current_function_id;
            this.js_login_id = Integer.parseInt(currentAndPrefDataRS.getString("js_login_id"));
            this.firstName = currentAndPrefDataRS.getString("first_name");
            this.lastName = currentAndPrefDataRS.getString("last_name");
            this.email = currentAndPrefDataRS.getString("email");
            this.profileUpdateDate = currentAndPrefDataRS.getString("last_update");
            String keywordsData = currentAndPrefDataRS.getString("key_skills");
            if (keywordsData != null) {
                this.keywords = Arrays.asList(keywordsData.split(","));
            }
            if ((current_function_id = currentAndPrefDataRS.getString("master_functional_area_id")) != null && current_function_id != "0") {
                if (current_function_id == "1") {
                    this.currentFunction = new Function(current_function_id);
                    String other_functional_area_name = currentAndPrefDataRS.getString("other_functional_area");
                    this.currentFunction.setName(other_functional_area_name);
                } else {
                    this.currentFunction = new Function(current_function_id);
                }
            }
            logger.trace("Current Function - " + (Object)this.currentFunction);
            String current_industry_id = currentAndPrefDataRS.getString("master_industry_id");
            if (current_industry_id != null && current_industry_id != "0") {
                if (current_industry_id == "1") {
                    this.currentIndustry = new Industry(current_industry_id);
                    String other_industry_area_name = currentAndPrefDataRS.getString("other_industry_area");
                    this.currentIndustry.setName(other_industry_area_name);
                } else {
                    this.currentIndustry = new Industry(current_industry_id);
                }
            }
            logger.trace("Current Industry - " + (Object)this.currentIndustry);
            String salary = currentAndPrefDataRS.getString("salary");
            if (salary != null && !salary.isEmpty()) {
                this.salary = Integer.parseInt(salary);
            }
            logger.trace("Current Salary - " + salary);
            String experience = currentAndPrefDataRS.getString("experience");
            if (experience != null) {
                this.experience = Integer.parseInt(experience);
            }
            logger.trace("Current work Experience -" + experience);
            String pref_industry = currentAndPrefDataRS.getString("preferred_industry");
            this.preferredIndustry = new ArrayList<Industry>();
            if (pref_industry != null && !pref_industry.isEmpty()) {
                String[] pref_ind_array;
                for (String id : pref_ind_array = pref_industry.split(",")) {
                    this.preferredIndustry.add(new Industry(id));
                }
            }
            logger.trace("preferredIndustry - " + this.preferredIndustry);
            String pref_functions = currentAndPrefDataRS.getString("preferred_function");
            this.preferredFunction = new ArrayList<Function>();
            if (pref_functions != null && !pref_functions.isEmpty()) {
                String[] pref_fun_array;
                for (String id : pref_fun_array = pref_functions.split(",")) {
                    this.preferredFunction.add(new Function(id));
                }
            }
            logger.trace("preferredFunction - " + this.preferredFunction);
            String preferred_location_cities = currentAndPrefDataRS.getString("preferred_location");
            if (preferred_location_cities != null && preferred_location_cities.equals("-1")) {
                this.anyLocation = true;
                logger.trace("NO location preference");
                continue;
            }
            this.setLocations(preferred_location_cities);
            logger.trace("preferred_location_cities -" + preferred_location_cities + " - " + this.location);
        }
    }

    private void setLocations(String preferred_location_cities) throws SQLException {
        this.location = new LinkedList<Location>();
        if (preferred_location_cities != null && !preferred_location_cities.isEmpty()) {
            String location_query = "select id,lat,lng from master_cities where id in (" + preferred_location_cities + ")";
            Connection abcConnection = null;
            Statement abcStatement = null;
            ResultSet currentDataRS = null;
            try {
                abcConnection = PersistentConnectionManager.getAbcReadConnection();
                abcStatement = abcConnection.createStatement();
                logger.trace("location_query - " + location_query);
                currentDataRS = abcStatement.executeQuery(location_query);
                while (currentDataRS.next()) {
                    double lng;
                    String id = currentDataRS.getString("id");
                    double lat = Double.parseDouble(currentDataRS.getString("lat"));
                    if (!(lat != 0.0 & (lng = Double.parseDouble(currentDataRS.getString("lng"))) != 0.0)) continue;
                    Location loc = new Location(id);
                    loc.setLatitude(lat);
                    loc.setLongitude(lng);
                    this.location.add(loc);
                }
            }
            catch (SQLException sqe) {
                logger.error("Sql error location abc", (Throwable)sqe);
            }
            finally {
                logger.debug("Closing abc DS @Locations.");
                try {
                    if (null != currentDataRS && !currentDataRS.isClosed()) {
                        currentDataRS.close();
                    }
                    if (null != abcStatement && !abcStatement.isClosed()) {
                        abcStatement.close();
                    }
                }
                catch (Exception e) {
                    logger.error("Database resources closing error ", (Throwable)e);
                }
            }
        }
    }

    public List<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    static {
        try {
            Class.forName("com.headhonchos.DBConnectionManager.PersistentConnectionManager").newInstance();
        }
        catch (InstantiationException e) {
            logger.error("Error loading class.", (Throwable)e);
        }
        catch (IllegalAccessException e) {
            logger.error("Error loading class.", (Throwable)e);
        }
        catch (ClassNotFoundException e) {
            logger.error("Error loading class.", (Throwable)e);
        }
    }

    @Override
    public String toString() {
        return "JSProfileData3{" +
                "js_login_id=" + js_login_id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", qualification=" + qualification +
                ", currentDesignation=" + currentDesignation +
                ", location=" + location +
                ", experience=" + experience +
                ", salary=" + salary +
                ", foreigner=" + foreigner +
                ", anyLocation=" + anyLocation +
                ", resumeSkillList=" + resumeSkillList +
                ", keywords=" + keywords +
                ", currentFunction=" + currentFunction +
                ", previousFunction=" + previousFunction +
                ", preferredFunction=" + preferredFunction +
                ", currentIndustry=" + currentIndustry +
                ", previousIndustry=" + previousIndustry +
                ", preferredIndustry=" + preferredIndustry +
                ", appliedJobs=" + appliedJobs +
                ", sentJobs=" + sentJobs +
                ", profileUpdateDate='" + profileUpdateDate + '\'' +
                '}';
    }
}