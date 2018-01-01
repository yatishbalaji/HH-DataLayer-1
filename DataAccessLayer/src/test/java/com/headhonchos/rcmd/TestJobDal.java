package com.headhonchos.rcmd;

import com.headhonchos.GlobalInstances;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by amanpoonia on 2/4/14.
 */
public class TestJobDal {


    @BeforeClass
    public static void initConstant() {
        Properties props = new Properties();
        try {
            props.load(new FileReader("/opt/java/resume_parser/java_module/GlobalFile.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlobalInstances.SLAVE_ABC = props.getProperty("SLAVE_ABC");
        GlobalInstances.SLAVE_ABC_RESUME_SKILL = props.getProperty("SLAVE_ABC_RESUME_SKILL");
        GlobalInstances.SLAVE_ABC_USER = props.getProperty("SLAVE_ABC_USER");
        GlobalInstances.SLAVE_ABC_PASSWORD = props.getProperty("SLAVE_ABC_PASSWORD");
        GlobalInstances.DRIVER = props.getProperty("DRIVER");
    }

    @Test
    public void testGetJob() throws Exception {

        //{annual_salary_min=[21], work_experience_min=[10], master_functional_area_id=[5,3],
        // keywords=[java], recommendedSkills=[], master_location_id=[1611,9,10],
        // work_experience_max=[21], final_skills=[], master_industry_id=[4,10],
        // annual_salary_max=[70], master_country_id=[11]}


        Map<String,List<String>> data = new HashMap<String, List<String>>();

        data.put("annual_salary_min", new ArrayList<String>() {{
            add("21");
        }});
        data.put("annual_salary_max", new ArrayList<String>() {{
            add("70");
        }});
        data.put("work_experience_min", new ArrayList<String>() {{
            add("10");
        }});
        data.put("work_experience_max", new ArrayList<String>() {{
            add("20");
        }});
        data.put("master_functional_area_id", new ArrayList<String>() {{
            add("5,3");
        }});
        data.put("master_industry_id", new ArrayList<String>() {{
            add("4,10");
        }});
        data.put("keywords", new ArrayList<String>() {{
            add("java");
        }});
        data.put("final_skills", new ArrayList<String>() {{
            add("S^jvaa^");
        }});
        data.put("master_location_id", new ArrayList<String>() {{
            add("1611,9,10");
        }});//master_location_id=[]
        JobDal jobDal = new JobDal();
        jobDal.getJob(data);

    }

}
