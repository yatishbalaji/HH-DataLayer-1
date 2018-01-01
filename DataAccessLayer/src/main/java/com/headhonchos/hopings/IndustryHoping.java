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
public class IndustryHoping{

    private static Map<String,Integer> indTotalHopCount = new HashMap<String, Integer>();
    private static Map<String, Integer> indToIndHopMap = new HashMap<String, Integer>();

    static {
        Connection readOnlyDatabaseConnection;
        readOnlyDatabaseConnection = ConnectionManager.getConnection("abc_resume_skill");
        if(readOnlyDatabaseConnection != null) {
            try {
//                String hopQuery = "select hop_industry_id,hop_count from js_industries_hop where master_industry_id = "+current;
                String hopQuery = "select * from js_industries_hop";
                PreparedStatement selectQuery = readOnlyDatabaseConnection.prepareStatement(hopQuery);
                ResultSet resultSet = selectQuery.executeQuery();
                while (resultSet.next()) {
                    String currentIndustryId = resultSet.getString("master_industry_id");
                    int nextIndustryId = resultSet.getInt("hop_industry_id");
                    int hopCount = resultSet.getInt("hop_count");
                    indToIndHopMap.put(currentIndustryId + ":" + nextIndustryId, hopCount);
                    Integer prevTotalCount = indTotalHopCount.get(currentIndustryId);
                    if(prevTotalCount == null){
                        indTotalHopCount.put(currentIndustryId,hopCount);
                    }
                    else{
                        indTotalHopCount.put(currentIndustryId,hopCount+prevTotalCount);
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

    public static Map<String, Integer> getIndTotalHopCount() {
        return indTotalHopCount;
    }

    public static Map<String, Integer> getIndToIndHopMap() {
        return indToIndHopMap;
    }
}
