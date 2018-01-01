package com.headhonchos.jobSeeker;

import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.paresdResume.Designation;
import com.headhonchos.skill.ResumeSkill;

import java.util.List;

/**
 * Created by ishu on 21/5/14.
 */
public interface JSProfile {

    public int getJSLoginId();
    public List<ResumeSkill> getResumeSkill();
    public List<ResumeSkill> getCloudSkill();

    public Function getCurrentFunction();
    public Function getPreviousFunction();
    public List<Function> getPreferredFunction();

    public Industry getCurrentIndustry();
    public Industry getPreviousIndustry();
    public List<Industry> getPreferredIndustry();

    public Designation getCurrentDesignation();
    public List<Designation> getPreferredDesignation();

    public List<Location> getLocation();
    public int getMinExperience();
    public int getMaxExperience();

    public int getMinSalary();
    public int getMaxSalary();

    public boolean isForeigner();
    public boolean isAnyLocation();

    public List<String> getQualification();

}
