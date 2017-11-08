package com.patan.gimnasio;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


// En esta actividad se mostrara toda la informacion asociada a un ejercicio seleccionado desde ExerciseListActivity
public class ExerciseViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_view);
    }
}
