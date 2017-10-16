package com.patan.gimnasio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// En esta actividad se mostrara toda la informacion asociada a un ejercicio seleccionado desde ExerciseListActivity
public class ExerciseViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_view);
    }
}
