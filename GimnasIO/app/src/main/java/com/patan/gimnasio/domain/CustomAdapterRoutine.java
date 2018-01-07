package com.patan.gimnasio.domain;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.patan.gimnasio.R;

import java.util.List;

public class CustomAdapterRoutine  extends ArrayAdapter<Routine> {

    private LayoutInflater layoutInflater;


    public CustomAdapterRoutine(Context context, List<Routine> objects)
    {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // holder pattern
        Holder holder = null;

        if (convertView == null)
        {
            holder = new Holder();

            convertView = layoutInflater.inflate(R.layout.routines_row_checkbox, null);
            holder.setTextViewTitle((TextView) convertView.findViewById(R.id.ro_rowcb));
            holder.setTextViewObjective((TextView) convertView.findViewById(R.id.ro_rowobj));
            //holder.setTextViewSubtitle((TextView) convertView.findViewById(R.id.textViewSubtitle));
//            holder.setCheckBox((CheckBox) convertView
//                    .findViewById(R.id.checkBoxExercise));
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        Routine r = getItem(position);
        holder.getRoutineName().setText(r.getName());
        holder.getRoutineObjective().setText(r.getObjective());
//        holder.getCheckBox().setTag(position);
//        holder.getCheckBox().setChecked(r.isChecked());
//        holder.getCheckBox().setOnClickListener(this);
        return convertView;
    }


    static class Holder
    {
        TextView RoutineName;
        TextView RoutineObjective;

        public TextView getRoutineName()
        {
            return RoutineName;
        }

        public TextView getRoutineObjective()
        {
            return RoutineObjective;
        }

        public void setTextViewTitle(TextView name)
        {
            this.RoutineName = name;
        }
        public void setTextViewObjective(TextView objective) {
            this.RoutineObjective = objective;
        }

    }
}
