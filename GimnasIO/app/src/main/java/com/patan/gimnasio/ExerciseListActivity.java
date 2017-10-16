package com.patan.gimnasio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// En esta actividad se mostrara una lista con todos los ejercicios, ademas de una barra de busqueda para poder filtrar
public class ExerciseListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_list);
    }
}
