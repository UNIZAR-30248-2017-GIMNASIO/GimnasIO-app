package com.patan.gimnasio;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ExerciseDbAdapter {
    /**
     * Definimos constante con el nombre de la tabla
     */
    public static final String DB_TABLE = "EXERCISES" ;

    /**
     * Definimos constantes con el nombre de las columnas de la tabla
     */
    public static final String DB_COL_ID   = "_id";
    public static final String DB_COL_NAME = "name";
    public static final String DB_COL_MUSCLE = "muscles";
    public static final String DB_COL_DESC = "description";
    public static final String DB_COL_IMG = "Image";
    public static final String DB_COL_TAG = "tags";

    private Context context;
    //private ExerciseDbHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Definimos lista de columnas de la tabla para utilizarla en las consultas a la base de datos
     */
    private String[] columnas = new String[]{ DB_COL_ID, DB_COL_NAME, DB_COL_MUSCLE, DB_COL_DESC, DB_COL_IMG, DB_COL_TAG} ;

    public ExerciseDbAdapter(Context context)
    {
        this.context = context;
    }

    public ExerciseDbAdapter abrir() throws SQLException
    {
        //dbHelper = new ExerciseDbHelper(context);
        //db = dbHelper.getWritableDatabase(); //Comprobar con BBDD
        return this;
    }

    public void cerrar()
    {
        //dbHelper.close();
    }

    /**
     * Devuelve cursor con todos las columnas de la tabla
     */
    public Cursor getCursor() throws SQLException
    {
        Cursor c = db.query( true, DB_TABLE, columnas, null, null, null, null, null, null);
        return c;
    }
}
