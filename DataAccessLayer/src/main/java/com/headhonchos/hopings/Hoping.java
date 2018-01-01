package com.headhonchos.hopings;

import com.headhonchos.jobPosting.Function;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by ishu on 22/5/14.
 */
public interface Hoping {
    public Map<Integer,Double> getNextJumpWeight(int current) throws SQLException;
    public double getNextJumpWeight(String current, String next);
}
