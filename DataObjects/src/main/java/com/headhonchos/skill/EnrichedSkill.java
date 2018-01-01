package com.headhonchos.skill;

/**
 * Created by ishu on 2/7/14.
 */
public class EnrichedSkill {
    String name;
    double weight;

    public EnrichedSkill(String name,double weight){
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EnrichedSkill{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
