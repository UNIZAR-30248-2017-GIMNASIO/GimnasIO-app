package com.patan.gimnasio;

import java.util.ArrayList;
import java.util.List;


public class Routine{

    private String nameGym;
    private String name;
    private String objective;
    private int series;
    private int rep;
    private double relxTime;
    private ArrayList<Long> excercises;

    public Routine(String nameGym,String name,String objective,int series,double relxTime,int rep, ArrayList<Long> exercises){
        this.nameGym = nameGym;
        this.name = name;
        this.objective = objective;
        this.series = series;
        this.relxTime = relxTime;
        this.excercises = exercises;
        this.rep=rep;
    }

    public void setRelxTime(double relxTime) {
        this.relxTime = relxTime;
    }

    public void setExcercises(ArrayList<Long> excercises) {
        this.excercises = excercises;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameGym(String nameGym) {
        this.nameGym = nameGym;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public String getName() {
        return name;
    }

    public double getRelxTime() {
        return relxTime;
    }

    public int getRep() {
        return rep;
    }

    public int getSeries() {
        return series;
    }

    public ArrayList<Long> getExercises() {
        return excercises;
    }

    public String getNameGym() {
        return nameGym;
    }

    public String getObjective() {
        return objective;
    }

}
