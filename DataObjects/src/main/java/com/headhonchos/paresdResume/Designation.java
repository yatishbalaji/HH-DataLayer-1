package com.headhonchos.paresdResume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishu on 21/5/14.
 */
public class Designation {

    private String id;
    private String name;
    private static Map<String,String> designationMap;

    static {
        designationMap = new HashMap<String, String>();
        InputStream functionAsStream = Designation.class.getResourceAsStream("/master_designations.csv");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(functionAsStream));
        String line = null;
        try {
            //get function map
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\;");
                String  desgID = tokens[0];
                String desgName = tokens[1];
                designationMap.put(desgID, desgName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        designationMap.put("0","Others");
    }
    public Designation(String id) {
        this.id = id;
        this.name =designationMap.get(id);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Designation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
