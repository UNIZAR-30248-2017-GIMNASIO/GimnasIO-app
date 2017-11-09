package com.patan.gimnasio;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

// Aqui se mostrara toda la informacion de una rutina con ejercicios, etc. Mediante el boton flotante se podran añadir nuevos ejercicios
// Si se accedio mediante crear rutina esta activididad estara vacia
// Si se accedio mediante ver/editar rutina estara rellenada con los datos de la rutina
public class RoutineEditActivity extends AppCompatActivity {

    private String mode_in;
    private long id_in;

    private ListView l;
    private EditText textName;
    private EditText textGym;
    private EditText textSeries;
    private EditText textRep;
    private EditText textRelax;
    private EditText textObjetivo;

    private GymnasioDBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_edit);

        db = new GymnasioDBAdapter(this);
        db.open();

        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");

        if (mode_in.equals("new")) {
            // Abrimos en modo crear
        } else if (mode_in.equals("edit")) {
            // Abrimos en modo editar
            id_in = intent.getLongExtra("ID", 0);
            fillContentEdit(id_in);
        }

        l = (ListView)findViewById(R.id.routineEditList);
        textName = (EditText) findViewById(R.id.nombreRutina);
        textGym = (EditText) findViewById(R.id.gimnasioRutina);;
        textSeries = (EditText) findViewById(R.id.seriesRutina);
        textRep = (EditText) findViewById(R.id.repeticionesRutina);
        textRelax = (EditText) findViewById(R.id.tiempoRelaxRutina);
        textObjetivo = (EditText) findViewById(R.id.objetivoRutina);
    }

    /*
     * Metodo que rellenara la actividad de editar rutina si se entra desde editar rutina
     */

    public void fillContentEdit(float id_in) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(db.KEY_RO_ID, id_in);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    public void saveState() {
        String nameGym = textGym.getText().toString();
        String name = textName.getText().toString();
        String objective = textObjetivo.getText().toString();
        int series = Integer.parseInt(textSeries.getText().toString());
        int rep = Integer.parseInt(textRep.getText().toString());
        double relxTime = Double.parseDouble(textRelax.getText().toString());
        ArrayList<Exercise> exercises = new ArrayList<>();

        Routine r = new Routine(nameGym, name, objective, series, relxTime, rep, exercises);

        if (mode_in.equals("new")) {
            // Llamamos a create
            db.createFreemiumRoutine(r);

        } else if (mode_in.equals("edit")) {
            // Llamamos a update
            db.updateFreemiumRoutine(id_in,r);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    public void populateFields() {
        Cursor routine = db.fetchRoutine(id_in);
        routine.moveToFirst();
        startManagingCursor(routine);

        textName.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_NAME)));
        textGym.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_GYM)));
        textSeries.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_S)));
        textRep.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_R)));
        textRelax.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_RT)));
        textObjetivo.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_OBJ)));

        // Faltara llenar el ArrayList de ejercicios
    }


}
