package com.patan.gimnasio;

import java.util.ArrayList;
import java.util.List;


public class Exercise {

    private String name;
    private String muscle;
    private String description;
    private String image;
    private ArrayList<String> tags;

    public Exercise(String name,String muscle,String description,String image,ArrayList<String> tags){
        this.name=name;
        this.muscle=muscle;
        this.description=description;
        this.image=image;
        this.tags=tags;
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

    public List<String> getTags() {
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

}
