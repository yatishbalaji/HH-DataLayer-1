package com.headhonchos.rcmd;

import com.headhonchos.GlobalInstances;
import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Job;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.parsedJob.LTTTT2ParsedJob;
import com.headhonchos.parsedJob.LTTTT2Skills;
import com.headhonchos.skill.EnrichedSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by ishu on 28/3/14.
 */
public class JobDal {

    Logger logger = LoggerFactory.getLogger(JobDal.class);
    Map<String,String> functionNameIdMap = new HashMap<String, String>();
    Map<String,String> industryNameIdMap = new HashMap<String, String>();
    /**
     * @author Ishu Khatri
     */
    public JobDal() throws SQLException {
        Connection readOnlyDatabaseConnection = getConnection("abc");
        Statement statement = readOnlyDatabaseConnection.createStatement();

        String masterFunctionQuery = "SELECT id,name FROM abc.master_functional_areas";
        ResultSet masterFunctionResultSet = statement.executeQuery(masterFunctionQuery);
        while(masterFunctionResultSet.next()){
            functionNameIdMap.put(masterFunctionResultSet.getString("name"), masterFunctionResultSet.getString("id"));
        }

        String masterIndustryQuery = "SELECT id,name FROM abc.master_industries";
        ResultSet masterIndustryResultSet = statement.executeQuery(masterIndustryQuery);
        while(masterIndustryResultSet.next()){
            industryNameIdMap.put(masterIndustryResultSet.getString("name"), masterIndustryResultSet.getString("id"));
        }

    }

    public Job getJob(String id) throws SQLException {
        //id

        Connection readOnlyDatabaseConnection;
        //PreparedStatement parsedJobQuery = null;
        List<Function> fList = new ArrayList<Function>();
        List<Industry> iList = new ArrayList<Industry>();
        List<Location> lList = new ArrayList<Location>();
        Job job = new Job();
        readOnlyDatabaseConnection = getConnection("abc");
        if (readOnlyDatabaseConnection != null) {
            PreparedStatement selectQuery = readOnlyDatabaseConnection.prepareStatement(
                    "SELECT " +
                            "  postings.annual_salary_min," +
                            "  postings.annual_salary_max," +
                            "  postings.work_experience_min," +
                            "  postings.work_experience_max," +
                            "  postings.master_functional_area_id," +
                            "  master_fun.name," +
                            "  postings.master_industry_id," +
                            "  master_ind.name " +
                            "FROM" +
                            "  cl_job_postings AS postings " +
                            "  LEFT JOIN master_functional_areas AS master_fun " +
                            "    ON master_fun.id = postings.master_functional_area_id " +
                            "  LEFT JOIN master_industries AS master_ind " +
                            "    ON master_ind.id = postings.master_industry_id " +
                            "WHERE postings.id =  '" + id + "'");

            ResultSet resultSet = selectQuery.executeQuery();

            if (resultSet.next()) {
                String lString;
                int lInt;

                //Create Job Object and set all values for further use for parsing and creating recommendation query.

                job.setId(Integer.parseInt(id));

                lInt = resultSet.getInt("postings.annual_salary_min");
                job.setMinSalary(lInt);

                lInt = resultSet.getInt("postings.annual_salary_max");
                job.setMaxSalary(lInt);

                lInt = resultSet.getInt("postings.work_experience_min");
                job.setMinExperience(lInt);

                lInt = resultSet.getInt("postings.work_experience_max");
                job.setMaxExperience(lInt);


                //Set Function Object
                Function function;// = new Function();
                lInt = resultSet.getInt("postings.master_functional_area_id");
                function = new Function(String.valueOf(lInt));

                lString = resultSet.getString("master_fun.name");
                function.setName(lString);


                fList.add(function);


                //Set Industry Object
                Industry industry;// = new Industry();
                lInt = resultSet.getInt("postings.master_industry_id");
                industry = new Industry(Integer.toString(lInt));//.setId(String.valueOf(lInt));

                lString = resultSet.getString("master_ind.name");
                industry.setName(lString);


                iList.add(industry);

            }
            PreparedStatement selectQuery2 = readOnlyDatabaseConnection.prepareStatement(
                    "SELECT " +
                            "  master_fun.name," +
                            "  preferred_fun.master_functional_area_id  " +
                            "FROM" +
                            "  cl_job_posting_preferred_functions AS preferred_fun " +
                            "  LEFT JOIN master_functional_areas AS master_fun " +
                            "    ON master_fun.id = preferred_fun.master_functional_area_id " +
                            "WHERE preferred_fun.cl_job_posting_id =  '" + id + "'");

            ResultSet resultSet2 = selectQuery2.executeQuery();

            while (resultSet2.next()) {
                String lString;
                int lInt;
                lInt = resultSet2.getInt("preferred_fun.master_functional_area_id");
                Function function;
                function = new Function(String.valueOf(lInt));

                lString = resultSet2.getString("master_fun.name");
                function.setName(lString);

                fList.add(function);
            }
            job.setMasterFunctionalAreas(fList);

            PreparedStatement selectQuery3 = readOnlyDatabaseConnection.prepareStatement(
                    "SELECT " +
                            "  master_ind.name," +
                            "  preferred_ind.master_industry_id  " +
                            "FROM" +
                            "  cl_job_posting_preferred_industries AS preferred_ind " +
                            "  LEFT JOIN master_industries AS master_ind " +
                            "    ON master_ind.id = preferred_ind.master_industry_id " +
                            "WHERE preferred_ind.cl_job_posting_id =  '" + id + "'");

            ResultSet resultSet3 = selectQuery3.executeQuery();

            while (resultSet3.next()) {
                String lString;
                int lInt;
                //Set Industry Object
                Industry industry;// = new Industry();
                lInt = resultSet3.getInt("preferred_ind.master_industry_id");
                industry = new Industry(Integer.toString(lInt));//.setId(String.valueOf(lInt));

                lString = resultSet3.getString("master_ind.name");
                industry.setName(lString);


                iList.add(industry);
            }
            job.setMasterIndustries(iList);

            PreparedStatement selectQuery4 = readOnlyDatabaseConnection.prepareStatement(
                    "SELECT " +
                            "  master_cities.id," +
                            "  master_cities.name," +
                            "  posting_locations.master_country_id," +
                            "  master_cities.lat," +
                            "  master_cities.lng " +
                            "FROM" +
                            "  cl_job_posting_locations AS posting_locations " +
                            "  LEFT JOIN master_cities " +
                            "    ON posting_locations.master_city_id = master_cities.id " +
                            "WHERE posting_locations.cl_job_posting_id =  '" + id + "'");

            ResultSet resultSet4 = selectQuery4.executeQuery();
            int lInt = -1;
            while (resultSet4.next()) {

                String lString;

                //Set location object
                lInt = resultSet4.getInt("master_cities.id");
                if (lInt == -1) {
                    job.setLocationPreference(true);
                    break;
                } else {
                    job.setLocationPreference(false);
                }

                lString = resultSet4.getString("master_cities.id");
                Location l = new Location(lString);//this is job_location id

                lString = resultSet4.getString("master_cities.name");
                l.setCityName(lString);

                lInt = resultSet4.getInt("posting_locations.master_country_id");
                l.setCountryId(String.valueOf(lInt)); // this is country id

                lString = resultSet4.getString("master_cities.lat");
                l.setLatitude(Double.parseDouble(lString));

                lString = resultSet4.getString("master_cities.lng");
                l.setLongitude(Double.parseDouble(lString));

                lList.add(l);

            }
            if (lInt != -1) {
                job.setLocations(lList);
            }

            readOnlyDatabaseConnection.close();

            return job;

        }

        readOnlyDatabaseConnection.close();
        return null;
    }

    public Job getJob(Map<String,List<String>> formData) throws SQLException {
        Job job = new Job();
        System.out.println("-------------DAL-------------------");
        for(Map.Entry<String,List<String>> e:formData.entrySet()){
            System.out.println(e.getKey() + " - " + e.getValue());
        }

        ArrayList<Function> functions = new ArrayList<Function>();
        for(String function_id : formData.get("master_functional_area_id").get(0).split(",")){
            functions.add(new Function(function_id));
        }
        job.setMasterFunctionalAreas(functions);

        ArrayList<EnrichedSkill> systemSkills = new ArrayList<EnrichedSkill>();
        String final_skills = formData.get("final_skills").get(0);
//        System.out.println("final _skill s- "+final_skills);

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
                    System.out.println("corrput skill - "+skill);
                }
            }
            job.setSystemSkills(systemSkills);
        }

        ArrayList<EnrichedSkill> manualSkills = new ArrayList<EnrichedSkill>();
        for (String manualSkill : formData.get("keywords").get(0).split(","))
        {
            manualSkills.add(new EnrichedSkill(manualSkill, 1.0));
        }
        job.setManualskills(manualSkills);

        ArrayList<Industry> industries = new ArrayList<Industry>();
        for(String industry_id : formData.get("master_industry_id").get(0).split(",")){
            industries.add(new Industry(industry_id));
        }
        job.setMasterIndustries(industries);

        job.setMinExperience(Integer.parseInt(formData.get("work_experience_min").get(0)));
        job.setMaxExperience(Integer.parseInt(formData.get("work_experience_max").get(0)));
        job.setMinSalary(Integer.parseInt(formData.get("annual_salary_min").get(0)));
        job.setMaxSalary(Integer.parseInt(formData.get("annual_salary_max").get(0)));

        List<String> locations = formData.get("master_location_id");
        StringBuilder stringBuilder = new StringBuilder();
        for(String locId :locations){
            stringBuilder.append(",").append(locId);
        }
        String stringForQuery = stringBuilder.toString().replaceFirst(",", "");
        String query ="select id,lat,lng,name,master_country_id FROM master_cities where id in ("+ stringForQuery.trim()+")" ;
        System.out.println("location query - "+query);
        Connection readOnlyDatabaseConnection = getConnection("abc");
        Statement statement = readOnlyDatabaseConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<Location> locationList = new ArrayList<Location>();
        while(resultSet.next()){
            Location location = new Location(resultSet.getString("id"));
            location.setCityName(resultSet.getString("name"));
            location.setLatitude(Double.parseDouble(resultSet.getString("lat")));
            location.setLongitude(Double.parseDouble(resultSet.getString("lng")));
            location.setCountryId(resultSet.getString("master_country_id"));
            locationList.add(location);
        }
        job.setLocations(locationList);
        readOnlyDatabaseConnection.close();

        boolean locationPref = false;
        job.setLocationPreference(locationPref);

        return job;
    }

    public LTTTT2ParsedJob getLttt2ParsedJob(String id) throws SQLException {

        Connection readOnlyDatabaseConnection;

        readOnlyDatabaseConnection = getConnection("abc_resume_skill");
        if (readOnlyDatabaseConnection != null) {
            System.out.println("Entering block");
            String allSkills = "";
            PreparedStatement selectQuery = readOnlyDatabaseConnection.prepareStatement(
                    "SELECT " +
                            "  job_title," +
                            "  job_description," +
                            "  keywords," +
                            "  essential_skills," +
                            "  other_details," +
                            "  master_industry," +
                            "  designation," +
                            "  master_functional_area " +
                            "FROM" +
                            "  job_parsed " +
                            "WHERE cl_job_posting_id ='" + id + "'");

            ResultSet jobSkillsResult = selectQuery.executeQuery();


            LTTTT2ParsedJob parsedJob = new LTTTT2ParsedJob();
            LTTTT2Skills skills = new LTTTT2Skills();
            while (jobSkillsResult.next()) {

                String lString = jobSkillsResult.getString("job_title");
                allSkills += lString;
                skills.setJobTitleSkills(new ArrayList<String>(createList(lString)));

                lString = jobSkillsResult.getString("job_description");
                allSkills += lString;
                skills.setJobDescriptionSkills(new ArrayList<String>(createList(lString)));

                lString = jobSkillsResult.getString("keywords");
                allSkills += lString;
                skills.setJobKeyWordsSkills(new ArrayList<String>(createList(lString)));

                lString = jobSkillsResult.getString("essential_skills");
                allSkills += lString;
                skills.setEssentialSkills(createList(lString));

                lString = jobSkillsResult.getString("other_details");
                allSkills += lString;
                skills.setOtherDetailsSkills(new ArrayList<String>(createList(lString)));

                lString = jobSkillsResult.getString("master_industry");
                allSkills += lString;
                skills.setMasterIndustrySkills(new ArrayList<String>(createList(lString)));

                lString = jobSkillsResult.getString("master_functional_area");
                allSkills += lString;
                skills.setFunctionalAreaSkills(new ArrayList<String>(createList(lString)));

                lString = jobSkillsResult.getString("designation");
                allSkills += lString;
                skills.setDesignationSkills(new ArrayList<String>(createList(lString)));

                parsedJob.setLTTTT2Skills(skills);
                Map<String, Integer> skillsFrequencyMap = getFrequencyFromSkills(allSkills);
                parsedJob.setSkillsFrequencyMap(skillsFrequencyMap);
                return parsedJob;
            }
        }
        return null;
    }

    private Map<String, Integer> getFrequencyFromSkills(String allSkills) throws SQLException {
        List<String> list = createList(allSkills);
        Map<String, Integer> skillsFrequency = getSkillsFrequency(list);
        return skillsFrequency;
    }

    public Map<String, Integer> getSkillsFrequency(List<String> skills) throws SQLException {
        Connection readOnlyDatabaseConnection;
        Map<String, Integer> skillsPower = new HashMap<String, Integer>();

        String commaSeperatedSkills = org.apache.commons.lang3.StringUtils.join(skills, "','");
        commaSeperatedSkills = "'" + commaSeperatedSkills + "'";

        readOnlyDatabaseConnection = getConnection("abc_resume_skill");
        if (readOnlyDatabaseConnection != null) {
            PreparedStatement selectQuery = readOnlyDatabaseConnection.prepareStatement(
                    "SELECT " +
                            "  NAME,js_frequency " +
                            "FROM " +
                            "  abc_resume_skill.job_non_repeated_skills " +
                            "WHERE is_deleted = 0 " +
                            "  AND (" +
                            "    js_frequency < 50000 " +
                            "    OR exception = 1" +
                            "  ) " +
                            "  AND NAME IN (" + commaSeperatedSkills + ")");
            ResultSet lData = selectQuery.executeQuery();
            while (lData.next()) {
                int calcValue = lData.getInt("js_frequency");
                String skillName = lData.getString("name");
                skillsPower.put(skillName, calcValue);
            }
        }
        readOnlyDatabaseConnection.close();
        return skillsPower;
    }

    public java.util.List<java.lang.String> createList(String commaSeparatedSkills) {
        commaSeparatedSkills = commaSeparatedSkills.trim();
        if (commaSeparatedSkills.length() == 0) {
            List<String> emptyList = new ArrayList<String>();
            //emptyList.add("");
            return emptyList;
        }

        commaSeparatedSkills = commaSeparatedSkills.replaceAll(",,+", ",");
        commaSeparatedSkills = commaSeparatedSkills.replaceAll(",\\s+,", ",");
        commaSeparatedSkills = commaSeparatedSkills.replaceAll(",\\s+", ",");
        commaSeparatedSkills = commaSeparatedSkills.replaceAll("\\s+,", ",");

        String[] skillsStringArray = commaSeparatedSkills.split(",");
        //skillsList.removeAll(Arrays.asList("", null));
        return Arrays.asList(skillsStringArray);
    }

    public Connection getConnection(String databaseName) {
        logger.debug("Driver - "+GlobalInstances.DRIVER);

        Connection readOnlyDatabaseConnection = null;
        //PreparedStatement parsedJobQuery = null;
        try {
            Class.forName(GlobalInstances.DRIVER).newInstance();
            if (databaseName.trim().equalsIgnoreCase("abc")) {
                logger.debug("getting abc connection");
                readOnlyDatabaseConnection = DriverManager.getConnection(
                        GlobalInstances.SLAVE_ABC,
                        GlobalInstances.SLAVE_ABC_USER,
                        GlobalInstances.SLAVE_ABC_PASSWORD
                );
            } else if (databaseName.trim().equalsIgnoreCase("abc_resume_skill")) {
                logger.debug("getting abc_resume_skill connection");
                readOnlyDatabaseConnection = DriverManager.getConnection(
                        GlobalInstances.SLAVE_ABC_RESUME_SKILL,
                        GlobalInstances.SLAVE_ABC_RESUME_SKILL_USER,
                        GlobalInstances.SLAVE_ABC_RESUME_SKILL_PASSWORD
                );
            }
        } catch (SQLException e) {
            System.err.println("Database Connection error occurred..Exiting JVM...");
            System.out.println(e);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return readOnlyDatabaseConnection;
    }
}
