package com.headhonchos.jobSeeker;

import com.headhonchos.jobPosting.Function;
import com.headhonchos.jobPosting.Industry;
import com.headhonchos.jobPosting.Location;
import com.headhonchos.jobPosting.Qualification;
import com.headhonchos.paresdResume.Designation;
import com.headhonchos.skill.ResumeSkill;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class JSProfileData3Test {

    private static JSProfileData3 jsProfileData3;

    @BeforeClass
    public static void setJobSeekerData(){
        jsProfileData3 = new JSProfileData3(1179585);
    }

    @Test
    public void testGetJs_login_id() throws Exception {
        int js_login_id = jsProfileData3.getJs_login_id();
        System.out.println("js_login_id - "+js_login_id);
    }

    @Test
    public void testGetResumeSkill() throws Exception {
        List<ResumeSkill> resumeSkill = jsProfileData3.getResumeSkill();
        System.out.println("resume skills - "+resumeSkill);
    }

    @Test
    public void testGetCurrentFunction() throws Exception {
        Function currentFunction = jsProfileData3.getCurrentFunction();
        System.out.println("Current Function - "+currentFunction);
    }

    @Test
    public void testGetPreviousFunction() throws Exception {
        Function previousFunction = jsProfileData3.getPreviousFunction();
        System.out.println("Previous Function - "+previousFunction);
    }

    @Test
    public void testGetPreferredFunction() throws Exception {
        List<Function> preferredFunction = jsProfileData3.getPreferredFunction();
        System.out.println("preferred Function - "+preferredFunction);
    }

    @Test
    public void testGetCurrentIndustry() throws Exception {
        Industry currentIndustry = jsProfileData3.getCurrentIndustry();
        System.out.println("currentIndustry - "+currentIndustry);
    }

    @Test
    public void testGetPreviousIndustry() throws Exception {
        Industry previousIndustry = jsProfileData3.getPreviousIndustry();
        System.out.println("previous Industry - "+previousIndustry);
    }

    @Test
    public void testGetPreferredIndustry() throws Exception {
        List<Industry> preferredIndustry = jsProfileData3.getPreferredIndustry();
        System.out.println("preferred Industry - "+preferredIndustry);
    }

    @Test
    public void testGetCurrentDesignation() throws Exception {
        Designation currentDesignation = jsProfileData3.getCurrentDesignation();
        System.out.println("current Designation - "+currentDesignation);
    }

    @Test
    public void testGetLocation() throws Exception {
        List<Location> location = jsProfileData3.getLocation();
        System.out.println("location - "+location);
    }

    @Test
    public void testGetExperience() throws Exception {
        int experience = jsProfileData3.getExperience();
        System.out.println("experience - "+experience);
    }

    @Test
    public void testGetSalary() throws Exception {
        int salary = jsProfileData3.getSalary();
        System.out.println("salary - "+salary);
    }

    @Test
    public void testIsForeigner() throws Exception {
        boolean foreigner = jsProfileData3.isForeigner();
        System.out.println("From foreign - "+foreigner);
    }

    @Test
    public void testIsAnyLocation() throws Exception {
        boolean isAnyLocation = jsProfileData3.isAnyLocation();
        System.out.println("isANyLocation - "+isAnyLocation);
    }

    @Test
    public void testGetQualification() throws Exception {
        List<Qualification> qualification = jsProfileData3.getQualification();
        System.out.println("qualification - "+qualification);
    }
}