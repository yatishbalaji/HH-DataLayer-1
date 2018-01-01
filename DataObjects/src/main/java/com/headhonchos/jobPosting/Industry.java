package com.headhonchos.jobPosting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishu on 28/3/14.
 */
public class Industry {
    String id;
    String name;

    private static Map<String,String> industryMap;
    static {

        industryMap = new HashMap<String, String>();
        InputStream industryAsStream = Industry.class.getResourceAsStream("/master_industries.csv");

        //get industry map
        String line;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(industryAsStream));
        try {
            while((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\;");
                industryMap.put(tokens[0],tokens[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        industryMap.put("0","Other");
    }
    public Industry(String id) {
        this.id = id;
        this.name = industryMap.get(id);
    }

    public Industry(String id,String name){
        this.id = id;
        this.name = name;
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
        return "Industry{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
