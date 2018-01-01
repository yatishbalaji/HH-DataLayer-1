package com.headhonchos.jobPosting;

/**
 * Created by richa on 20/8/14.
 */
public class JSkill  implements Comparable<JSkill> {

    @Override
    public String toString() {
        return this.name+"|E"+this.exp+"W"+this.weight;
    }

    String name="";
    int exp=0;
    Double weight=0.0;

    public JSkill(String name) {
        this.name=name;
    }

    public JSkill(String name,int exp,Double weight){
        this.name=name;
        this.exp=exp;
        this.weight=weight;
    }

    public JSkill(String name,Double weight){
        this.name=name;
        this.weight=weight;
    }

    public String getName() {
        return this.name;
    }

    public int getExp(){
        return this.exp;
    }

    public void setExp(int i) {
        this.exp=i;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return weight;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object newSkill) {
        if(newSkill instanceof JSkill){
            JSkill ns = (JSkill)newSkill;
            if(ns.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(JSkill o) {
        if(o.getWeight()<weight){
            return 1;
        }
        else if(o.getWeight()>weight){
            return -1;
        }
        return 0;
    }

}
