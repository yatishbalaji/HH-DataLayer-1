package com.headhonchos.jobPosting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishu on 21/8/14.
 */
public class Qualification {

    String id;
    String name;
    private static Map<String,String> qualificationMap;
    static {
        qualificationMap = new HashMap<String, String>();
        InputStream qualAsStream = Function.class.getResourceAsStream("/master_qualification.csv");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(qualAsStream));
        String line;
        try {
            //get qualification map
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\;");
                String qualId = tokens[0];
                String qualName = tokens[1];
                qualificationMap.put(qualId, qualName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        qualificationMap.put("0", "Other");
    }

    public Qualification(String id) {
        this.id  = id;
        String qualificationName = qualificationMap.get(id);
        if(qualificationName == null || qualificationName.equalsIgnoreCase("null")){
            qualificationName="Other";
        }
        this.name = qualificationName;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Qualification{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
