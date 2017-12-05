package com.patan.gimnasio.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.patan.gimnasio.R;

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

        textoNombre.setText(name_in);   // Ponemos el nombre del ejercicio

        // TODO: Contemplar el caso en el que se llame mediante editar
        //  - Cogeremos extras del intent con los valores de los campos y los rellenarmos,
        //      el guardar ejerciio devolvera lo mismo.
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

        i.putExtra("ID",id_in);
        i.putExtra("SERIES",series);
        i.putExtra("REP",rep);
        i.putExtra("RELAX",relax);

        setResult(RESULT_OK,i);
        finish();       // Forzamos volver a la actividad anterior
    }
}
