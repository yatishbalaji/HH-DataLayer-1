package com.headhonchos.hopings;

import com.headhonchos.DBConnectionManager.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ishu on 13/8/14.
 */
public class SkillExperiences {
    private static final Map<String, Integer> skillExperienceMap = new HashMap<String, Integer>();

    static {
        Connection readOnlyDatabaseConnection;
        readOnlyDatabaseConnection = ConnectionManager.getConnection("abc_resume_skill");
        if(readOnlyDatabaseConnection != null) {
            try {
//                String hopQuery = "select hop_industry_id,hop_count from js_industries_hop where master_industry_id = "+current;
                String hopQuery = "select * from skill_experiences";
                PreparedStatement selectQuery = readOnlyDatabaseConnection.prepareStatement(hopQuery);
                ResultSet resultSet = selectQuery.executeQuery();
                while (resultSet.next()) {
                    String skillName = resultSet.getString("name");
                    int experience = resultSet.getInt("experience");
                    skillExperienceMap.put(skillName, experience);
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

    public static Map<String,Integer> getSkillExperienceMap(){
        return skillExperienceMap;
    }
}
