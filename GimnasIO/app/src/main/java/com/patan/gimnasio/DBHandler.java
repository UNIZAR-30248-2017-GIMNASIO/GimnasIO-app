package com.patan.gimnasio;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GymnasIOapp.db";
    private static final String Table_Routine = "Routine";
    private static final String Table_Exercise = "Exercise";
    private static final String Table_ExOfRoutine = "ExOfRoutine";
    private static final String Table_Tag = "Tags";
    private static final String Table_TagXEx = "TagxEx";
    /**
     * Adds to DB the exercises which are on the first parameter.
     *
    public void actualizarEjercicios(List<Exercise> exercises) {
        //SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;
        //name: args[0], muscle: args[1], description: args[2], images: destiny, tag: args[4]}],
        int i = -1;
        for (Exercise e : exercises) {
            i++;
            String name = e.getName();
            String des = e.getDescription();
            String image = e.getImage();
            //db.execSQL(format("INSERT INTO %s values(%d,'%s','%s','%s');",Table_Exercise, i, name, des, image));
            mDb.execSQL("INSERT INTO " + Table_Exercise + " values(" + i + ",'" + name + "','" + des + "','" + image + "');");
            List<String> muscles;// = e.getMuscle();
            List<String> tags = e.getTag0();
            //Coger el numero de tags
            c = mDb.rawQuery("SELECT * FROM  " + Table_Tag, null);
            int numTags = c.getCount();
            for (int t = 0; t < tags.size(); t++) {
                String tag = tags.get(t);
                c = mDb.rawQuery("SELECT * FROM  " + Table_Tag + " WHERE nombre='" + tag + "'", null);
                int count = c.getCount();
                if (count == 0) {
                    mDb.execSQL("INSERT INTO " + Table_Tag + " values(" + numTags + ",'" + tag + "');");
                    mDb.execSQL("INSERT INTO " + Table_TagXEx + " values(" + numTags + "," + i + ");");
                    numTags++;
                } else {
                    c.moveToFirst();
                    mDb.execSQL("INSERT INTO " + Table_TagXEx + " values(" + c.getInt(c.getColumnIndex("id")) + "," + i + ");");
                }

            }
            //Coger el numero de musculos
            /*c = mDb.rawQuery("SELECT * FROM  " + Table_Muscles, null);
            int numMusculos = c.getCount();
            for (int m = 0; m < muscles.size(); m++) {
                String namem = muscles.get(m);
                c = mDb.rawQuery("SELECT * FROM  " + Table_Muscles + " WHERE nombre='" + namem + "'", null);
                int count = c.getCount();
                if (count == 0) {
                    mDb.execSQL("INSERT INTO " + Table_Muscles + " values(" + numMusculos + ",'" + namem + "');");
                    mDb.execSQL("INSERT INTO " + Table_MuscleXEx + " values(" + numMusculos + "," + i + ");");
                    numMusculos++;
                } else {
                    c.moveToFirst();
                    mDb.execSQL("INSERT INTO " + Table_MuscleXEx + " values(" + c.getInt(c.getColumnIndex("id")) + "," + i + ");");
                }

            }
        }
    }*

    /**
     * Return exercise by tag.
     *
    public List<Exercise> getExercisesByTag(String tag){
        //SQLiteDatabase db = this.getReadableDatabase();
        Cursor m = mDb.rawQuery("Select * from " +  Table_Tag + " e  WHERE  e.nombre='"+tag+"'", null);
        int idT = m.getInt(0); // id del tag que estamos buscando
        Cursor cursor= mDb.rawQuery("Select e.* from " + Table_Exercise + " e," + Table_Tag + " t, " + Table_TagXEx + " txe WHERE  t.id='"+idT+"' and e.id=txe.idEj and t.id=txe.idTag", null);
        //Este cursor tiene los ejercicios donde se usa el tag buscado
        int count = cursor.getCount();
        cursor.moveToFirst();
        List<Exercise> result = new ArrayList<>();
        //Código muy parecido a fetchAllExercises, donde se sacan todos los ejercicios,músculos y tags
        for ( int i=0;i < count;i++){
            List<String> muscles = new ArrayList<String>();
            List<String> tags = new ArrayList<String>();
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String desc = cursor.getString(2);
            String image = cursor.getString(3);
            Cursor cursorMuscles = mDb.rawQuery("SELECT m.nombre FROM " + Table_MuscleXEx + " mxe ," + Table_Exercise + " e ," + Table_Muscles + " m WHERE e.id = " + id + " and m.id = mxe.idMusculo AND e.id=mxe.idEj", null);
            int countMuscles = cursorMuscles.getCount();
            cursorMuscles.moveToFirst();
            for( int j = 0;j < countMuscles;j++){
                muscles.add(cursorMuscles.getString(0));
                cursorMuscles.moveToNext();
            }
            Cursor cursorTags = mDb.rawQuery("SELECT t.nombre FROM " + Table_TagXEx + " txe, " + Table_Exercise + " e, " + Table_Tag + " t WHERE e.id= " + id + " and t.id = txe.idTag AND e.id=txe.idEj", null);
            int countTags = cursorTags.getCount();
            cursorTags.moveToFirst();
            for( int k = 0;k < countTags;k++){
                tags.add(cursorTags.getString(0));
                cursorTags.moveToNext();
            }
            cursor.moveToNext();
            result.add(new Exercise(name, muscles, desc, image, tags));
        }

        return result;
    }*/
    /**
     * Return routine by id.
     *
    public Routine getRoutineById(int id){
        //SQLiteDatabase mDb = this.getReadableDatabase();
        Routine result;
        //(id INTEGER PRIMARY KEY ,series INTEGER not null ,relaxtime double not null,rep INTEGER not null,objetivo VARCHAR(20) not null,nombre VARCHAR(20) not null,premium INT not null, gym varchar(20))
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + Table_Routine + " WHERE id=" + id, null);
        List<Exercise> ejercicios = new ArrayList<>();
        int series = cursor.getInt(1);
        double relaxtime = cursor.getDouble(2);
        int rep = cursor.getInt(3);
        String obj = cursor.getString(4);
        String name = cursor.getString(5);
        int premium = cursor.getInt(6);
        String gymName = null;
        if (premium == 1) gymName = cursor.getString(7);
            /*Ahora hay que sacar la lista de ejercicios
        Cursor cursorej = mDb.rawQuery("SELECT e.nombre FROM " + Table_ExOfRoutine + " edr ," + Table_Exercise + " e ," + Table_Routine + " r WHERE r.id = " + id + " and e.id = edr.idEj AND r.id=edr.idRut", null);
        int countej = cursorej.getCount();
        cursorej.moveToFirst();
        for (int j = 0; j < countej; j++) {
                    /*Se añaden los ejercicios de la rutina con getExercise(name)
                ejercicios.add(getExercise(cursorej.getString(0)));
                cursorej.moveToNext();
        }
        result =  new Routine(gymName, name, obj, series, relaxtime, rep, ejercicios);
        return result;
    }*/
}

