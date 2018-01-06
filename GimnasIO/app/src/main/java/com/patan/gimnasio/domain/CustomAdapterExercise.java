package com.patan.gimnasio.domain;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.patan.gimnasio.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterExercise extends ArrayAdapter<Exercise> implements
        View.OnClickListener  {

    private LayoutInflater layoutInflater;


    public CustomAdapterExercise(Context context, List<Exercise> objects)
    {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // holder pattern
        CustomAdapterExercise.Holder holder = null;
        if (convertView == null)
        {
            holder = new CustomAdapterExercise.Holder();

            convertView = layoutInflater.inflate(R.layout.exercises_row_checkbox, null);
            holder.setExerciseName((TextView) convertView.findViewById(R.id.ex_row));
            holder.setTags((TextView) convertView.findViewById(R.id.ex_row2));
            holder.setCheckBox((CheckBox) convertView
                    .findViewById(R.id.checkBoxExercise));
            convertView.setTag(holder);
        }
        else
        {
            holder = (CustomAdapterExercise.Holder) convertView.getTag();
        }

        Exercise r = getItem(position);
        holder.getExerciseName().setText(r.getName());
        ArrayList<String> tagsplit = r.getTags();
        String aux = "";
        for(String s : tagsplit) {
            aux += " " + s;
        }
        holder.getTags().setText(aux);
        holder.getCheckBox().setTag(position);
        holder.getCheckBox().setChecked(r.isChecked());
        holder.getCheckBox().setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {

        CheckBox checkBox = (CheckBox) v;
        int position = (Integer) v.getTag();
        getItem(position).setChecked(checkBox.isChecked());



        //String msg = "Has seleccionado la rutina: " + getItem(position).getName();
        //Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();


    }



    static class Holder
    {
        TextView ExerciseName;
        TextView tags;
        CheckBox checkBox;

        public TextView getExerciseName()
        {
            return ExerciseName;
        }

        public void setExerciseName(TextView name)
        {
            this.ExerciseName = name;
        }

        public TextView getTags()
        {
            return tags;
        }

        public void setTags(TextView name)
        {
            this.tags = name;
        }

        public CheckBox getCheckBox()
        {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox)
        {
            this.checkBox = checkBox;
        }

    }

}
