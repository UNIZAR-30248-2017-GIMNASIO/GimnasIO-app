package com.patan.gimnasio;

import java.util.List;

/**
 * Created by alejandro on 25/10/17.
 */

public class Exercise {

    private String name;
    private List<String> muscle;
    private String description;
    private String image;
    private List<String> tags;

    public Exercise(String name,List<String> muscle,String description,String image,List<String> tags){
        this.name=name;
        this.muscle=muscle;
        this.description=description;
        this.image=image;
        this.tags=tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMuscle(List<String> muscle){
        this.muscle = muscle;
    }

    public void setDescription(String desc){
        this.description = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getMuscle() {
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
