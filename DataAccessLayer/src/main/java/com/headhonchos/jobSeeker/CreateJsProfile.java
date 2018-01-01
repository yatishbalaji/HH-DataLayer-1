package com.headhonchos.jobSeeker;

import com.headhonchos.DBConnection;
import com.headhonchos.DatabaseOps;
import com.headhonchos.GlobalInstances;
import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.paresdResume.Designation;
import com.headhonchos.paresdResume.ParsedResumeAndJob;
import com.headhonchos.skill.JobSeekerSkill;
import com.headhonchos.skill.ResumeSkill;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by richa on 1/7/14.
 */

public class CreateJsProfile implements JSProfile {

    SolrServer solr = null;
    SolrQuery parameters = null;

    String db_abc = "abc";
    String db_abc_resume_skill = "abc_resume_skill";

    DatabaseOps databaseOpsAbc = null;
    DatabaseOps databaseOpsAbcResumeSkill = null;
    DBConnection dbConnection = null;

    ParsedResumeAndJob parsedResumeAndJob = null;

    private String profile = "";

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
    private String rawText;

    List<JobSeekerSkill> skills;
    List<String> desg;
    List<String> inst;
    List<String> emailid;
    List<String> mobNo;
    List<String> qual;
    String idd;
    String skill = "", enrich_skill = "";


    public CreateJsProfile() {
        dbConnection = new DBConnection();
        databaseOpsAbc = dbConnection.getSlaveConnection(db_abc);
        databaseOpsAbcResumeSkill = dbConnection.getSlaveConnection(db_abc_resume_skill);

        solr = new HttpSolrServer(GlobalInstances.SOLR_URL + "/alllevel");
        parameters = new SolrQuery();
    }

    public void processId(int js_login_id) {
        this.js_login_id = js_login_id;
        this.profile = "";
        //"jsp.caption, concat_ws(' , ',jsp.key_skills,group_concat(ml.name),group_concat(ms.name),group_concat(mq.name),group_concat(jedu.other_specialization)) as key_skills, " +
        //"left join js_employments as je on je.js_login_id = pref_loc.js_login_id " +

        String query = "select pref_ind.js_login_id, max(je.master_lakh_id) as salary, jsp.master_year_id as exp, je.master_industry_id, mi.name as industry, je.master_designation_id, " +
                "if(je.master_designation_id = '0' or je.master_designation_id = '1', if(je.other_designation is null,' ',je.other_designation),md.name) as designation, " +
                "je.master_functional_area_id, mfa.name as 'function', " +
                "group_concat(distinct pref_ind.master_industry_id) as preferred_industry, group_concat( distinct pref_func.master_functional_area_id) as 'preferred_function', " +
                "group_concat(distinct pref_loc.master_city_id) as preferred_city, " +
                "jsp.caption, concat_ws(' , ',jsp.key_skills,ml.name,ms.name,mq.name,jedu.other_specialization) as key_skills, " +
                "je.duration_to, je.duration_from, je.job_profile " +
                "from js_logins jsl " +
                "left join js_profiles jsp on jsl.id=jsp.js_login_id " +
                "left join abc.js_educations jedu on jedu.js_login_id=jsp.js_login_id " +
                "left join master_specializations ms on ms.id=jedu.master_specialization_id " +
                "left join master_qualifications mq on mq.id=jedu.master_qualification_id " +
                "left join master_levels ml on ml.id=jedu.master_level_id " +
                "left join js_preferences_industries as pref_ind on jsl.id=pref_ind.js_login_id " +
                "left join js_preferences_functions as pref_func on jsl.id=pref_func.js_login_id " +
                "left join js_preferences_locations as pref_loc on jsl.id=pref_loc.js_login_id " +
                "left join js_employments as je on je.js_login_id = jsl.id " +
                "left JOIN js_employments je2 ON (je2.js_login_id = je.js_login_id AND je.duration_to < je2.duration_to) " +
                "left join master_functional_areas mfa on je.master_functional_area_id=mfa.id " +
                "left join master_industries mi on je.master_industry_id=mi.id " +
                "left join master_designations md on je.master_designation_id=mi.id " +
                "where jsl.id= " + js_login_id +
                " group by jsl.id";
        //System.out.println(query);
        List<String> data = databaseOpsAbc.getAllStrings(query, 17);

        int maxyr = 0;
        String profile = "", captiondate = "", profiledate = "", res = "";
        String jsid = "", salary = "", exp = "", ind_id = "", ind = "", desg_id = "", desg = "", other_desg = "", mfa_id = "", func = "", pref_indus = "", pref_func = "", pref_city = "", caption = "", keyskills = "", duration_to = "", duration_from = "", job_profile = "";

        //String enrichSkillQry = "select enriched_skills from abc_resume_skill.process_resumes where js_login_id = " + this.js_login_id ;
        //List<String> skillData = databaseOpsAbcResumeSkill.getAllStrings(enrichSkillQry, 1);


        //get info about an user like current function, previous function, preferred function ,designation ...etc.
        preferredFunction = new LinkedList<Function>();
        List<String> listLocation = new LinkedList<String>();

        for (int i = 0; i < data.size(); i++) {
            jsid = data.get(i);
            i++;

            salary = data.get(i);
            minSalary = (int) Math.floor(Integer.parseInt(salary) * GlobalInstances.Smin);
            maxSalary = (int) Math.ceil(Integer.parseInt(salary) * GlobalInstances.Smax);
            i++;

            exp = data.get(i);
            minExperience = (int) Math.floor(Integer.parseInt(exp) * GlobalInstances.Emin);
            maxExperience = (int) Math.ceil(Integer.parseInt(exp) * GlobalInstances.Emax);
            try {
                maxyr = Integer.parseInt(exp);
            } catch (NumberFormatException e) {
                System.out.println("unable to convert exp in number: " + exp);
            }
            i++;

            ind_id = data.get(i);
            currentIndustry = new Industry(ind_id);
            i++;

            ind = data.get(i);
            i++;

            desg_id = data.get(i);
            currentDesignation = new Designation(desg_id);
            i++;

            desg = data.get(i);
            currentDesignation.setName(desg);
            i++;

            mfa_id = data.get(i);
            currentFunction = new Function(mfa_id);
            i++;

            func = data.get(i);
            i++;

            pref_indus = data.get(i);
            preferredIndustry = new LinkedList<Industry>();
            if(pref_indus != null && !pref_indus.trim().equals("")) {
                String[] pref_ind_array = pref_indus.split(",");
                for (String id : pref_ind_array) {
                    preferredIndustry.add(new Industry(id));
                }
            }
            i++;

            pref_func = data.get(i);
            if(pref_func != null && !pref_func.trim().equals("")) {
                String[] pref_func_array = pref_func.split(",");
                for (String id : pref_func_array) {
                    Function f = new Function(id);
                    preferredFunction.add(f);
                }
            }
            i++;

            pref_city = data.get(i);
            location = new LinkedList<Location>();
            if(pref_city != null && !pref_city.trim().equals("")) {
                String[] city_id_array = pref_city.split(",");
                for (String id : city_id_array) {
                    if (id.trim().equals("-1")) {
                        anyLocation = true;
                        break;
                    }
                    listLocation.add(id);
                }
            }
            i++;

            caption = data.get(i);
            i++;
            keyskills = data.get(i);
            i++;
            duration_to = data.get(i);
            i++;
            duration_from = data.get(i);
            i++;
            job_profile = data.get(i);

            if (job_profile == null) {
                job_profile = "";
            }
            if (desg != null) {
                desg = desg.replaceAll("\\<.*?>", "");
            } else {
                desg = "";
            }
            if (func != null) {
                func = func.replaceAll("\\<.*?>", "");
            } else {
                func = "";
            }
            if (ind != null) {
                ind = ind.replaceAll("\\<.*?>", "");
            } else {
                ind = "";
            }

            profile = makeProfile(caption, keyskills, maxyr);

            if (duration_from != null && !duration_from.isEmpty() && duration_to != null && !duration_to.isEmpty()) {
                res = getProfileDate(duration_from, duration_to);
            }
            // logger.debug("Profile--"+profile+"\nres--"+res+"\njobprofile--"+jobprofile+"\nindustry--"+industry+"\ndesignation--"+designation);
            if (res != null) {
                profile = profile + "\n" + res + "\n" + job_profile.replaceAll("\\<.*?>", "") + "\n" + desg;
            }
            setProfile(profile);
        }

        for (String id : listLocation) {
            String latLngQry = "select lat,lng from master_cities where id=" + id;
            List<String> result = databaseOpsAbc.getAllStrings(latLngQry, 2);
            Location loc = new Location(id);
            if (!result.isEmpty() && result.size() > 0) {

                loc.setLatitude(Double.parseDouble(result.get(0)));
                loc.setLongitude(Double.parseDouble(result.get(1)));
            }
            location.add(loc);
        }

        String prevIndQry = "select master_functional_area_id, master_industry_id " +
                "from js_employments where js_login_id=" + js_login_id +
                " order by duration_to desc limit 1 OFFSET 1";
        List<String> prev_ind_nd_func = databaseOpsAbc.getAllStrings(prevIndQry, 2);
        if (!prev_ind_nd_func.isEmpty() && prev_ind_nd_func.size() > 0) {
            for (int i = 0; i < prev_ind_nd_func.size(); i++) {
                previousFunction = new Function(prev_ind_nd_func.get(i));
                i++;
                previousIndustry = new Industry(prev_ind_nd_func.get(i));
            }
        }

        String resultQualQry = "select group_concat(distinct master_qualifications.name) as qualifications " +
                " from js_educations Inner join master_qualifications " +
                " where js_educations.master_qualification_id = master_qualifications.id AND js_educations.js_login_id = " + js_login_id;
        List<String> resultQualifications = databaseOpsAbc.getAllStrings(resultQualQry, 1);
        if (!resultQualifications.isEmpty() && resultQualifications.size() > 0) {
            String tempQualifications = "";
            for (String s : resultQualifications) {
                tempQualifications = s;
                break;
            }
            if (tempQualifications != null && !tempQualifications.equals("")) {
                qualification = new LinkedList<String>();
                String[] qual_array = tempQualifications.split(",");
                for (String name : qual_array) {
                    qualification.add(name);
                }
            }
        }


    }

    public void setParsedResumeData() {
        this.skills = parsedResumeAndJob.getSkills();
        this.desg = new ArrayList<String>(new HashSet<String>(parsedResumeAndJob.getDesignation()));
        this.inst = new ArrayList<String>(new HashSet<String>(parsedResumeAndJob.getInstitute()));
        this.emailid = new ArrayList<String>(new HashSet<String>(parsedResumeAndJob.getEmailId()));
        this.mobNo = new ArrayList<String>(new HashSet<String>(parsedResumeAndJob.getMobileNumber()));
        this.qual = new ArrayList<String>(new HashSet<String>(parsedResumeAndJob.getQualification()));
        this.idd = parsedResumeAndJob.getId();
        this.rawText = parsedResumeAndJob.getRawText();

        List<String> skillData = new ArrayList<String>();
        int ey = 0, luy = 0;
        int tempy = 0;
        String sk = "";
        StringBuilder flatSkillBuilder = new StringBuilder();

        for (int i = 0; i < skills.size(); i++) {
            JobSeekerSkill jsk = skills.get(i);
            tempy = jsk.getExp() / 12;
            if (jsk.getExp() % 12 >= 6) {
                tempy++;
            }
            ey = tempy;
            tempy = jsk.getLastUsed() / 12;
            if (jsk.getLastUsed() % 12 >= 6) {
                tempy++;
            }
            luy = tempy;
            sk = sk + "," + jsk.getName() + "|" + "E" + ey + "L" + luy;
            flatSkillBuilder.append("," + jsk.getName());
            //skillData.add(jsk.getName() + "|E" + jsk.getExp() + "L" + jsk.getLastUsed());
        }
        this.skill = flatSkillBuilder.toString().replaceFirst(",", "");
        this.enrich_skill = sk.replaceFirst(",", "");
        //Set resume skill value
        this.resumeSkill = new LinkedList<ResumeSkill>();

        //for(String en_sk : enrich_skill){
        //String temp_skills = enrich_skill;
        String[] skillsUnformatted = enrich_skill.split(",");
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
            this.resumeSkill.add(new ResumeSkill(skillName, experienceVal, lastUsedVal));
        }
        setCloudSkills(this.resumeSkill);
    }


    public void dbStorage() {
        boolean exist = databaseOpsAbcResumeSkill.recordExists("select skills from process_resumes where js_login_id = " + this.js_login_id);
        String js_all_resume_id = "select id from js_all_resumes where js_login_id = " + this.js_login_id + " and is_primary=1";
        String jsid = databaseOpsAbc.getString(js_all_resume_id);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String created = dateFormat.format(date);

        String insertQry = "insert into process_resumes (js_all_resumes_id,js_login_id,resume_text,enriched_skills,created,modified,qualification,institute,mobile_number,skills) values (?,?,?,?,?,?,?,?,?,?)";
        String updateQry = "update process_resumes set js_all_resumes_id=?,resume_text=? , enriched_skills=? ,modified =?,qualification=? ,institute=?,mobile_number=?,skills=? where js_login_id=?";
        PreparedStatement insPst = databaseOpsAbcResumeSkill.preparePst(insertQry);
        PreparedStatement upPst = databaseOpsAbcResumeSkill.preparePst(updateQry);
        if (exist)
            databaseOpsAbcResumeSkill.pstUpdate(upPst, new String[]{jsid, rawText, enrich_skill, dateFormat.format(date), StringUtils.join(qual, ","), StringUtils.join(inst, ","), StringUtils.join(mobNo, ","), skill, Integer.toString(this.js_login_id)});
        else
            databaseOpsAbcResumeSkill.pstUpdate(insPst, new String[]{jsid, Integer.toString(this.js_login_id), rawText, enrich_skill, dateFormat.format(date), dateFormat.format(date), StringUtils.join(qual, ","), StringUtils.join(inst, ","), StringUtils.join(mobNo, ","), skill});
        //js_all_resumes_id,js_login_id,resume_text,enriched_skills,created,modified,qualification,institute,mobile_number,skills
        //js_all_resume_id, Integer.toString(id), rawResumeText, skills, dateFormat.format(date), dateFormat.format(date), quals, insts, mobile, flatSkills

    }

    protected void setCloudSkills(List<ResumeSkill> listOfSkills) {

        if (listOfSkills == null || listOfSkills.size() == 0) {
            System.out.println("Empty List is passed to String");
            return;
        }

        String skillsSpaceSeperated = null;
        for (ResumeSkill temp : listOfSkills) {
            if (skillsSpaceSeperated == null)
                skillsSpaceSeperated = "\"" + temp.getName() + "\"";
            skillsSpaceSeperated = skillsSpaceSeperated + "  " + "\"" + temp.getName() + "\"";
        }

        String tempStr = "skills:(" + skillsSpaceSeperated + ") OR l0_skills_separated:(" + skillsSpaceSeperated + ")";
        parameters.set("q", tempStr);
        parameters.setFields("l0_skills_separated", "skills");
        parameters.set("rows", 10);

        QueryResponse response = null;
        try {
            response = solr.query(parameters);
        } catch (SolrServerException e) {
            System.err.println("alllevel solr param---\n" + parameters.toString());
            e.printStackTrace();
        }
        SolrDocumentList results = response.getResults();

        String skills = "";
        String l0_skills = "";

        cloudSkill = new LinkedList<ResumeSkill>();

        for (SolrDocument document : results) {
            try {
                try {
                    skills = document.getFieldValue("skills").toString();
                } catch (Exception e) {

                }
                try {
                    l0_skills = document.getFieldValue("l0_skills_separated").toString();
                } catch (Exception e) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (skills != null) {
                if (skills != null && skills != "") {
                    skills = skills.replace('[', ' ');
                    skills = skills.replace(']', ' ');
                    String[] tempSkillsArr = skills.split(",");
                    for (String s : tempSkillsArr) {
                        s = s.trim();
                        if (s != null && s != "")
                            this.cloudSkill.add(new ResumeSkill(s, 0, 0));
                    }
                }
            }

            if (l0_skills != null) {
                if (l0_skills != null && l0_skills != "") {
                    l0_skills = l0_skills.replace('[', ' ');
                    l0_skills = l0_skills.replace(']', ' ');
                    String[] tempSkillsArr = l0_skills.split(",");
                    for (String s : tempSkillsArr) {
                        s = s.trim();
                        if (s != null && s != "")
                            this.cloudSkill.add(new ResumeSkill(s, 0, 0));
                    }
                }
            }
        }
        parameters.clear();
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

    public void setParsedResumeAndJob(ParsedResumeAndJob parsedResumeAndJob) {
        this.parsedResumeAndJob = parsedResumeAndJob;
    }

    private void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProfile() {
        return this.profile;
    }

    private String makeProfile(String caption, String keyskills, int maxyr) {
        String profile = "";
        if (caption == null && keyskills == null) {
        } else {
            if (caption != null && keyskills != null) {
                profile = getCaptionDate(maxyr) + "\n" + caption + "\n" + keyskills;
            } else {
                if (caption != null) {
                    profile = getCaptionDate(maxyr) + "\n" + caption;
                }
                if (keyskills != null) {
                    profile = getCaptionDate(maxyr) + "\n" + keyskills;
                }
            }
        }
        return profile;
    }


    /**
     * duration (since max experience to till now) format dd-mm-yyyy till now
     *
     * @param year maximum experience
     * @return duartion for caption and keyskills
     */
    private String getCaptionDate(int year) {
        String captiondate = "";
        // String query="SELECT curdate(),curdate() - interval " + year +
        // " year from dual";
        String query = "SELECT date_format(curdate() - interval " + "1" + " year , '%d-%m-%Y') from dual";
        List<String> result = databaseOpsAbc.getAllStrings(query, 1);
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
     * @param cur  duration_to
     * @return duration of fro each job profile
     */
    public String getProfileDate(String prev, String cur) {
        String profiledate = "";
        try {
            int curr = 0, org = 0;
            String prevdate = null, curdate = null, pryr = null;
            // System.out.println("prev "+prev+"  cur  "+cur);
            String query1 = "select year(\"" + prev + "\"" + "), year(\"" + cur + "\"" + "), year(curdate()),date_format(\"" + prev + "\",'%d-%m-%Y'),date_format(\"" + cur + "\",'%d-%m-%Y') from dual";
            List<String> result1 = databaseOpsAbc.getAllStrings(query1, 5);
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

    public void close(){
        databaseOpsAbc.close();
        databaseOpsAbcResumeSkill.close();
    }
}
