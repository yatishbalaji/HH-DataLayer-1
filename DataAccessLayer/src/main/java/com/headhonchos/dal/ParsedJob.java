package com.headhonchos.dal;

import com.headhonchos.jobPosting.JSkill;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishu on 27/8/14.
 */
public class ParsedJob {

    private static final Logger logger = LoggerFactory.getLogger(ParsedJob.class);

    List<JSkill> jobKeywordSkills = new ArrayList<JSkill>();
    List<JSkill> jobDescSkills = new ArrayList<JSkill>();
    List<JSkill> jobTitleSkills = new ArrayList<JSkill>();

    public ParsedJob(JSONObject data){

        JSONObject extractedObject = (JSONObject) data.values().iterator().next();
        JSONArray jobTitleArray = (JSONArray) extractedObject.get("title_skills");
        JSONArray jobDescArray = (JSONArray) extractedObject.get("desc_skills");
        JSONArray jobKeywordsArray = (JSONArray) extractedObject.get("keyword_skills");

        if(jobTitleArray != null){
            List<JSkill> skills=new ArrayList<JSkill>();
            int exp=0;
            for(Object o:jobTitleArray){
                JSONObject o1 = (JSONObject) o;
                String name = (String) o1.get("string");
                String expS = (String) o1.get("exp");
                if(expS!=null){
                    exp=Integer.parseInt(expS);
                }
                skills.add(new JSkill(name.toLowerCase(), exp, 0.0));
            }
            setJobTitleSkills(skills);
        }

        if(jobDescArray != null){
            List<JSkill> skills=new ArrayList<JSkill>();
            int exp=0;
            for(Object o:jobDescArray){
                JSONObject o1 = (JSONObject) o;
                String name = (String) o1.get("string");
                String expS = (String) o1.get("exp");
                if(expS!=null){
                    exp=Integer.parseInt(expS);
                }
                skills.add(new JSkill(name.toLowerCase(), exp, 0.0));
            }
            setJobDescSkills(skills);
        }

        if(jobKeywordsArray != null){
            List<JSkill> skills=new ArrayList<JSkill>();
            int exp=0;
            for(Object o:jobKeywordsArray){
                JSONObject o1 = (JSONObject) o;
                String name = (String) o1.get("string");
                String expS = (String) o1.get("exp");
                if(expS!=null){
                    exp=Integer.parseInt(expS);
                }
                skills.add(new JSkill(name.toLowerCase(), exp, 0.0));
            }
            setJobKeywordSkills(skills);
        }

        logger.debug("extracted Desc Skills : {}",jobDescSkills);
        logger.debug("extracted Keyword Skills : {}",jobKeywordSkills);
        logger.debug("extracted Title Skills: {}",jobTitleSkills);
    }

    public List<JSkill> getKeywordSkills() {
        return jobKeywordSkills;
    }

    public void setJobKeywordSkills(List<JSkill> jobKeywordSkills) {
        this.jobKeywordSkills = jobKeywordSkills;
    }

    public void setJobDescSkills(List<JSkill> jobDescSkills) {
        this.jobDescSkills = jobDescSkills;
    }

    public void setJobTitleSkills(List<JSkill> jobTitleSkills) {
        this.jobTitleSkills = jobTitleSkills;
    }

    public List<JSkill> getDescSkills() {
        return jobDescSkills;
    }

    public List<JSkill> getTitleSkills() {
        return jobTitleSkills;
    }
}
