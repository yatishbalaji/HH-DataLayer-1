package com.headhonchos.parsedJob;

import java.util.Map;

/**
 * Created by ishu on 14/3/14.
 */

public class LTTTT2ParsedJob {
    private LTTTT2Skills LTTTT2Skills;
    private Map<String, Integer> skillsFrequencyMap;

    public LTTTT2Skills getLTTTT2Skills() {
        return LTTTT2Skills;
    }

    public void setLTTTT2Skills(LTTTT2Skills LTTTT2Skills) {
        this.LTTTT2Skills = LTTTT2Skills;
    }

    public void setSkillsFrequencyMap(Map<String, Integer> skillsFrequencyMap) {
        this.skillsFrequencyMap = skillsFrequencyMap;
    }

    public Map<String, Integer> getSkillsFrequencyMap() {
        return skillsFrequencyMap;
    }
}
