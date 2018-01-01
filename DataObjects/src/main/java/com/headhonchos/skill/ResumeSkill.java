/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.headhonchos.skill;

import java.lang.Object;import java.lang.Override;import java.lang.String; /**
 *
 * @author richa
 */
public class ResumeSkill {

    int exp = 0;
    int lastUsed = 0;
    String skillName;

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object newSkill) {
        if (newSkill instanceof ResumeSkill) {
            ResumeSkill ns = (ResumeSkill) newSkill;
            if (ns.getName().equalsIgnoreCase(skillName)) {
                return true;
            }
        }
        return false;
    }

    public ResumeSkill(String skillName, int exp, int lastUsed) {
        this.skillName = skillName;
        this.exp = exp;
        this.lastUsed = lastUsed;

    }

    public String getName() {
        return this.skillName;
    }

    public int getExp() {
        return this.exp;
    }

    public int getLastUsed() {
        return this.lastUsed;
    }

    public void setExp(int i) {
        this.exp = i;
    }

    public void setLastUsed(int i) {
        this.lastUsed = i;
    }

    public boolean sameSkill(ResumeSkill otherSkill) {
        if (otherSkill.getName().equalsIgnoreCase(skillName) && otherSkill.getExp() == this.exp && otherSkill.getLastUsed() == this.lastUsed) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return skillName + "|E" + exp + "L" + lastUsed;
    }
}
