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
public class Function {

    String id;
    String name;
    private static Map<String,String> functionMap;
    static {
        functionMap = new HashMap<String, String>();
        InputStream functionAsStream = Function.class.getResourceAsStream("/master_functional_areas.csv");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(functionAsStream));
        String line = null;
        try {
            //get function map
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\;");
                String functionId = tokens[0];
                String functionName = tokens[1];
                functionMap.put(functionId, functionName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        functionMap.put("0","Other");
    }

    public Function(String id) {
        this.id  = id;
        this.name = functionMap.get(id);
    }

    public Function(String id,String name){
        this.id = id;
        this.name =name;
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
        return "Function{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
