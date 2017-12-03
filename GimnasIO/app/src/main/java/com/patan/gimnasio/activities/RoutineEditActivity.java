package com.patan.gimnasio.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.R;
import com.patan.gimnasio.domain.Routine;

import java.util.ArrayList;

// Aqui se mostrara toda la informacion de una rutina con ejercicios, etc. Mediante el boton flotante se podran añadir nuevos ejercicios
// Si se accedio mediante crear rutina esta activididad estara vacia
// Si se accedio mediante ver/editar rutina estara rellenada con los datos de la rutina
public class RoutineEditActivity extends AppCompatActivity {

    private String mode_in;
    private long id_in;
    private EditText textName;
    private EditText textGym;
    private EditText textSeries;
    private EditText textRep;
    private EditText textRelax;
    private EditText textObjetivo;
    private FloatingActionButton fab;
    private Menu optionsMenu;

    private final int DELETE_EX = 1;
    private final int MOVE_EX_UP = 2;
    private final int MOVE_EX_DOWN = 3;

    private GymnasioDBAdapter db;

    private ListView l;

    public GymnasioDBAdapter getGymnasioDbAdapter() {
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_edit);

        l = (ListView)findViewById(R.id.routineEditList);
        textName = (EditText) findViewById(R.id.nombreRutina);
        textGym = (EditText) findViewById(R.id.gimnasioRutina);
        textSeries = (EditText) findViewById(R.id.seriesRutina);
        textRep = (EditText) findViewById(R.id.repeticionesRutina);
        textRelax = (EditText) findViewById(R.id.tiempoRelaxRutina);
        textObjetivo = (EditText) findViewById(R.id.objetivoRutina);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        registerForContextMenu(l);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Adapter adapter = l.getAdapter();
                Cursor item = (Cursor) adapter.getItem(position);
                int pos = item.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                long id_ex = item.getLong(pos);
                Intent intent = new Intent(v.getContext(), ExerciseViewActivity.class);
                intent.putExtra("ID",id_ex);
                startActivity(intent);
            }
        });

        db = new GymnasioDBAdapter(this);
        db.open();

        Intent intent = getIntent();
        mode_in = intent.getStringExtra("MODE");

        if (mode_in.equals("new")) {
            ArrayList<Long> array = new ArrayList<>();
            Routine r = new Routine("","","",0,0,0,array);
            id_in = db.createFreemiumRoutine(r,null);
            populateFields();
            goToEditMode();
        } else if (mode_in.equals("view")) {
            // Abrimos en modo editar
            id_in = intent.getLongExtra("ID", 0);
            populateFields();
            goToViewMode();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (getSupportActionBar().getTitle().equals("Rutina (Editar)")) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.add(Menu.NONE, DELETE_EX, Menu.NONE, R.string.menu_delete);
            menu.add(Menu.NONE, MOVE_EX_UP, Menu.NONE, "Subir ejercicio");
            menu.add(Menu.NONE, MOVE_EX_DOWN, Menu.NONE, "Bajar ejercicio");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_EX :
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Adapter adapter = l.getAdapter();
                Cursor c = (Cursor) adapter.getItem(info.position);
                int pos = c.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                long id = c.getLong(pos);
                Routine r = getRoutineFields();
                ArrayList<Long> ex = r.getExercises();
                int index = ex.indexOf(id);
                ex.remove(index);
                r.setExercises(ex);
                db.updateFreemiumRoutine(id_in,r,null);
                populateFields();
                return true;
            case MOVE_EX_UP :
                AdapterView.AdapterContextMenuInfo info_up = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Adapter adapter_up = l.getAdapter();
                if (info_up.position > 0 && adapter_up.getCount() >= 2) {
                    Cursor c_up = (Cursor) adapter_up.getItem(info_up.position);
                    int pos_up = c_up.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                    long id_up = c_up.getLong(pos_up);
                    Routine r_up = getRoutineFields();
                    ArrayList<Long> ex_up = r_up.getExercises();
                    int index_up = ex_up.indexOf(id_up);
                    long exercise1_up = ex_up.get(index_up);
                    long exercise2_up = ex_up.get(index_up -1);
                    ex_up.set(index_up, exercise2_up);
                    ex_up.set(index_up -1, exercise1_up);
                    r_up.setExercises(ex_up);
                    db.updateFreemiumRoutine(id_in,r_up,null);
                    populateFields();
                }
                return true;
            case MOVE_EX_DOWN :
                AdapterView.AdapterContextMenuInfo info_down = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Adapter adapter_down = l.getAdapter();
                if (info_down.position < adapter_down.getCount()-1 && adapter_down.getCount() >= 2) {
                    Log.w("COUNT: ",""+adapter_down.getCount());
                    Log.w("POS: ",""+info_down.position);
                    Cursor c_down = (Cursor) adapter_down.getItem(info_down.position);
                    int pos_down = c_down.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                    long id_down = c_down.getLong(pos_down);
                    Routine r_down = getRoutineFields();
                    ArrayList<Long> ex_down = r_down.getExercises();
                    int index_down = ex_down.indexOf(id_down);
                    long exercise1_down = ex_down.get(index_down);
                    long exercise2_down = ex_down.get(index_down + 1);
                    ex_down.set(index_down, exercise2_down);
                    ex_down.set(index_down + 1, exercise1_down);
                    r_down.setExercises(ex_down);
                    db.updateFreemiumRoutine(id_in, r_down,null);
                    populateFields();
                }
                return true;
            default: return false;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mode_in.equals("new")) {
            inflater.inflate(R.menu.routine_edit_menu_2, menu);
        } else inflater.inflate(R.menu.routine_edit_menu_1, menu);
        optionsMenu = menu;
        return true;
    }

    // Para cambiar del menu de ver al de editar
    public boolean changeMenuToView() {
        optionsMenu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_edit_menu_1, optionsMenu);
        return true;
    }

    // Para cambiar del menu de editar al de ver
    public boolean changeMenuToEdit() {
        optionsMenu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_edit_menu_2, optionsMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_edit_1:
                goToEditMode();
                changeMenuToEdit();
                break;
            case R.id.action_edit_2:
                saveState();
                changeMenuToView();
                goToViewMode();
                break;
        }
        return true;
    }

    // Metodo para cambiar las propiedades de los campos EditText al modo Editar
    public void goToEditMode() {

        textName.setFocusable(true);
        textName.setEnabled(true);
        textName.setFocusableInTouchMode(true);
        textName.setClickable(true);

        // ESTO NUNCA SERA EDITABLE
        textGym.setFocusable(true);
        textGym.setEnabled(true);
        textGym.setFocusableInTouchMode(true);
        textGym.setClickable(true);

        textSeries.setFocusable(true);
        textSeries.setEnabled(true);
        textSeries.setFocusableInTouchMode(true);
        textSeries.setClickable(true);

        textRep.setFocusable(true);
        textRep.setEnabled(true);
        textRep.setFocusableInTouchMode(true);
        textRep.setClickable(true);

        textRelax.setFocusable(true);
        textRelax.setEnabled(true);
        textRelax.setFocusableInTouchMode(true);
        textRelax.setClickable(true);

        textObjetivo.setFocusable(true);
        textObjetivo.setEnabled(true);
        textObjetivo.setFocusableInTouchMode(true);
        textObjetivo.setClickable(true);

        fab.show();
        getSupportActionBar().setTitle("Rutina (Editar)");
    }

    // Metodo para cambiar las propiedades de los campos EditText al modo Ver
    public void goToViewMode() {
        textName.setFocusable(false);
        textName.setEnabled(false);
        textName.setTextColor(getResources().getColor(R.color.labelColor));

        textGym.setFocusable(false);
        textGym.setEnabled(false);
        textGym.setTextColor(getResources().getColor(R.color.labelColor));

        textSeries.setFocusable(false);
        textSeries.setEnabled(false);
        textSeries.setTextColor(getResources().getColor(R.color.labelColor));

        textRep.setFocusable(false);
        textRep.setEnabled(false);
        textRep.setTextColor(getResources().getColor(R.color.labelColor));

        textRelax.setFocusable(false);
        textRelax.setEnabled(false);
        textRelax.setTextColor(getResources().getColor(R.color.labelColor));

        textObjetivo.setFocusable(false);
        textObjetivo.setEnabled(false);
        textObjetivo.setTextColor(getResources().getColor(R.color.labelColor));

        fab.hide();
        getSupportActionBar().setTitle("Rutina");
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    // Metodo que cambia a la actividad de ExercisesListActivity en modo routine par aañadir ejercicios
    public void goToListOfExercises(View v) {
        saveState();
        Intent intent = new Intent(v.getContext(), ExerciseListActivity.class);
        intent.putExtra("MODE","routine");
        startActivityForResult(intent,1);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra("ID",0);  // Cogemos el ID del ejercicio añadido;
                Routine r = getRoutineFields();
                ArrayList<Long> r_ex = r.getExercises();
                r_ex.add(id);
                r.setExercises(r_ex);
                boolean exito = db.updateFreemiumRoutine(id_in,r,null);
                populateFields();
            }
        }
    }

    // Metodo que genera un objeto rutina a partir del contenido de los campos de la actividad
    public Routine getRoutineFields() {
        String nameGym = textGym.getText().toString();
        String name;
        if (textName.getText().toString().equals("")) {
            name = "Rutina sin nombre";
        } else name = textName.getText().toString();
        String objective = textObjetivo.getText().toString();
        int series = 0;
        if (!textSeries.getText().toString().equals("")) {
            series = Integer.parseInt(textSeries.getText().toString());
        }
        int rep = 0;
        if (!textRep.getText().toString().equals("")) {
            rep = Integer.parseInt(textRep.getText().toString());
        }
        double relxTime = 0.0;
        if (!textRelax.getText().toString().equals("")) {
            relxTime = Double.parseDouble(textRelax.getText().toString());
        }

        ArrayList<Long> id_array = new ArrayList<>();

        if (l.getCount() != 0) {
            // Cogemos los ejercicios de la listView
            Adapter adapter = this.l.getAdapter();
            int elementos = adapter.getCount();

            if (elementos != 0) {
                for (int i = 0; i < elementos; i++) {
                    Cursor item = (Cursor) adapter.getItem(i);
                    int pos = item.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE);
                    long id = item.getLong(pos);
                    id_array.add(id);
                }
            }
        }

        Routine r = new Routine(nameGym, name, objective, series, relxTime, rep,id_array);
        return r;
    }

    public void saveState() {
        Routine r = getRoutineFields();
        if (mode_in.equals("new")) {
            mode_in = "view";   // Cambiamos a modo view para que no se cree la rutina multiples veces
        }
        db.updateFreemiumRoutine(id_in,r,null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No se si es necesario
         //populateFields();
    }

    public void populateFields() {
        Cursor routine = db.fetchRoutine(id_in);
        startManagingCursor(routine);
        if (routine.getString(routine.getColumnIndex(db.KEY_RO_NAME)) != null) {
            textName.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_NAME)));
        } else textName.setText("");

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_GYM)) != null) {
            textGym.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_GYM)));
        } else textGym.setText("");
        /*
        if (routine.getString(routine.getColumnIndex(db.KEY_RO_S)) != null) {
            textSeries.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_S)));
        } else textSeries.setText("");

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_R)) != null) {
            textRep.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_R)));
        } else textRep.setText("");

        if (routine.getString(routine.getColumnIndex(db.KEY_RO_RT)) != null) {
            textRelax.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_RT)));
        } else textRelax.setText("");
        */
        if (routine.getString(routine.getColumnIndex(db.KEY_RO_OBJ)) != null) {
            textObjetivo.setText(routine.getString(routine.getColumnIndex(db.KEY_RO_OBJ)));
        } else textObjetivo.setText("");
        stopManagingCursor(routine);

        if (!mode_in.equals("new")) {
            populateExerciseList();
        }
    }

    // Metodo que rellena la lista de ejercicios
    public void populateExerciseList() {
        Cursor ejercicios = db.getExercisesFromRoutine(id_in);
        if (ejercicios != null) {
            startManagingCursor(ejercicios);
            // Create an array to specify the fields we want to display in the list (only NAME)
            String[] from = new String[] {GymnasioDBAdapter.KEY_EX_NAME,GymnasioDBAdapter.KEY_EX_TAG};
            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] { R.id.ex_row,R.id.ex_row2};
            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.exercises_row, ejercicios, from, to,0);
            l.setAdapter(notes);
            registerForContextMenu(l);
            stopManagingCursor(ejercicios);
        } else {
            String[] from = new String[] {};
            // and an array of the fields we want to bind those fields to (in this case just text1)
            int[] to = new int[] {};
            // Now create an array adapter and set it to display using our row
            SimpleCursorAdapter notes =
                    new SimpleCursorAdapter(this, R.layout.exercises_row, ejercicios, from, to,0);
            l.setAdapter(notes);
            registerForContextMenu(l);
        }
    }
}
