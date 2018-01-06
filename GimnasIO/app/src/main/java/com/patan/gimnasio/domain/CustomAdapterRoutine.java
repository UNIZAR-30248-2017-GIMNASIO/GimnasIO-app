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

import java.util.List;

public class CustomAdapterRoutine  extends ArrayAdapter<Routine> implements
        View.OnClickListener  {

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
//        holder.getCheckBox().setTag(position);
//        holder.getCheckBox().setChecked(r.isChecked());
//        holder.getCheckBox().setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {

        CheckBox checkBox = (CheckBox) v;
        int position = (Integer) v.getTag();
        getItem(position).setChecked(checkBox.isChecked());

        String msg = "Has seleccionado la rutina: " + getItem(position).getName();
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    static class Holder
    {
        TextView RoutineName;
        CheckBox checkBox;

        public TextView getRoutineName()
        {
            return RoutineName;
        }

        public void setTextViewTitle(TextView name)
        {
            this.RoutineName = name;
        }


//        public CheckBox getCheckBox()
//        {
//            return checkBox;
//        }
//        public void setCheckBox(CheckBox checkBox)
//        {
//            this.checkBox = checkBox;
//        }

    }
}
