package com.headhonchos.hopings;

import com.headhonchos.DBConnectionManager.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aman on 23/5/14.
 */
public class FunctionHoping {

    private static Map<String, Integer> funToFunHopMap = new HashMap<String, Integer>();
    private static Map<String,Integer> funTotalHopCount = new HashMap<String, Integer>();

    static {
        Connection readOnlyDatabaseConnection;
        readOnlyDatabaseConnection = ConnectionManager.getConnection("abc_resume_skill");
        if(readOnlyDatabaseConnection != null) {
            try {
//                String hopQuery = "select hop_function_id,hop_count from js_functional_areas_hop where master_functional_area_id = "+current;
                String hopQuery = "select * from js_functional_areas_hop";
                PreparedStatement selectQuery = readOnlyDatabaseConnection.prepareStatement(hopQuery);
                ResultSet resultSet = selectQuery.executeQuery();
                while (resultSet.next()) {
                    String currentFunctionId = resultSet.getString("master_functional_area_id");
                    String nextFunctionId = resultSet.getString("hop_function_id");
                    int hopCount = resultSet.getInt("hop_count");
                    funToFunHopMap.put(currentFunctionId + ":" + nextFunctionId, hopCount);
                    Integer prevTotalCount = funTotalHopCount.get(currentFunctionId);
                    if(prevTotalCount == null){
                        funTotalHopCount.put(currentFunctionId,hopCount);
                    }
                    else{
                        funTotalHopCount.put(currentFunctionId,hopCount+prevTotalCount);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    readOnlyDatabaseConnection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<String, Integer> getFunTotalHopCount() {
        return funTotalHopCount;
    }

    public static Map<String, Integer> getFunToFunHopMap() {
        return funToFunHopMap;
    }
}
