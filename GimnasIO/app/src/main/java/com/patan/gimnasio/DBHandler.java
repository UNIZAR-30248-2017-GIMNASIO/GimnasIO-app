package com.patan.gimnasio;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static android.content.ContentValues.TAG;
import static java.lang.String.*;

/**
 * Created by alejandro on 25/10/17.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rutinasDB.db";
    private static final String Table_Rutina = "Rutina";
    private static final String Table_Ejercicio = "Ejercicio";;
    private static final String Table_EjdeRutina = "EjDeRutina";
    private static final String Table_Musculos = "Musculos";
    private static final String Table_MusculosxEj = "MusculosxEj";
    private static final String Table_Tag = "Tags";
    private static final String Table_TagxEj = "TagxEj";


    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> lista = new ArrayList<String>();
        String crearRutina = "CREATE TABLE IF NOT EXISTS " + Table_Rutina + " (id INTEGER PRIMARY KEY ,series INTEGER not null ,relaxtime double not null,rep INTEGER not null,objetivo VARCHAR(20) not null,nombre VARCHAR(20) not null,premium INT not null, gym varchar(20))";
        lista.add(crearRutina);
        String crearEjercicio = "CREATE TABLE IF NOT EXISTS " + Table_Ejercicio + " (id INTEGER  PRIMARY KEY ,nombre VARCHAR(20) not null, desc text not null, imagen varchar(20) not null)";
        lista.add(crearEjercicio);
        String crearEjxRutina = "CREATE TABLE IF NOT EXISTS "+ Table_EjdeRutina + " (idRut INTEGER,idEj INTEGER, FOREIGN KEY(idRut) REFERENCES "+Table_Rutina+"(id),FOREIGN KEY (idEj) REFERENCES "+Table_Ejercicio+"(id))";
        lista.add(crearEjxRutina);
        String crearMusculo = "CREATE TABLE IF NOT EXISTS " + Table_Musculos + " (id INTEGER PRIMARY KEY ,nombre varchar(20) UNIQUE)";
        lista.add(crearMusculo);
        String crearMxEj = "CREATE TABLE IF NOT EXISTS "+ Table_MusculosxEj + " (idMusculo INTEGER,idEj INTEGER, FOREIGN KEY(idMusculo) REFERENCES "+Table_Musculos+" (id),FOREIGN KEY (idEj) REFERENCES "+Table_Ejercicio+"(id))";
        lista.add(crearMxEj);
        String crearTag = "CREATE TABLE IF NOT EXISTS " + Table_Tag + " (id INTEGER PRIMARY KEY ,nombre varchar(20) UNIQUE)";
        lista.add(crearTag);
        String crearTagxEj = "CREATE TABLE IF NOT EXISTS "+ Table_TagxEj + " (idTag INTEGER,idEj INTEGER, FOREIGN KEY(idTag) REFERENCES "+Table_Tag+" (id),FOREIGN KEY (idEj) REFERENCES "+Table_Ejercicio+"(id))";
        lista.add(crearTagxEj);

        for (String query : lista) db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS "+ Table_Ejercicio);
        db.execSQL("DROP TABLE IF EXISTS "+ Table_Rutina);
        db.execSQL("DROP TABLE IF EXISTS "+ Table_EjdeRutina);
        db.execSQL("DROP TABLE IF EXISTS "+ Table_Musculos);
        db.execSQL("DROP TABLE IF EXISTS "+ Table_MusculosxEj);
        db.execSQL("DROP TABLE IF EXISTS "+ Table_TagxEj);
        db.execSQL("DROP TABLE IF EXISTS "+ Table_TagxEj);

        onCreate(db);
    }

    /*Devuelve TRUE si la base de datos existe*/
    public boolean exists(Context context){
        File DBfile = context.getDatabasePath(DATABASE_NAME);
        return DBfile.exists();
    }

    public void actualizarEjercicios(JSONObject JSON) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        JSONArray ejercicios = JSON.getJSONArray("exercises");

        Log.e(TAG, "Response from url: " + ejercicios);
    //name: args[0], muscle: args[1], description: args[2], images: destiny, tag: args[4]}],
        for(int i = 0;i < ejercicios.length();i++){
            JSONObject ejercicio = ejercicios.getJSONObject(i);
            String name = ejercicio.getString("name");
            String des = ejercicio.getString("description");
            String image = ejercicio.getString("images");
            db.execSQL(format("INSERT INTO %s values(%d,'%s','%s','%s');",Table_Ejercicio, i, name, des, image));
            JSONArray tags = ejercicio.getJSONArray("tag");
                for(int t=0;t < tags.length();t++){
                    String namet = tags.getString(t);
                    db.execSQL(format("INSERT INTO %s values(%d,'%s');", Table_Tag,t,namet));
                    db.execSQL(format("INSERT INTO %s values(%d,%d);", Table_TagxEj,t,i));
                }
            JSONArray muscles = ejercicio.getJSONArray("muscle");
                for(int m=0;m < muscles.length();m++){
                    String namem = tags.getString(m);
                    db.execSQL(format("INSERT INTO %s values(%d,'%s');", Table_Musculos,i,namem));
                    db.execSQL(format("INSERT INTO %s values(%d,%d)", Table_MusculosxEj,m,i));
                }
        }
    }

    public void actualizarEjercicios(List<Exercise> exercises) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        //name: args[0], muscle: args[1], description: args[2], images: destiny, tag: args[4]}],
        int i = -1;
        for (Exercise e : exercises) {
            i++;
            String name = e.getName();
            String des = e.getDescription();
            String image = e.getImage();

            //db.execSQL(format("INSERT INTO %s values(%d,'%s','%s','%s');",Table_Ejercicio, i, name, des, image));
            db.execSQL("INSERT INTO " + Table_Ejercicio + " values(" + i + ",'" + name + "','" + des + "','" + image + "');");
            List<String> muscles = e.getMuscle();
            List<String> tags = e.getTags();
            //Coger el numero de tags
            c = db.rawQuery("SELECT * FROM  " + Table_Tag, null);
            int numTags = c.getCount();
            for (int t = 0; t < tags.size(); t++) {
                String tag = tags.get(t);
                c = db.rawQuery("SELECT * FROM  " + Table_Tag + " WHERE nombre='" + tag + "'", null);
                int count = c.getCount();
                if (count == 0) {
                    db.execSQL("INSERT INTO " + Table_Tag + " values(" + numTags + ",'" + tag + "');");
                    db.execSQL("INSERT INTO " + Table_TagxEj + " values(" + numTags + "," + i + ");");
                    numTags++;
                } else {
                    c.moveToFirst();
                    db.execSQL("INSERT INTO " + Table_TagxEj + " values(" + c.getInt(c.getColumnIndex("id")) + "," + i + ");");
                }

            }
            //Coger el numero de musculos
            c = db.rawQuery("SELECT * FROM  " + Table_Musculos, null);
            int numMusculos = c.getCount();
            for (int m = 0; m < muscles.size(); m++) {
                String namem = muscles.get(m);
                c = db.rawQuery("SELECT * FROM  " + Table_Musculos + " WHERE nombre='" + namem + "'", null);
                int count = c.getCount();
                if (count == 0) {
                    db.execSQL("INSERT INTO " + Table_Musculos + " values(" + numMusculos + ",'" + namem + "');");
                    db.execSQL("INSERT INTO " + Table_MusculosxEj + " values(" + numMusculos + "," + i + ");");
                    numMusculos++;
                } else {
                    c.moveToFirst();
                    db.execSQL("INSERT INTO " + Table_MusculosxEj + " values(" + c.getInt(c.getColumnIndex("id")) + "," + i + ");");
                }

            }
        }
    }

    public List<Exercise> getExercises() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Exercise> result = new ArrayList<Exercise>();
        List<String> muscles = new ArrayList<String>();
        List<String> tags = new ArrayList<String>();


        Cursor cursor = db.rawQuery("SELECT * FROM " + Table_Ejercicio, null);
        int count = cursor.getCount();
        cursor.moveToFirst();
        for (int i=0;i< count ;i++){
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String desc = cursor.getString(2);
                String image = cursor.getString(3);
                Cursor cursorMuscles = db.rawQuery("SELECT m.nombre FROM " + Table_MusculosxEj + " mxe ," + Table_Ejercicio + " e ," + Table_Musculos + " m WHERE e.id = " + id + " and m.id = mxe.idMusculo AND e.id=mxe.idEj", null);
                int countMuscles = cursorMuscles.getCount();
                cursorMuscles.moveToFirst();
                for( int j = 0;j < countMuscles;j++){
                        muscles.add(cursorMuscles.getString(1));
                        cursorMuscles.moveToNext();
                }
                Cursor cursorTags = db.rawQuery("SELECT t.nombre FROM " + Table_TagxEj + " txe, " + Table_Ejercicio + " e, " + Table_Tag + " t WHERE e.id= " + id + " and t.id = txe.idTag AND e.id=txe.idEj", null);
                int countTags = cursorTags.getCount();
                cursorTags.moveToFirst();
                for( int k = 0;k < countMuscles;k++){
                        tags.add(cursorTags.getString(1));
                        cursorTags.moveToNext();
                }
                cursor.moveToNext();
                result.add(new Exercise(name, muscles, desc, image, tags));
            }

        return result;
    }

    /*Funcion que devuelve el ejercicio segun el nombre del mismo, para usar en la actividad que muestra el ejercicio en grande*/
    public Exercise getExercise(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> muscles = new ArrayList<String>();
        List<String> tags = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT * FROM " + Table_Ejercicio + " WHERE nombre='"+ name + "'",null);
        c.moveToFirst();
        c.moveToFirst();
        int id = c.getInt(0);
        String desc = c.getString(2);
        String image = c.getString(3);
        Cursor cursorMuscles = db.rawQuery("SELECT m.nombre FROM " + Table_MusculosxEj + " mxe ," + Table_Ejercicio + " e ," + Table_Musculos + " m WHERE e.id = " + id + " and m.id = mxe.idMusculo AND e.id=mxe.idEj", null);
        int countMuscles = cursorMuscles.getCount();
        cursorMuscles.moveToFirst();
        for( int j = 0;j < countMuscles;j++){
            muscles.add(cursorMuscles.getString(1));
            cursorMuscles.moveToNext();
        }
        Cursor cursorTags = db.rawQuery("SELECT t.nombre FROM " + Table_TagxEj + " txe, " + Table_Ejercicio + " e, " + Table_Tag + " t WHERE e.id= " + id + " and t.id = txe.idTag AND e.id=txe.idEj", null);
        int countTags = cursorTags.getCount();
        cursorTags.moveToFirst();
        for( int k = 0;k < countMuscles;k++){
            tags.add(cursorTags.getString(1));
            cursorTags.moveToNext();
        }
            return (new Exercise(name, muscles, desc, image, tags));
    }


    /*Funcion para añadir rutinas freemium */
    public void addRoutineFreemium(Routine r) {
        SQLiteDatabase db = this.getWritableDatabase();
        String name = r.getName();
        String obj = r.getObjective();
        int series = r.getSeries();
        int rep = r.getRep();
        double relxtime = r.getRelxTime();
        List<Exercise> ejercicios = r.getExcercises();

        Cursor c = db.rawQuery("Select * from " + Table_Rutina,null);
        int newId = c.getCount();
        List<Integer> ExercisesId = new ArrayList<>();
        //bucle para recuperar los ID de los ejercicios de la rutina
        for (Exercise e : ejercicios) {
            String nameEj = e.getName();
            c = db.rawQuery("Select * from " +  Table_Ejercicio + " e  WHERE  e.nombre='"+nameEj+"'", null);
            ExercisesId.add(c.getInt(0));
        }
        //Introducimos la rutina
        db.execSQL("INSERT INTO " + Table_Rutina + " VALUES ("+newId+"," + series + " , " + relxtime + " , " + rep + " ,'" + obj + "','" + name + "',0,null");
        //Añadimos los ejercicios de la rutina
        for (int ejId : ExercisesId) db.execSQL("INSERT INTO " + Table_EjdeRutina + " VALUES (" + newId +  "," + ejId + ")");
    }

    /*Metodo para añadir rutinas premium */
    public void addRoutinePremium(Routine r) {
        SQLiteDatabase db = this.getWritableDatabase();
        String name = r.getName();
        String gym = r.getNameGym();
        String obj = r.getObjective();
        int series = r.getSeries();
        int rep = r.getRep();
        double relxtime = r.getRelxTime();
        List<Exercise> ejercicios = r.getExcercises();

        Cursor c = db.rawQuery("Select * from " + Table_Rutina,null);
        int newId = c.getCount();
        List<Integer> ExercisesId = new ArrayList<>();
        //bucle para recuperar los ID de los ejercicios de la rutina
        for (Exercise e : ejercicios) {
            String nameEj = e.getName();
            c = db.rawQuery("Select * from " +  Table_Ejercicio + " e  WHERE  e.nombre='"+nameEj+"'", null);
            ExercisesId.add(c.getInt(0));
        }
        //Introducimos la rutina
        db.execSQL("INSERT INTO " + Table_Rutina + " VALUES ("+newId+"," + series + " , " + relxtime + " , " + rep + " ,'" + obj + "','" + name + "',1,'" + gym +"')");
        //Añadimos los ejercicios de la rutina
        for (int ejId : ExercisesId) db.execSQL("INSERT INTO " + Table_EjdeRutina + " VALUES (" + newId +  "," + ejId + ")");
    }


}
