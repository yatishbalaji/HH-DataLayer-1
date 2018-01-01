package com.headhonchos.jobSeeker;

import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.paresdResume.Designation;
import com.headhonchos.skill.ResumeSkill;
import junit.framework.TestCase;

public class JSProfileDataTest extends TestCase {


    public void testObjectCreation() throws Exception {
        JSProfileData obj = new JSProfileData(8);
    }

    public void testGetCloudSkills () {
        JSProfileData obj = new JSProfileData(8);
        System.out.println("Cloud skill: ");
        for (ResumeSkill resumeSkill : obj.getCloudSkill()) {
            System.out.println(resumeSkill.getName());
        }


    }
    public void testGetJSLoginId() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Login Id: "+obj.getJSLoginId());

    }

    public void testGetResumeSkill() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("resume skill: ");
        for (ResumeSkill resumeSkill : obj.getResumeSkill()) {
            System.out.println(resumeSkill.getName());
        }
    }

    public void testGetCloudSkill() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("cloud skill: ");
        for (ResumeSkill resumeSkill : obj.getCloudSkill()) {
            System.out.println(resumeSkill.getName());
        }

    }

    public void testGetCurrentFunction() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("curr function: "+obj.getCurrentFunction().getName());
    }

    public void testGetPreviousFunction() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("prev func: "+obj.getPreviousFunction().getName());
    }

    public void testGetPreferredFunction() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("preferred function: ");
        for (Function function : obj.getPreferredFunction()) {
            System.out.println(function.getName());
        }
    }

    public void testGetCurrentIndustry() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("current ind: "+obj.getCurrentIndustry().getName());
    }

    public void testGetPreviousIndustry() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("previous industry: "+obj.getPreviousIndustry().getName());
    }

    public void testGetPreferredIndustry() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Preferred Industry: ");
        for (Industry industry : obj.getPreferredIndustry()) {
            System.out.println(industry.getName());
        }

    }

    public void testGetCurrentDesignation() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("current designation: "+obj.getCurrentDesignation().getName());
    }

    public void testGetPreferredDesignation() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Preferred designation: ");
        for (Designation designation : obj.getPreferredDesignation()) {
            System.out.println(designation.getId());
            System.out.println(designation.getName());
        }
    }

    public void testGetLocation() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Location: ");
        for (Location location : obj.getLocation()) {
            System.out.println(location.getId());
            //System.out.println(location.getCityName());
            //System.out.println(location.getCountryId());
            System.out.println("lat: "+location.getLatitude());
            System.out.println("lng: "+location.getLongitude());
        }

        System.out.println();
    }

    public void testGetMinExperience() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Min exp: "+obj.getMinExperience());
    }

    public void testGetMaxExperience() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Max exp: "+obj.getMaxExperience());
    }

    public void testGetMinSalary() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Min Salary: "+obj.getMinSalary());
    }

    public void testGetMaxSalary() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("max salary: "+obj.getMaxSalary());
    }

    public void testIsForeigner() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Is foreigner: "+obj.isForeigner());
    }

    public void testIsAnyLocation() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("Any location "+obj.isAnyLocation());
    }

    public void testGetQualification() throws Exception {
        JSProfileData obj = new JSProfileData(7);
        System.out.println("qualification : ");
        for (String s : obj.getQualification()) {
            System.out.println(s);
        }
    }

    public void testConst(){
        JSProfileData profileData = new JSProfileData(8);
    }
}