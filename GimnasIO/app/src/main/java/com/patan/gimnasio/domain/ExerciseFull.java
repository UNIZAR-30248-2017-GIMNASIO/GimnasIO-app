package com.patan.gimnasio.domain;

import java.util.ArrayList;


public class ExerciseFull {

    private String name;
    private String muscle;
    private String description;
    private String image;
    private int repetitions;
    private int series;
    private double relaxTime;
    private ArrayList<String> tags;


    public ExerciseFull(String name, String muscle, String description, String image, int repetitions, int series, double relaxTime, ArrayList<String> tags) {
        this.name = name;
        this.muscle = muscle;
        this.description = description;
        this.image = image;
        this.repetitions = repetitions;
        this.series = series;
        this.relaxTime = relaxTime;
        this.tags = tags;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public double getRelaxTime() {
        return relaxTime;
    }

    public void setRelaxTime(double relaxTime) {
        this.relaxTime = relaxTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMuscle(String muscle){
        this.muscle = muscle;
    }

    public void setDescription(String desc){
        this.description = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }



    public String getMuscle() {
        return muscle;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }



    public boolean equalList(ArrayList<String> list1, ArrayList<String> list2)
    {
        //null checking
        if(list1==null && list2==null)
            return true;
        if((list1 == null && list2 != null) || (list1 != null && list2 == null))
            return false;

        if(list1.size()!=list2.size())
            return false;
        for(String itemList1: list1)
        {
            if(!list2.contains(itemList1))
                return false;
        }

        return true;
    }
    public boolean equals(ExerciseFull otro){
        return (this.name.equals(otro.getName()) && this.description.equals(otro.getDescription()) && otro.getMuscle().equals(this.muscle) && this.image.equals(otro.getImage()) && equalList(this.tags,otro.getTags()));
    }
}
