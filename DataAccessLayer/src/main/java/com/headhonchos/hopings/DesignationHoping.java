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
public class DesignationHoping{

    private static Map<String,Integer> desgTotalHopCount = new HashMap<String, Integer>();
    private static Map<String, Map<String,Integer>> desgToDesgHopMap = new HashMap<String, Map<String, Integer>>();

    static {
        Connection readOnlyDatabaseConnection;
        readOnlyDatabaseConnection = ConnectionManager.getConnection("abc_resume_skill");
        if(readOnlyDatabaseConnection != null) {
            try {
//                String hopQuery = "select hop_designation_id,hop_count from js_designations_hop where master_designation_id = "+current;
                String hopQuery = "select * from js_designations_hop";
                PreparedStatement selectQuery = readOnlyDatabaseConnection.prepareStatement(hopQuery);
                ResultSet resultSet = selectQuery.executeQuery();
                while (resultSet.next()) {
                    String currentDesignationId = resultSet.getString("master_designation_id");
                    String nextDesignation = resultSet.getString("hop_designation_id");
                    int count = resultSet.getInt("hop_count");
                    //put hopings count
                    Map<String, Integer> nextHopingsMap = desgToDesgHopMap.get(currentDesignationId);
                    if(nextHopingsMap!=null){
                        nextHopingsMap.put(nextDesignation,count);
                    }
                    else{
                        nextHopingsMap = new HashMap<String, Integer>();
                        nextHopingsMap.put(nextDesignation,count);
                    }
                    //Put total counts
                    Integer prevTotalCount = desgTotalHopCount.get(currentDesignationId);
                    if(prevTotalCount == null){
                        desgTotalHopCount.put(currentDesignationId, count);
                    }
                    else{
                        desgTotalHopCount.put(currentDesignationId, count + prevTotalCount);
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

    public static Map<String, Integer> getDesgTotalHopCount() {
        return desgTotalHopCount;
    }

    public static Map<String, Map<String, Integer>> getDesgToDesgHopMap() {
        return desgToDesgHopMap;
    }
}
