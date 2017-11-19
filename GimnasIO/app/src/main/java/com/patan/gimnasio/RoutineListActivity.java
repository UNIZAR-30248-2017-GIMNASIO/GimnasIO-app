package com.patan.gimnasio;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

// En esta actividad se mostrara una lista de rutinas creadas ademas de la opcion de crear una nueva rutina
//  - Crear rutina (boton flotante) llevara a una RoutineEditActivity vacia
//  - Con una rutina creada podremos:
//      - Verla/Editarla, se abrira una RoutineEditActivity rellenada con los datos de la rutina
//      - Ejecutarla, se abrira una RoutineExecuteActivity para ejecutarla
public class RoutineListActivity extends AppCompatActivity {

    private ListView l;
    private GymnasioDBAdapter db;

    private static final int DELETE_ID = 1;

    public GymnasioDBAdapter getGymnasioDBAdapter(){
        return db;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        db = new GymnasioDBAdapter(this);
        db.open();

        l = (ListView)findViewById(R.id.dbRoutinesList);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(v.getContext(), RoutineEditActivity.class);
                intent.putExtra("MODE","edit");
                intent.putExtra("ID",id);
                startActivity(intent);
            }
        });

        // Rellenamos la lista
        testRutina();
        updateRutina();
        verRutinica();
        fillData();
    }


    private void fillData() {
        // Get all of the routines from the database and create the item list
        Cursor routines = db.fetchRoutines();
        startManagingCursor(routines);
        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[] {GymnasioDBAdapter.KEY_RO_NAME};
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.ro_row };
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.routines_row, routines, from, to,0);
        l.setAdapter(notes);
        registerForContextMenu(l);
    }


    /**
     * Metodo que cambia a la actividad ExerciseViewActivity en modo crear nueva rutina
     */
    public void goToRoutineEditCreate(View v) {
        Intent intent = new Intent(this, RoutineEditActivity.class);
        intent.putExtra("MODE","new");
        intent.putExtra("ID",0);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == DELETE_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            db.deleteRoutine(info.id);
            fillData();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
    }

    private void testRutina(){
        Routine r = new Routine("LA CALLE","MACHACA","MAZAMIENTO",4,90,15,null);
        Cursor c = db.getExerciseByName("Aperturas con mancuerna en banco inclinado");
        String name ="name";
        String muscle = "muscle";
        String desc = "desc";
        String img= "/ruta";
        Exercise e = new Exercise(name,muscle,desc,img,null);
        long id = db.createExercise(e);
        ArrayList<Long> exs = new ArrayList<>();
        exs.add(id);
        r.setExcercises(exs);
        db.createFreemiumRoutine(r);
    }

    private void updateRutina() {
        //db.fetchRoutines();
        Routine r = new Routine("LA CALLE2","MACHACA2","MAZAMIENTO2",4,90,15,null);
        Cursor c = db.getRoutineByName("MACHACA");
        long name = c.getLong(c.getColumnIndex(GymnasioDBAdapter.KEY_RO_ID));
        String name2 ="name2";
        String muscle = "muscle2";
        String desc = "desc2";
        String img= "/ruta2";
        Exercise e = new Exercise(name2,muscle,desc,img,null);
        long id = db.createExercise(e);
        ArrayList<Long> exs = new ArrayList<>();
        exs.add(id);
        r.setExcercises(exs);
        db.updateFreemiumRoutine(name,r);
    }

    private void verRutinica() {
        Cursor c = db.getRoutineByName("MACHACA2");
        long name = c.getLong(c.getColumnIndex(GymnasioDBAdapter.KEY_RO_ID));
        db.getExercisesFromRoutine(name);
        //long a =c2.getLong(c2.getColumnIndex(GymnasioDBAdapter.KEY_EXRO_IDE));
        //Log.d("PRUEBA",a+"");


    }


}
