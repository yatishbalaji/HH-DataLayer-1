package com.headhonchos.paresdResume;

import com.headhonchos.skill.JobSeekerSkill;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishu on 16/4/14.
 */
public class ParsedResumeAndJobTest {
    @org.junit.Test
    public void testMerge() throws Exception {
        ParsedResumeAndJob pr = new ParsedResumeAndJob("34");
        List<JobSeekerSkill> prList = new ArrayList<JobSeekerSkill>();
        prList.add(new JobSeekerSkill("java",3,3));
        prList.add(new JobSeekerSkill("php",5,6));
        pr.setSkills(prList);

        ParsedResumeAndJob pf = new ParsedResumeAndJob("34");
        List<JobSeekerSkill> pfList = new ArrayList<JobSeekerSkill>();
        pfList.add(new JobSeekerSkill("java",4,5));
        pfList.add(new JobSeekerSkill("php",2,1));
        pf.setSkills(pfList);

        pr.merge(pf);

        for(JobSeekerSkill rs:pr.getSkills()){
            System.out.println(rs);
        }
    }

    @Test
    public void testConstructor() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("/home/ishu/Desktop/output.txt")));
        String line = null;
        StringBuilder sb= new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }
    }
}
