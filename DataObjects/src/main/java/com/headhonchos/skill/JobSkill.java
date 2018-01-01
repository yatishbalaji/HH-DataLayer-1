package com.headhonchos.skill;

/**
 * Created by ishu on 9/4/14.
 */
public class JobSkill {
    int exp;
    String name;

    public JobSkill(String name,int exp){
        this.name = name.trim();
        this.exp = exp;

    }

    public String getName() {
        return name;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public boolean equals(Object obj) {
        JobSkill js = (JobSkill)obj;
        return this.name.equalsIgnoreCase(js.getName().trim());
    }

    @Override
    public String toString() {
        return this.getName()+":"+this.getExp();
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
