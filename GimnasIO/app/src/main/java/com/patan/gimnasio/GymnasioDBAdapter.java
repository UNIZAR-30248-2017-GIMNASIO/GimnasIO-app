package com.patan.gimnasio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBar;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GymnasioDBAdapter {

    private static final String TAG = "GymnasioDbAdapter";
    private DatabaseHelper DbHelper;
    private SQLiteDatabase Db;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "GymnasIOapp.db";
    private static final String Table_Routine = "Routine";
    private static final String Table_Exercise = "Exercise";
    private static final String Table_ExOfRoutine = "ExOfRoutine";
    private static final String Table_Updates = "Updates";

    public static final String KEY_EX_NAME = "name";
    public static final String KEY_EX_MUSCLE = "muscle";
    public static final String KEY_EX_DESC = "description";
    public static final String KEY_EX_IMG = "image";
    public static final String KEY_EX_ID = "_id";
    public static final String KEY_EX_TAG = "tags";

    public static final String KEY_RO_ID= "_id";
    public static final String KEY_RO_NAME = "name";
    public static final String KEY_RO_S = "series";
    public static final String KEY_RO_RT = "relaxTime";
    public static final String KEY_RO_R = "rep";
    public static final String KEY_RO_PREMIUM = "premium";
    public static final String KEY_RO_GYM = "gym";
    public static final String KEY_RO_OBJ = "objective";

    public static final String KEY_EXRO_ID = "_id";
    public static final String KEY_EXRO_IDR = KEY_RO_ID+"R";
    public static final String KEY_EXRO_IDE = KEY_EX_ID+"E";

    private static final String CREATE_TABLE_ROUTINES = "CREATE TABLE IF NOT EXISTS " + Table_Routine +
            " ("+ KEY_RO_ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"+ KEY_RO_S
            + " INTEGER not null ,"+ KEY_RO_RT +" double not null,"+ KEY_RO_R
            + " INTEGER not null,"+ KEY_RO_OBJ +" VARCHAR(20)"
            + " not null,"+ KEY_RO_NAME +" VARCHAR(20) not null,"+KEY_RO_PREMIUM
            + " INT not null,"+ KEY_RO_GYM +" gym varchar(20))";
    private static final String CREATE_TABLE_EXERCISES="CREATE TABLE IF NOT EXISTS " + Table_Exercise +
            " (" + KEY_EX_ID + " integer primary key autoincrement," + KEY_EX_NAME + " VARCHAR(20) not null," +
    KEY_EX_DESC + " not null," + KEY_EX_MUSCLE + " VARCHAR(20) not null, " + KEY_EX_IMG +
            " varchar(20) not null, "+ KEY_EX_TAG +" varchar(100))";
    private static final String CREATE_TABLE_RELATION = "CREATE TABLE IF NOT EXISTS "
            + Table_ExOfRoutine + "("+KEY_EXRO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EXRO_IDR + " INTEGER," + KEY_EXRO_IDE + " INTEGER)";
    private static final String CREATE__TABLE_UPDATES="CREATE TABLE IF NOT EXISTS " + Table_Updates +
            "( _id integer primary key autoincrement , lastUpdate VARCHAR(20) not null, firstInstalation int not null);";


    private static final String[] RO_ROWS={KEY_RO_ID, KEY_RO_S, KEY_RO_RT, KEY_RO_R,
            KEY_RO_OBJ,KEY_RO_NAME,KEY_RO_PREMIUM,KEY_RO_GYM};
    private static final String[] EX_ROWS=new String[]{KEY_EX_ID, KEY_EX_NAME, KEY_EX_DESC,
            KEY_EX_MUSCLE, KEY_EX_IMG, KEY_EX_TAG};


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ROUTINES);
            db.execSQL(CREATE_TABLE_EXERCISES);
            // todo_tag table create statement
            //String crearEjxRutina = "CREATE TABLE IF NOT EXISTS " + Table_ExOfRoutine
            //        + " (idRut INTEGER,idEj INTEGER, FOREIGN KEY(idRut) REFERENCES "
            //        + Table_Routine + "(_id),FOREIGN KEY (idEj) REFERENCES " + Table_Exercise + "(_id))";
            db.execSQL(CREATE_TABLE_RELATION);
            db.execSQL(CREATE__TABLE_UPDATES);
            ContentValues v = new ContentValues();
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateandTime = sdf.format(currentTime);
            v.put("lastUpdate",currentDateandTime);
            v.put("firstInstalation",1);
            db.insert(Table_Updates,null,v);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + Table_Exercise);
            db.execSQL("DROP TABLE IF EXISTS " + Table_Routine);
            db.execSQL("DROP TABLE IF EXISTS " + Table_ExOfRoutine);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public GymnasioDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the GymnasIOApp database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public GymnasioDBAdapter open() throws SQLException {
        DbHelper = new DatabaseHelper(mCtx);
        Db = DbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DbHelper.close();
    }


    public Cursor checkForUpdates() {
        return Db.query(Table_Updates, new String[]{"_id","lastUpdate","firstInstalation"},
                null,null,null,null,null);
    }
    /**
     * Updates de lastUpdates date when the app updates itself.
     *
     * @param id the id of the row
     * @return exId or -1 if failed
     */
    public long updateLastUpdate(int id, String lU) {
        ContentValues v = new ContentValues();
        v.put("lastUpdate",lU);
        v.put("firstInstalation",0);
        Log.d("DBUpdate", "Updating the last update date on database with value: "+lU);
        return Db.update(Table_Updates,v,"_id ="+id,null);
    }
    /**
     * Return a Cursor over the list of all exercises in the database
     *
     * @return Cursor over all exercises.
     */
    public Cursor fetchExercises() {
        return Db.query(Table_Exercise, EX_ROWS
                , null, null, null, null, KEY_EX_NAME);
    }
    /**
     * Create a new Exercise using the object provided. If the exercise is
     * successfully created return the new exId for that exercies, otherwise return
     * a -1 to indicate failure.
     *
     * @param e the object which contains the exercise
     * @return exId or -1 if failed
     */
    public long createExercise(Exercise e) {
        ContentValues v = new ContentValues();
        v.put(KEY_EX_NAME, e.getName());
        v.put(KEY_EX_MUSCLE, e.getMuscle());
        v.put(KEY_EX_DESC, e.getDescription());
        v.put(KEY_EX_IMG, e.getImage());
        String tags="";
        if (e.getTags()!=null){
            for (String s: e.getTags()) {
                tags += "#"+s+",";
            }
            v.put(KEY_EX_TAG, tags);
        }
        Log.d("DBInsertion", "Inserting exercise to database");
        return Db.insert(Table_Exercise, null, v);
    }
    /**
     * Delete the exercise with the given rowId
     *
     * @param rowId id of exercise to delete
     * @return true if deleted, false otherwise
     *
     * public boolean deleteExercise(long rowId) {
     * return Db.delete(Table_Exercise, KEY_EX_ID + "=" + rowId, null) > 0;
     * }
     */
    /**
     * Return a Cursor positioned at the exercise that matches the given rowId
     *
     * @param rowId id of exercise to retrieve
     * @return Cursor positioned to matching exercise, if found
     * @throws SQLException if exercise could not be found/retrieved
     */
    public Cursor fetchExercise(long rowId) throws SQLException {

        Cursor mCursor =

                Db.query(true, Table_Exercise, new String[]{KEY_EX_ID, KEY_EX_NAME, KEY_EX_DESC,KEY_EX_MUSCLE,
                                KEY_EX_IMG, KEY_EX_TAG}, KEY_EX_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    /**
     * Return a Cursor positioned at the exercise that matches the given name
     *
     * @param name name of exercise to retrieve
     * @return Cursor positioned to matching exercise, if found
     * @throws SQLException if exercise could not be found/retrieved
     */
    public Cursor getExerciseByName(String name) throws SQLException {
        String[] consulta = {name};
        Cursor mCursor;
        mCursor = Db.query( Table_Exercise, new String[]{KEY_EX_ID, KEY_EX_NAME, KEY_EX_DESC,KEY_EX_MUSCLE,
                        KEY_EX_IMG, KEY_EX_TAG}, KEY_EX_NAME+"='"+name+"'", null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    /**
     * Return a Cursor positioned at the exercise that matches the given muscle
     *
     * @param muscle id of exercise to retrieve
     * @return Cursor positioned to matching exercise, if found
     * @throws SQLException if exercise could not be found/retrieved
     */
    public Cursor getExercisesByMuscle(String muscle) throws SQLException {

        Cursor mCursor =

                Db.query(true, Table_Exercise, new String[]{KEY_EX_ID, KEY_EX_NAME, KEY_EX_DESC,KEY_EX_MUSCLE,
                                KEY_EX_IMG,  KEY_EX_TAG}, KEY_EX_MUSCLE + "='" + muscle+"'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    /**
     * Return a Cursor positioned at the exercise that matches the given tag
     *
     * @param tag tag of exercise to retrieve
     * @return Cursor positioned to matching exercise, if found
     * @throws SQLException if exercise could not be found/retrieved
     */
    public Cursor getExercisesByTag(String tag) throws SQLException {
        //THIS WILL DO SOMETHING WHEN I KNOW HOW TO DO IT.
        Cursor mCursor = Db.rawQuery("SELECT * FROM " + Table_Exercise + " WHERE " + KEY_EX_TAG
                + " LIKE '%#" + tag + ",%';", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    /**
     * Create a new Freemium Routine using the object provided. If the routine is
     * successfully created return the new roId for that routine, otherwise return
     * a -1 to indicate failure.
     *
     * @param r the object which contains the routine
     * @return exId or -1 if failed
     */
    public long createFreemiumRoutine(Routine r) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_S, r.getSeries());
        v.put(KEY_RO_RT, r.getRelxTime());
        v.put(KEY_RO_R, r.getRep());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, false);
        ArrayList<Long> ex = r.getExercises();
        //Introducimos la rutina
        long id = Db.insert(Table_Routine,null,v);
        //Añadimos los ejercicios de la rutina
        for (long ejId : ex) {
            ContentValues v2 = new ContentValues();
            v2.put(KEY_EXRO_IDR,id);
            v2.put(KEY_EXRO_IDE,ejId);
            Db.insert(Table_ExOfRoutine,null,v2);
        }
        Log.d("DBInsertion", "Inserting Freemium routine to database");
        return id;
    }
    /**
     * Create a new Freemium Routine using the object provided. If the routine is
     * successfully created return the new roId for that routine, otherwise return
     * a -1 to indicate failure.
     *
     * @param r the object which contains the routine
     * @return exId or -1 if failed
     */
    public long createPremiumRoutine(Routine r) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_GYM, r.getNameGym());
        v.put(KEY_RO_S, r.getSeries());
        v.put(KEY_RO_RT, r.getRelxTime());
        v.put(KEY_RO_R, r.getRep());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, true);
        ArrayList<Long> ex = r.getExercises();
        //Introducimos la rutina
        long id = Db.insert(Table_Routine,null,v);
        //Añadimos los ejercicios de la rutina
        for (long ejId : ex) {
            Db.execSQL("INSERT INTO " + Table_ExOfRoutine + " VALUES (" + id +  "," + ejId + ")");
        }
        Log.d("DBInsertion", "Inserting Premium routine to database");
        return id;
    }

    /**
     * Update a new Freemium Routine using the object provided. If the routine is
     * successfully created return the new roId for that routine, otherwise return
     * a -1 to indicate failure.
     *
     * @param r the object which contains the routine
     * @param id id of the routine to be updated
     * @return true if success or false if failure
     */
    public boolean updateFreemiumRoutine(long id, Routine r) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_GYM, r.getNameGym());
        v.put(KEY_RO_S, r.getSeries());
        v.put(KEY_RO_RT, r.getRelxTime());
        v.put(KEY_RO_R, r.getRep());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, false);
        ArrayList<Long> ex = r.getExercises();
        boolean updateRo = Db.update(Table_Routine, v, KEY_RO_ID + "=" + id, null) > 0;
        Db.delete(Table_ExOfRoutine,KEY_EXRO_IDR+"="+id,null);
        if (ex != null) {
            for (long ejId : ex) {
                ContentValues v2 = new ContentValues();
                v2.put(KEY_EXRO_IDR,id);
                v2.put(KEY_EXRO_IDE,ejId);
                Db.insert(Table_ExOfRoutine,null,v2);
            }
        }
        return updateRo;
    }
    /**
     * Update a new Freemium Routine using the object provided. If the routine is
     * successfully created return the new roId for that routine, otherwise return
     * a -1 to indicate failure.
     *
     * @param r the object which contains the routine
     * @param id id of the routine to be updated
     * @return true if success or false if failure
     */
    public boolean updatePremiumRoutine(long id, Routine r) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_GYM, r.getNameGym());
        v.put(KEY_RO_S, r.getSeries());
        v.put(KEY_RO_RT, r.getRelxTime());
        v.put(KEY_RO_R, r.getRep());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, true);
        ArrayList<Long> ex = r.getExercises();
        boolean updateRo = Db.update(Table_Routine, v, KEY_RO_ID + "=" + id, null) > 0;
        Db.delete(Table_ExOfRoutine,"idRut="+id,null);
        for (long ejId: ex) {
            Db.execSQL("INSERT INTO " +Table_ExOfRoutine + " VALUES (" + id +  "," + ejId + ")");
        }
        return updateRo;
    }



    /**
     * Return a Cursor over the list of all routines in the database
     *
     * @return Cursor over all routines.
     */
    public Cursor fetchRoutines() {
        return Db.query(Table_Routine, RO_ROWS, null,
                        null, null, null, null);
    }

    /**
     * Returns the number of routines that exist in the database
     *
     * @return Number of routines.
     */
    public int getNumberOfRoutines() {
         Cursor c =
                 Db.query(Table_Routine, new String[]{KEY_RO_ID, KEY_RO_S, KEY_RO_RT, KEY_RO_R,
                                 KEY_RO_OBJ,KEY_RO_NAME,KEY_RO_PREMIUM,KEY_RO_GYM}, null,
                         null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getCount();
    }

    /**
     * Return a Cursor positioned at the routine that matches the given rowId
     *
     * @param rowId id of routine to retrieve
     * @return Cursor positioned to matching routine, if found
     * @throws SQLException if routine could not be found/retrieved
     */
    public Cursor fetchRoutine(long rowId) throws SQLException {

        Cursor mCursor =

                Db.query(Table_Routine, new String[]{KEY_RO_ID, KEY_RO_S, KEY_RO_RT, KEY_RO_R,
                                KEY_RO_OBJ,KEY_RO_NAME,KEY_RO_PREMIUM,KEY_RO_GYM}, KEY_RO_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public boolean deleteRoutine(long rowId) throws SQLException {
        return Db.delete(Table_Routine, KEY_RO_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor positioned at the routine that matches the given name
     *
     * @param name name of exercise to retrieve
     * @return Cursor positioned to matching routine, if found
     * @throws SQLException if exercise could not be found/retrieved
     */
    public Cursor getRoutineByName(String name) throws SQLException {

        Cursor mCursor =

                Db.query(Table_Routine, new String[]{KEY_RO_ID, KEY_RO_S, KEY_RO_RT, KEY_RO_R,
                                KEY_RO_OBJ,KEY_RO_NAME,KEY_RO_PREMIUM,KEY_RO_GYM}, KEY_RO_NAME + "='" + name+"'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public boolean deleteExercise (long id) {
        return Db.delete(Table_Exercise,KEY_EX_ID+"="+id,null)>0;
    }
    public Cursor getExercisesFromRoutine(long id) {
        String selectQuery = "SELECT * FROM "+ Table_Exercise+" ex, "+Table_Routine+" ro, "+
                Table_ExOfRoutine+" exro WHERE ro."+KEY_RO_ID+"="+id+" AND exro."+KEY_RO_ID+" AND ex."
                + KEY_EX_ID + "= exro."+KEY_EX_ID;
        Log.w("TAG",selectQuery);
        Cursor c = Db.rawQuery(selectQuery,null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            return c;
        } else {
            return null;
        }
    }
}
