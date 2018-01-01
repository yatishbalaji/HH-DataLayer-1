package com.headhonchos.jobPosting;

import com.headhonchos.skill.EnrichedSkill;
import java.util.List;


/**
 * Created by ishu on 14/3/14.
 */
public class Job {
    private int id;
    private List<Function> masterFunctionalAreas;
    private List<Industry> masterIndustries;
    private int minSalary;
    private int maxSalary;
    private int minExperience;
    private int maxExperience;
    private List<Location> locations;
    private boolean locationPreference;
    private List<EnrichedSkill> systemskills;
    private List<EnrichedSkill> manualskills;
    private boolean isJobQuery = true;

    //location Preferrence
    public boolean isLocationPreference() {
        return locationPreference;
    }
    public void setLocationPreference(boolean locationPreference) {
        this.locationPreference = locationPreference;
    }

    //Function
    public List<Function> getMasterFunctionalAreas() {
        return masterFunctionalAreas;
    }
    public void setMasterFunctionalAreas(List<Function> masterFunctionalAreaId) {
        this.masterFunctionalAreas = masterFunctionalAreaId;
    }
    //Skills
    public List<EnrichedSkill> getSystemSkills() {
        return systemskills;
    }
    public void setSystemSkills(List<EnrichedSkill> systemskills) {
        this.systemskills = systemskills;
    }
    public List<EnrichedSkill> getManualskills() {
        return manualskills;
    }
    public void setManualskills(List<EnrichedSkill> manualskills) {
        this.manualskills = manualskills;
    }
    //Industry
    public List<Industry> getMasterIndustries() {
        return masterIndustries;
    }
    public void setMasterIndustries(List<Industry> masterIndustries) {
        this.masterIndustries = masterIndustries;
    }

    //min Salary
    public int getMinSalary() {
        return minSalary;
    }
    public void setMinSalary(int minSalary) {
        this.minSalary = minSalary;
    }

    //Max Salary
    public int getMaxSalary() {
        return maxSalary;
    }
    public void setMaxSalary(int maxSalary) {
        this.maxSalary = maxSalary;
    }

    //minExp
    public int getMinExperience() {
        return minExperience;
    }
    public void setMinExperience(int minExperience) {
        this.minExperience = minExperience;
    }

    //maxExp
    public int getMaxExperience() {
        return maxExperience;
    }
    public void setMaxExperience(int maxExperience) {
        this.maxExperience = maxExperience;
    }

    //Location
    public List<Location> getLocations() {
        return locations;
    }
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    //id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public boolean isJobQuery() {
        return isJobQuery;
    }

    public void setJobQuery(boolean jobQuery) {
        isJobQuery = jobQuery;
    }

}
