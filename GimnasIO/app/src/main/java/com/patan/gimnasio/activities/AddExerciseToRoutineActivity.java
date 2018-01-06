package com.patan.gimnasio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.patan.gimnasio.R;
import com.patan.gimnasio.domain.ExFromRoutine;

import java.util.ArrayList;

public class AddExerciseToRoutineActivity extends AppCompatActivity {

    private String mode_in;
    private long id_in;
    private String name_in;
    private int series;
    private int rep;
    private double relax;

    TextView textoNombre;
    EditText textoSeries;
    EditText textoRep;
    EditText textoRelax;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise_to_routine);

        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");
        id_in = intent.getLongExtra("ID",0);
        name_in = intent.getStringExtra("NAME");

        textoNombre = (TextView) findViewById(R.id.nombreEjercicio);
        textoSeries = (EditText) findViewById(R.id.seriesField);
        textoRep = (EditText) findViewById(R.id.repeticionesField);
        textoRelax = (EditText) findViewById(R.id.relaxField);
        button = (Button) findViewById(R.id.buttonAdd);

        textoNombre.setText(name_in);   // Ponemos el nombre del ejercicio

        if (mode_in.equals("EDIT")) {
            series = intent.getIntExtra("SERIES",0);
            rep = intent.getIntExtra("REP",0);
            relax = intent.getDoubleExtra("RELAX",0.0);
            button.setText("Guardar cambios");
            fillFields();
        }
    }

    public void fillFields() {
        textoSeries.setText(String.valueOf(series));
        textoRep.setText(String.valueOf(rep));
        textoRelax.setText(String.valueOf(relax));
    }

    public void guardarEjercicio(View v) {
        Intent i = new Intent();

        if (!textoSeries.getText().toString().equals("")) {
            series = Integer.parseInt(textoSeries.getText().toString());
        } else series = 0;

        if (!textoRep.getText().toString().equals("")) {
            rep = Integer.parseInt(textoRep.getText().toString());
        } else rep = 0;

        if (!textoRelax.getText().toString().equals("")) {
            relax = Double.parseDouble(textoRelax.getText().toString());
        } else relax = 0.0;

            i.putExtra("MODE", mode_in);
            i.putExtra("ID", id_in);
            i.putExtra("SERIES", series);
            i.putExtra("REP", rep);
            i.putExtra("RELAX", relax);

            setResult(RESULT_OK, i);
            finish();       // Forzamos volver a la actividad anterior

    }
}
