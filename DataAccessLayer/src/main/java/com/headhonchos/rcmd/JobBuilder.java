package com.headhonchos.rcmd;

import com.headhonchos.DBConnectionManager.PersistentConnectionManager;
import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Job;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.skill.EnrichedSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by ishu on 28/3/14.
 */
public class JobBuilder {

    private static final Logger logger = LoggerFactory.getLogger(JobBuilder.class);
    private static Map<Integer,String> functionNameIdMap = new HashMap<Integer, String>();
    private static Map<Integer,String> industryNameIdMap = new HashMap<Integer, String>();

    //todo Move this to Function and Industry Class
    static {
        logger.debug("Init JobBuilder class.");
        String masterFunctionQuery = "SELECT id,name FROM abc.master_functional_areas";
        String masterIndustryQuery = "SELECT id,name FROM abc.master_industries";

        try {
            Class.forName("com.headhonchos.DBConnectionManager.PersistentConnectionManager").newInstance();
        } catch (InstantiationException e) {
            logger.error("Error loading class.",e);
        } catch (IllegalAccessException e) {
            logger.error("Error loading class.", e);
        } catch (ClassNotFoundException e) {
            logger.error("Error loading class.", e);
        }

        Connection abcReadConnection = PersistentConnectionManager.getAbcReadConnection();
        try {
            Statement statement = abcReadConnection.createStatement();
            ResultSet masterFunctionResultSet = statement.executeQuery(masterFunctionQuery);
            while(masterFunctionResultSet.next()){
                int id = masterFunctionResultSet.getInt("id");
                String name = masterFunctionResultSet.getString("id");
                functionNameIdMap.put(id,name);
            }

            ResultSet masterIndustryResultSet = statement.executeQuery(masterIndustryQuery);
            while(masterIndustryResultSet.next()){
                int id =masterIndustryResultSet.getInt("id");
                String name=masterIndustryResultSet.getString("name");
                industryNameIdMap.put(id,name);
            }
        } catch (SQLException e) {
            logger.error("Error Loading master Function,Industry",e);
        }
    }

    public static Job buildJob(Map<String, List<String>> formData) throws SQLException {
        logger.debug("Job Data: {}",formData);
        Job job = new Job();
        extractAndSet_Function(formData, job);
        extractAndSet_Industry(formData, job);
        extractAndSet_Experience(formData, job);
        extractAndSet_Salary(formData, job);
        extractAndSet_Skills(formData, job);
        extractAndSet_Location(formData, job);
        return job;
    }

    public static Job buildSimilarCandidate(Map<String, List<String>> formData) throws SQLException {
        logger.debug("Job Data: {}",formData);
        Job job = new Job();
        job.setJobQuery(false);
        extractAndSet_Id(formData, job);
        extractAndSet_Function(formData, job);
        extractAndSet_Industry(formData, job);
        extractAndSet_Experience(formData, job);
        extractAndSet_Salary(formData, job);
        extractAndSet_JS_Skills(formData, job);
        extractAndSet_Location(formData, job);
        return job;
    }

    private static void extractAndSet_Id(Map<String, List<String>> formData, Job job) {
        logger.debug("Extracting Id for JS");
        String jsid = formData.get("id").get(0);
        try{
            job.setId(Integer.parseInt(jsid));
        } catch (Exception e) {
            logger.error("Erroor in setting id: {} Exception: {}", jsid, e.getMessage());
        }
    }

    private static void extractAndSet_Function(Map<String, List<String>> formData, Job job) {
        logger.debug("Extracting Function for job");
        String[] master_functional_area_ids = formData.get("master_functional_area_id").get(0).split(",");
        logger.debug("master_functional_area_ids: {}",master_functional_area_ids);
        ArrayList<Function> functions = new ArrayList<Function>();
        for(String function_id : master_functional_area_ids){
            functions.add(new Function(function_id));
        }
        job.setMasterFunctionalAreas(functions);
    }

    private static void extractAndSet_Industry(Map<String, List<String>> formData, Job job) {
        logger.debug("Extracting Industry for job");
        String[] masterIndustryIds = formData.get("master_industry_id").get(0).split(",");
        logger.debug("master_industries_ids: {}",masterIndustryIds);
        ArrayList<Industry> industries = new ArrayList<Industry>();
        for(String industry_id : masterIndustryIds){
            industries.add(new Industry(industry_id));
        }
        job.setMasterIndustries(industries);
    }

    private static void extractAndSet_Experience(Map<String, List<String>> formData, Job job) {
        logger.debug("Extracting Experience for job");
        int minExp = Integer.parseInt(formData.get("work_experience_min").get(0));
        int maxExp = Integer.parseInt(formData.get("work_experience_max").get(0));
        logger.debug("Experience:min {} max {}",minExp,maxExp);
        //Set Experience
        job.setMinExperience(minExp);
        job.setMaxExperience(maxExp);
    }

    private static void extractAndSet_Salary(Map<String, List<String>> formData, Job job) {
        logger.debug("Extracting Salary for job");
        String annual_salary_min = formData.get("annual_salary_min").get(0);
        String annual_salary_max = formData.get("annual_salary_max").get(0);
        logger.debug("Salary is minSal {},MaxSal {}",annual_salary_min,annual_salary_max);
        //Set Salary
        job.setMinSalary(Integer.parseInt(annual_salary_min));
        job.setMaxSalary(Integer.parseInt(annual_salary_max));
    }

    private static void extractAndSet_Skills(Map<String, List<String>> formData, Job job) {
        logger.debug("Extracting Skills for job");
        //Set Skills
        ArrayList<EnrichedSkill> systemSkills = new ArrayList<EnrichedSkill>();
        String final_skills = formData.get("final_skills").get(0);
        if((final_skills.isEmpty()) || (final_skills == null)) {
            job.setSystemSkills(new ArrayList<EnrichedSkill>());
        }
        else{
            List<String> skills = Arrays.asList(final_skills.split(","));
            for (String skill : skills) {
                String[] skillsWeight = skill.split("\\^");
                if(skillsWeight.length==3) {
                    String skillName = skillsWeight[1];
                    if(!skillName.trim().isEmpty()) {
                        if(!skillsWeight[2].trim().isEmpty()) {
                            double weight = Double.parseDouble(skillsWeight[2]);
                            systemSkills.add(new EnrichedSkill(skillName, weight));
                        }
                    }
                }
                else{
                    logger.debug("corrput skill - " + skill);
                }
            }
            job.setSystemSkills(systemSkills);
        }
        logger.debug("System Skills: {}",systemSkills);

        ArrayList<EnrichedSkill> manualSkills = new ArrayList<EnrichedSkill>();
        for (String manualSkill : formData.get("keywords").get(0).split(","))
        {
            manualSkills.add(new EnrichedSkill(manualSkill, 1.0));
        }
        logger.debug("Keywords: {}",manualSkills);
        job.setManualskills(manualSkills);
    }

    private static void extractAndSet_JS_Skills(Map<String, List<String>> formData, Job job) {
        logger.debug("Extracting Skills for Similar Candidates");
        ArrayList<EnrichedSkill> manualSkills = new ArrayList<EnrichedSkill>();
        List<String> manualSkillList = Arrays.asList(formData.get("keywords").get(0).toLowerCase().replaceAll("  ,",",").split(","));
        for (String manualSkill : formData.get("keywords").get(0).split(","))
        {
            manualSkills.add(new EnrichedSkill(manualSkill.trim(), 20000.0));
        }
        ArrayList<EnrichedSkill> recommendedSkills = new ArrayList<EnrichedSkill>();
        Set<String> recSkillSet = new HashSet<>(Arrays.asList(formData.get("recommendedSkills").get(0).toLowerCase().split(",")));
        recSkillSet.removeAll(manualSkillList);
        for (String recommendedSkill : recSkillSet)
        {
            recommendedSkills.add(new EnrichedSkill(recommendedSkill.trim(), 10000.0));
        }
        logger.debug("Keywords: {}",manualSkills);
        job.setManualskills(manualSkills);
        job.setSystemSkills(recommendedSkills);
    }

    private static void extractAndSet_Location(Map<String, List<String>> formData, Job job) throws SQLException {
        logger.debug("Extracting Location for job");
        List<String> locations = formData.get("master_location_id");
        StringBuilder stringBuilder = new StringBuilder();
        for(String locId :locations){
            stringBuilder.append(",").append(locId);
        }
        String stringForQuery = stringBuilder.toString().replaceFirst(",", "");
        String locationQuery ="select id,lat,lng,name,master_country_id FROM master_cities where id in ("+ stringForQuery.trim()+")" ;
        logger.debug("location SQL query :{} ", locationQuery);
        Connection readOnlyDatabaseConnection = PersistentConnectionManager.getAbcReadConnection();
        Statement statement = readOnlyDatabaseConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(locationQuery);
        List<Location> locationList = new ArrayList<Location>();
        while(resultSet.next()){
            Location location = new Location(resultSet.getString("id"));
            location.setCityName(resultSet.getString("name"));
            location.setLatitude(Double.parseDouble(resultSet.getString("lat")));
            location.setLongitude(Double.parseDouble(resultSet.getString("lng")));
            location.setCountryId(resultSet.getString("master_country_id"));
            locationList.add(location);
        }
        logger.debug("Locations - "+locationList);
        job.setLocations(locationList);
        if(statement != null && !statement.isClosed()){
            statement.close();
        }
        if(resultSet!=null && !resultSet.isClosed()){
            resultSet.close();
        }

        boolean locationPref = false;
        job.setLocationPreference(locationPref);
    }
}
