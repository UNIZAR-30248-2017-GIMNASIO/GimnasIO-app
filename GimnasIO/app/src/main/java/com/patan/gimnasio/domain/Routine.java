package com.patan.gimnasio.domain;

import java.util.ArrayList;


public class Routine{

    private String nameGym;
    private String name;
    private String objective;

    private ArrayList<Long> exercises;

    public Routine(String nameGym,String name,String objective){
        this.nameGym = nameGym;
        this.name = name;
        this.objective = objective;
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

    public String getName() {
        return name;
    }


    public String getNameGym() {
        return nameGym;
    }

    public String getObjective() {
        return objective;
    }

    public boolean equalList(ArrayList<Long> list1, ArrayList<Long> list2)
    {
        //null checking
        if(list1==null && list2==null)
            return true;
        if((list1 == null && list2 != null) || (list1 != null && list2 == null))
            return false;

        if(list1.size()!=list2.size())
            return false;
        for(Long itemList1: list1)
        {
            if(!list2.contains(itemList1))
                return false;
        }

        return true;
    }
}
