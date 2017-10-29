package com.patan.gimnasio;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

// En esta actividad se mostrara una lista de rutinas creadas ademas de la opcion de crear una nueva rutina
//  - Crear rutina (boton flotante) llevara a una RoutineEditActivity vacia
//  - Con una rutina creada podremos:
//      - Verla/Editarla, se abrira una RoutineEditActivity rellenada con los datos de la rutina
//      - Ejecutarla, se abrira una RoutineExecuteActivity para ejecutarla
public class RoutineListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);
    }

}
