package com.headhonchos.jobSeeker;

import com.headhonchos.DBConnectionManager.ConnectionManager;
import com.headhonchos.GlobalInstances;
import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.paresdResume.Designation;
import com.headhonchos.skill.ResumeSkill;
import json.JSONArray;
import json.JSONException;
import json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by aman on 23/5/14.
 */
public class JSProfileData implements JSProfile {
    private static final Logger logger = LoggerFactory.getLogger((Class)JSProfileData.class);
    private static String APPS = System.getProperty("APPS");

    static {
        Properties properties = new Properties();
        logger.info("Load application variables from configs/datalayer_solr.txt file");
        try {
            String solrPath = APPS + "configs/datalayer_solr.txt";
            properties.load(new FileReader(solrPath));
            GlobalInstances.SOLR_URL = properties.getProperty("SOLR_URL");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int js_login_id;
    private List<ResumeSkill> resumeSkill;
    private List<ResumeSkill> cloudSkill;
    private Function currentFunction;
    private Function previousFunction;
    private List<Function> preferredFunction;
    private Industry currentIndustry;
    private Industry previousIndustry;
    private List<Industry> preferredIndustry;
    private Designation currentDesignation;
    private List<Designation> preferredDesignation;
    private List<Location> location;
    private int minExperience;
    private int maxExperience;
    private int minSalary;
    private int maxSalary;
    private boolean foreigner;
    private boolean anyLocation;
    private List<String> qualification;


    public JSProfileData(int js_login_id) {
        this.js_login_id = js_login_id;
        Connection abcConnection = ConnectionManager.getConnection("abc");
        Connection abcResumeSkillConnection = ConnectionManager.getConnection("abc_resume_skill");
        try {
            String queryAbc = "select pref_ind.js_login_id, " +
                    "max(je.master_lakh_id) as salary, " +
                    "jsp.master_year_id as exp, " +
                    "je.master_industry_id, " +
                    "mi.name as industry, " +
                    "je.master_designation_id, " +
                    "md.name as designation, " +
                    "je.other_designation, " +
                    "je.master_functional_area_id, " +
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
                    "where  jsl.id = "+this.js_login_id+" " +
                    " group by jsl.id;";

            PreparedStatement selectAbcQuery = abcConnection.prepareStatement(queryAbc);

            String queryAbcResumeSkill = "select enriched_skills from abc_resume_skill.process_resumes where js_login_id = "+this.js_login_id;
            PreparedStatement selectAbcResumeQuery = abcResumeSkillConnection.prepareStatement(queryAbcResumeSkill);

            ResultSet resultAbc = selectAbcQuery.executeQuery();
            ResultSet resultAbcResumeSkill = selectAbcResumeQuery.executeQuery();

            //Set resume skill value
            resumeSkill = new LinkedList<ResumeSkill>();

            while(resultAbcResumeSkill.next()) {
                String temp_skills = resultAbcResumeSkill.getString("enriched_skills");
                String[] skillsUnformatted = temp_skills.split(",");
                for(String s: skillsUnformatted) {
                    //skills unformatted have skills in form of "skill_name|E1|L1 where E and L are experience and last used respectively"
                    String[] splittedValuesOfSkill = s.split("\\|");

                    String skillName = splittedValuesOfSkill[0];

                    String experienceNdLastUsed = splittedValuesOfSkill[1];
                    experienceNdLastUsed = experienceNdLastUsed.replace("L",",L");
                    String[] split_loc_exp = experienceNdLastUsed.split(",");


                    String experience = split_loc_exp[0];
                    experience = experience.replace('E',' ');
                    experience = experience.trim();
                    int experienceVal = Integer.parseInt(experience);

                    String lastUsed = split_loc_exp[1];
                    lastUsed = lastUsed.replace('L',' ');
                    lastUsed = lastUsed.trim();
                    int lastUsedVal = Integer.parseInt(lastUsed);
                    resumeSkill.add(new ResumeSkill(skillName, experienceVal,lastUsedVal));
                }

            }
            //get info about an user like current function, previous function, preferred function ,designation ...etc.
            preferredFunction = new LinkedList<Function>();
            List<String> listLocation = new LinkedList<String>();
            while (resultAbc.next()) {
                String master_functional_area_id = resultAbc.getString("master_functional_area_id");
                //String function = resultAbc.getString("function");
                String pref_function = resultAbc.getString("preferred_function");
                currentFunction = new Function(master_functional_area_id);
                //currentFunction.setName(function);
                String[] pref_func_array = pref_function.split(",");
                for(String id : pref_func_array) {
                    Function f = new Function(id);

                    preferredFunction.add(f);

                }

                String master_industry_id = resultAbc.getString("master_industry_id");
                String industry_name = resultAbc.getString("industry");
                String pref_industry = resultAbc.getString("preferred_industry");

                currentIndustry = new Industry(master_industry_id);
                currentIndustry.setName(industry_name);
                preferredIndustry = new LinkedList<Industry>();
                String[] pref_ind_array = pref_industry.split(",");
                for(String id:pref_ind_array) {
                    preferredIndustry.add(new Industry(id));
                }

                String experience = resultAbc.getString("exp");

                minExperience = (int) Math.floor(Integer.parseInt(experience) * GlobalInstances.Emin);
                maxExperience = (int) Math.ceil(Integer.parseInt(experience) * GlobalInstances.Emax);

                String salary = resultAbc.getString("salary");
                minSalary = (int) Math.floor(Integer.parseInt(salary)*GlobalInstances.Smin);
                maxSalary = (int) Math.ceil(Integer.parseInt(salary)*GlobalInstances.Smax);
                String designation_id =  resultAbc.getString("master_designation_id");
                String designation = resultAbc.getString("designation");
                String other_designation = resultAbc.getString("other_designation");

                currentDesignation = new Designation(designation_id);
                if(designation != null && !designation.trim().isEmpty()) {
                    currentDesignation.setName(designation);
                } else {
                    currentDesignation.setName(other_designation);
                }


                location = new LinkedList<Location>();
                String pref_city = resultAbc.getString("preferred_city");
                String[] city_id_array = pref_city.split(",");
                for (String id:city_id_array) {
                    if(id.trim().equals("-1")) {
                        anyLocation = true;
                        break;
                    }
                    listLocation.add(id);
//                    ResultSet latNdlng = selectAbcQuery.executeQuery("select lat,lng from master_cities where id="+id);
//                    Location loc = new Location(id);
//                    if(latNdlng.next()) {
//                        loc.setLatitude(Double.parseDouble(latNdlng.getString("lat")));
//                        loc.setLongitude(Double.parseDouble(latNdlng.getString("lng")));
//                    }
//                    location.add(new Location(id));
                }
            }


            for(String id:listLocation) {
                ResultSet latNdlng = selectAbcQuery.executeQuery("select lat,lng from master_cities where id=" + id);
                Location loc = new Location(id);
                if (latNdlng.next()) {
                    loc.setLatitude(Double.parseDouble(latNdlng.getString("lat")));
                    loc.setLongitude(Double.parseDouble(latNdlng.getString("lng")));
                }
                location.add(loc);
            }

            ResultSet prev_ind_nd_func = selectAbcQuery.executeQuery("select js_login_id,duration_from,duration_to,master_industry_id, master_functional_area_id " +
                    "from js_employments where " +
                    " js_login_id=" +this.js_login_id+ " "+
                    " order by duration_to desc limit 1 OFFSET 1;");
            if(prev_ind_nd_func.next()) {
                previousFunction = new Function(prev_ind_nd_func.getString("master_functional_area_id"));
                previousIndustry = new Industry(prev_ind_nd_func.getString("master_industry_id"));
            }

            // designation using js_designations_hop table
//            ResultSet pref_desigs_resultset = selectAbcResumeQuery.executeQuery("select group_concat(hop_designation_id) as next_designation " +
//                    " from js_designations_hop " +
//                    " where master_designation_id = " + currentDesignation.getId());
//
//            preferredDesignation = new LinkedList<Designation>();
//            //preferredDesignation.add(new Designation(id));
//            if(pref_desigs_resultset.next()) {
//                String[] next_designations_arr = pref_desigs_resultset.getString("next_designation").split(",");
//                for(String id:next_designations_arr) {
//                    preferredDesignation.add(new Designation(id));
//                }
//            }


            // function using hoping js_functional_areas_hop table
//            ResultSet pref_func_using_hop = selectAbcResumeQuery.executeQuery("select  " +
//                    "    group_concat(hop_function_id) as next_function " +
//                    " from " +
//                    "    abc_resume_skill.js_functional_areas_hop " +
//                    " where " +
//                    "    master_functional_area_id = " + this.currentFunction.getId());
//            if (pref_func_using_hop.next()) {
//                String next_functions = pref_func_using_hop.getString("next_function");
//                String[] next_func_array = next_functions.split(",");
//                for(String id:next_func_array) {
//                    this.preferredFunction.add(new Function(id));
//                }
//            }
//

            // industry using hoping js_industries_hop table
//            ResultSet pref_ind_using_hop = selectAbcResumeQuery.executeQuery("select " +
//                    "    group_concat(hop_industry_id) as next_industry " +
//                    " from " +
//                    "    abc_resume_skill.js_industries_hop " +
//                    " where " +
//                    "    master_industry_id = " + this.currentIndustry.getId());
//            if (pref_ind_using_hop.next()) {
//                String next_industries = pref_ind_using_hop.getString("next_industry");
//                String[] next_ind_array = next_industries.split(",");
//                for(String id:next_ind_array) {
//                    this.preferredIndustry.add(new Industry(id));
//                }
//            }

            ResultSet resultQualifications = selectAbcQuery.executeQuery("select  " +
                    " group_concat(distinct master_qualifications.name) as qualifications "+
                    " from " +
                    "    js_educations " +
                    "        Inner join " +
                    "    master_qualifications " +
                    " where " +
                    "    js_educations.master_qualification_id = master_qualifications.id AND js_educations.js_login_id = "+this.js_login_id);

            if (resultQualifications.next()) {
                String tempQualifications = resultQualifications.getString("qualifications");
                if(tempQualifications != null) {
                    qualification = new LinkedList<String>();
                    String[] qual_array = tempQualifications.split(",");
                    for (String name : qual_array) {
                        qualification.add(name);
                    }
                }
            }
            setCloudSkills(resumeSkill);

        } catch (SQLException e) {
            e.printStackTrace();
        }  catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getJSLoginId() {
        return this.js_login_id;
    }

    @Override
    public List<ResumeSkill> getResumeSkill() {

        return this.resumeSkill;
    }

    @Override
    public List<ResumeSkill> getCloudSkill() {

        return this.cloudSkill;
    }

    @Override
    public Function getCurrentFunction() {

        return this.currentFunction;
    }

    @Override
    public Function getPreviousFunction() {

        return this.previousFunction;
    }

    @Override
    public List<Function> getPreferredFunction() {

        return this.preferredFunction;
    }

    @Override
    public Industry getCurrentIndustry() {

        return this.currentIndustry;
    }

    @Override
    public Industry getPreviousIndustry() {

        return this.previousIndustry;
    }

    @Override
    public List<Industry> getPreferredIndustry() {

        return this.preferredIndustry;
    }

    @Override
    public Designation getCurrentDesignation() {

        return this.currentDesignation;
    }

    @Override
    public List<Designation> getPreferredDesignation() {

        return this.preferredDesignation;
    }

    @Override
    public List<Location> getLocation() {

        return this.location;
    }

    @Override
    public int getMinExperience() {

        return this.minExperience;
    }

    @Override
    public int getMaxExperience() {

        return this.maxExperience;
    }

    @Override
    public int getMinSalary() {

        return this.minSalary;
    }

    @Override
    public int getMaxSalary() {

        return this.maxSalary;
    }

    @Override
    public boolean isForeigner() {

        return this.foreigner;
    }

    @Override
    public boolean isAnyLocation() {

        return this.anyLocation;
    }

    @Override
    public List<String> getQualification() {

        return this.qualification;
    }

    protected void setCloudSkills(List<ResumeSkill> listOfSkills) throws IOException, JSONException {

        if(listOfSkills.size() == 0) {
            System.out.println("Empty List is passed to String");
            return;
        }

        String skillsSpaceSeperated = null;
        for(ResumeSkill temp:listOfSkills) {
            if(skillsSpaceSeperated == null)  skillsSpaceSeperated =  "\""+temp.getName() +"\"";
            skillsSpaceSeperated = skillsSpaceSeperated +  "  " +"\""+temp.getName() +"\"";
        }
        //String skillsSpaceSeperated = StringUtils.join(listOfSkills, ' ');
        String urlString = GlobalInstances.SOLR_URL+"/alllevel";

        //readJsonFromUrl(urlString+"/select?q=skills:("+skillsSpaceSeperated+") OR l0_skills_separated:("+skillsSpaceSeperated+")&fl=l0_skills_separated,skills,linkedin_level1_separated&wt=xml");
        String tempStr = "skills:("+skillsSpaceSeperated+") OR l0_skills_separated:("+skillsSpaceSeperated+")";
        tempStr = URLEncoder.encode(tempStr,"UTF-8");
        JSONObject json = readJsonFromUrl(urlString+"/select?q="+tempStr+"&wt=json&fl=l0_skills_separated&fl=skills&fl=linkedin_level1_separated");
        if(json != null) {
            JSONObject resp = json.getJSONObject("response");
            JSONArray recs = resp.getJSONArray("docs");


            String skills = "";
//            String linkedin_level1 = "";
            String l0_skills = "";

            cloudSkill = new LinkedList<ResumeSkill>();

            for (int k = 0; k < recs.length(); k++)
            {
                JSONObject rec = recs.getJSONObject(k);
                try
                {
                    try {
                        skills = rec.getString("skills");
                    } catch (Exception e) {

                    }
//                    try {
//                        linkedin_level1 = rec.getString("linkedin_level1_separated");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    try {
                        l0_skills = rec.getString("l0_skills_separated");
                    } catch (Exception e) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if(skills != null) {
                    if (skills != null && skills != "") {
                        skills = skills.replace('[', ' ');
                        skills = skills.replace(']', ' ');
                        String[] tempSkillsArr = skills.split(",");
                        for (String s : tempSkillsArr) {
                            s = s.trim();
                            if (s != null && s != "")
                                cloudSkill.add(new ResumeSkill(s, 0, 0));
                        }
                    }
                }
//                if(linkedin_level1 != null) {
//                    if (linkedin_level1 != null && linkedin_level1 != "") {
//                        linkedin_level1 = linkedin_level1.replace('[', ' ');
//                        linkedin_level1 = linkedin_level1.replace(']', ' ');
//                        String[] tempSkillsArr = linkedin_level1.split(",");
//                        for (String s : tempSkillsArr) {
//                            s = s.trim();
//                            if (s != null && s != "")
//                                cloudSkill.add(new ResumeSkill(s, 0, 0));
//                        }
//                    }
//                }
                if(l0_skills != null) {
                    if (l0_skills != null && l0_skills != "") {
                        l0_skills = l0_skills.replace('[', ' ');
                        l0_skills = l0_skills.replace(']', ' ');
                        String[] tempSkillsArr = l0_skills.split(",");
                        for (String s : tempSkillsArr) {
                            s = s.trim();
                            if (s != null && s != "")
                                cloudSkill.add(new ResumeSkill(s, 0, 0));
                        }
                    }
                }

//                if (allSkills.equals(""))
//                    allSkills = name;
//                else {
//                    allSkills = new StringBuilder().append(allSkills).append(",").append(name).toString();
//                }
            }

        }


//        SolrServer server = new HttpSolrServer(urlString);
//
//        SolrQuery query = new SolrQuery();
//        query.setQuery("skills:("+skillsSpaceSeperated+") OR l0_skills_separated:("+skillsSpaceSeperated+")");
//        query.setFields("l0_skills_separated", "linkedin_level1_separated", "skills");
//
//        QueryResponse response = server.query(query);
//        SolrDocumentList results = response.getResults();
//        cloudSkill = new LinkedList<ResumeSkill>();
//        for (SolrDocument document : results) {
//
//            if(document.getFieldValue("l0_skills_separated") != null) {
//                String tempStr = document.getFieldValue("l0_skills_separated").toString();
//                if (tempStr != null && tempStr != "") {
//                    tempStr = tempStr.replace('[', ' ');
//                    tempStr = tempStr.replace(']', ' ');
//                    String[] tempSkillsArr = tempStr.split(",");
//                    for (String s : tempSkillsArr) {
//                        s = s.trim();
//                        if (s != null && s != "")
//                            cloudSkill.add(new ResumeSkill(s, 0, 0));
//                    }
//                }
//            }
//
//
//            if(document.getFieldValue("linkedin_level1_separated") != null) {
//                String tempStr = document.getFieldValue("linkedin_level1_separated").toString();
//                if (tempStr != null && tempStr != "") {
//                    tempStr = tempStr.replace('[', ' ');
//                    tempStr = tempStr.replace(']', ' ');
//                    String[] tempSkillsArr = tempStr.split(",");
//                    for (String s : tempSkillsArr) {
//                        s = s.trim();
//                        if (s != null && s != "")
//                            cloudSkill.add(new ResumeSkill(s, 0, 0));
//                    }
//                }
//            }
//
//            if(document.getFieldValue("skills") != null) {
//                String tempStr = document.getFieldValue("skills").toString();
//                if (tempStr != null && tempStr != "") {
//                    tempStr = tempStr.replace('[', ' ');
//                    tempStr = tempStr.replace(']', ' ');
//                    String[] tempSkillsArr = tempStr.split(",");
//                    for (String s : tempSkillsArr) {
//                        s = s.trim();
//                        if (s != null && s != "")
//                            cloudSkill.add(new ResumeSkill(s, 0, 0));
//                    }
//                }
//            }
//
//            //System.out.println("aman");
//
//        }
    }
    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        System.out.println(url);

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    private String readAll(Reader rd)
            throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char)cp);
        }
        return sb.toString();
    }
}
