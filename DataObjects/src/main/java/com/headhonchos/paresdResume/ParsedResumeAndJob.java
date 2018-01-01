package com.headhonchos.paresdResume;

import com.headhonchos.skill.JobSeekerSkill;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishu on 16/4/14.
 */
public class ParsedResumeAndJob {
    private static final Logger logger = LoggerFactory.getLogger(ParsedResumeAndJob.class);

    private String id;
    private String rawText;
    private List<String> qualification = new ArrayList<String>();
    private List<String> institute = new ArrayList<String>();
    private List<JobSeekerSkill> skills = new ArrayList<JobSeekerSkill>();
    private List<JobSeekerSkill> cloud_skills = new ArrayList<JobSeekerSkill>();
    private List<String> mobileNumber = new ArrayList<String>();
    private List<String> email = new ArrayList<String>();
    private List<String> designation = new ArrayList<String>();


    public ParsedResumeAndJob(String id) {
        this.id=id;
    }

    public ParsedResumeAndJob(String id, JSONObject data) {
        //this.id=id;
        //System.out.println("gate parsed data--- " + data + "\n--------------");
        setId(id);
        JSONArray educationArray = null;
        JSONArray mobileNumberArray = null;
        JSONArray skillsArray = null;
        JSONArray cloudSkillsArray = null;
        JSONArray emailArray = null;
        JSONArray desgArray = null;
        JSONArray jobKeywordsArray = null;
        JSONArray jobDescArray = null;
        JSONArray jobTitleArray = null;
        System.out.println("data - "+data.toJSONString());
        educationArray = (JSONArray) data.get("EDUCATION");
        mobileNumberArray = (JSONArray) data.get("PHONE");
        skillsArray = (JSONArray) data.get("SKILLS");
        cloudSkillsArray = (JSONArray) data.get("CLOUD_SKILLS");
        emailArray = (JSONArray) data.get("EmailId");
        desgArray = (JSONArray) data.get("GDesg");

        if(mobileNumberArray!=null){
            for(Object o:mobileNumberArray){
                JSONObject o1 = (JSONObject) o;
                String code = "";
                try{
                    code = (String) o1.get("country_code");
                }catch(Exception e){

                }
                String number = "";
                if(code!=null)
                    number = code + " " + (String) o1.get("number");
                else
                    number = (String) o1.get("number");
                mobileNumber.add(number.trim());
                //logger.info("number - "+number);
            }
            setMobileNumber(mobileNumber);
        }

        if(skillsArray!=null){
            for(Object o:skillsArray){
                JSONObject o1 = (JSONObject) o;
                String skill = (String) o1.get("skill");
                int lastUsed = Integer.parseInt((String) o1.get("lastused"));
                int exp = Integer.parseInt((String) o1.get("exp"));
                JobSeekerSkill jobSeekerSkill = new JobSeekerSkill(skill.toLowerCase(), exp, lastUsed);
                skills.add(jobSeekerSkill);
            }
            setSkills(skills);
        }

        if(cloudSkillsArray!=null){
            for(Object o:cloudSkillsArray){
                JSONObject o1 = (JSONObject) o;
                String skill = (String) o1.get("cloud_skill");
                int lastUsed = Integer.parseInt((String) o1.get("lastused"));
                int exp = Integer.parseInt((String) o1.get("exp"));
                JobSeekerSkill jobSeekerSkill = new JobSeekerSkill(skill.toLowerCase(), exp, lastUsed);
                cloud_skills.add(jobSeekerSkill);
            }
            setCloudSkills(cloud_skills);
        }

        if(educationArray!=null){
            for(Object o:educationArray){
                JSONObject o1 = (JSONObject) o;
                String education = (String) o1.get("EDUCATION");
                qualification.add(education);
                String inst = (String)o1.get("INSTITUTE");
                institute.add(inst);
            }
            setInstitute(institute);
            setQualification(qualification);
        }

        if(emailArray!=null){
            for(Object o:emailArray){
                JSONObject o1 = (JSONObject) o;
                String emailid = (String) o1.get("Id");
                email.add(emailid);
            }
            setEmailId(email);
        }

        if(desgArray!=null){
            for(Object o:desgArray){
                JSONObject o1 = (JSONObject) o;
                String desg = (String) o1.get("desg");
                designation.add(desg);
            }
            setDesignation(designation);
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public List<String> getQualification() {
        return qualification;
    }

    public void setQualification(List<String> qualification) {
        this.qualification = qualification;
    }

    public List<String> getEmailId() {
        return email;
    }

    public void setEmailId(List<String> email) {
        this.email = email;
    }

    public List<String> getDesignation() {
        return designation;
    }

    public void setDesignation(List<String> designation) {
        this.designation = designation;
    }

    public List<String> getInstitute() {
        return institute;
    }

    public void setInstitute(List<String> institute) {
        this.institute = institute;
    }

    public List<JobSeekerSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<JobSeekerSkill> skills) {
        this.skills = skills;
    }

    public List<JobSeekerSkill> getCloudSkills() {
        return cloud_skills;
    }

    public void setCloudSkills(List<JobSeekerSkill> cloud_skills) {
        this.cloud_skills = cloud_skills;
    }

    public List<String> getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(List<String> mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void merge(ParsedResumeAndJob other){
            List<JobSeekerSkill> otherSkills = other.getSkills();
        if(otherSkills.isEmpty()){
            return;
        }
        for (JobSeekerSkill otherSkill : otherSkills) {
            if (skills.contains(otherSkill)) {
                JobSeekerSkill resumeSkill = skills.get(skills.indexOf(otherSkill));
                if (otherSkill.getExp() > resumeSkill.getExp()) {
                    resumeSkill.setExp(otherSkill.getExp());
                }
                if (otherSkill.getLastUsed() < resumeSkill.getLastUsed()) {
                    resumeSkill.setLastUsed(otherSkill.getLastUsed());
                }
            } else {
                skills.add(otherSkill);
            }
        }
    }

    public boolean isEmpty(){
        if(qualification.isEmpty() && skills.isEmpty() && mobileNumber.isEmpty() && institute.isEmpty() && email.isEmpty()){
            return true;
        }
        return false;
    }
}
