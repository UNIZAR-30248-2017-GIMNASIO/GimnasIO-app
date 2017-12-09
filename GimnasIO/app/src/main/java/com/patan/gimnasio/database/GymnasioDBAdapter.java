package com.patan.gimnasio.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.patan.gimnasio.domain.ExFromRoutine;
import com.patan.gimnasio.domain.Exercise;
import com.patan.gimnasio.domain.Routine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GymnasioDBAdapter {

    private static final String TAG = "GymnasioDbAdapter";
    private DatabaseHelper DbHelper;
    private SQLiteDatabase Db;

    private static final int DATABASE_VERSION = 30;
    private static final String DATABASE_NAME = "GymnasIOapp.db";
    private static final String Table_Routine = "Routine";
    private static final String Table_Exercise = "Exercise";
    private static final String Table_ExOfRoutine = "ExOfRoutine";
    private static final String Table_Updates = "Updates";
    private static final String Table_Gyms = "Gyms";

    public static final String KEY_EX_NAME = "name";
    public static final String KEY_EX_MUSCLE = "muscle";
    public static final String KEY_EX_DESC = "description";
    public static final String KEY_EX_IMG = "image";
    public static final String KEY_EX_ID = "_id";
    public static final String KEY_EX_TAG = "tags";

    public static final String KEY_RO_ID= "_id";
    public static final String KEY_RO_NAME = "name";

    public static final String KEY_RO_PREMIUM = "premium";
    public static final String KEY_RO_GYM = "gym";
    public static final String KEY_RO_OBJ = "objective";

    public static final String KEY_EXRO_ID = "_id";
    public static final String KEY_EXRO_IDR = KEY_RO_ID+"R";
    public static final String KEY_EXRO_IDE = KEY_EX_ID+"E";
    public static final String KEY_EXRO_EXREP = "rep";
    public static final String KEY_EXRO_EXSER = "series";
    public static final String KEY_EXRO_EXRT = "relaxTime";

    public static final String KEY_GYM_ID = "_id";
    public static final String KEY_GYM_NAME = "nameGym";
    public static final String KEY_GYM_TYPE = "type";

    private static final String CREATE_TABLE_ROUTINES = "CREATE TABLE IF NOT EXISTS " + Table_Routine +
            " ("+ KEY_RO_ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"+ KEY_RO_OBJ +" VARCHAR(20)"
            + " not null,"+ KEY_RO_NAME +" VARCHAR(20) not null,"+KEY_RO_PREMIUM
            + " INT not null,"+ KEY_RO_GYM +" gym varchar(20))";
    private static final String CREATE_TABLE_EXERCISES="CREATE TABLE IF NOT EXISTS " + Table_Exercise +
            " (" + KEY_EX_ID + " integer primary key autoincrement," + KEY_EX_NAME + " VARCHAR(20) not null," +
    KEY_EX_DESC + " not null," + KEY_EX_MUSCLE + " VARCHAR(20) not null, " + KEY_EX_IMG +
            " varchar(20) not null, "+ KEY_EX_TAG +" varchar(100))";
    private static final String CREATE_TABLE_RELATIONEXRO = "CREATE TABLE IF NOT EXISTS "
            + Table_ExOfRoutine + "("+KEY_EXRO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_EXRO_IDR + " INTEGER," + KEY_EXRO_IDE + " INTEGER,"+KEY_EXRO_EXREP+" INTEGER not null,"
            + KEY_EXRO_EXSER + " INTEGER not null," + KEY_EXRO_EXRT + " double not null)";
    private static final String CREATE_TABLE_UPDATES="CREATE TABLE IF NOT EXISTS " + Table_Updates +
            " ( _id integer primary key autoincrement , lastUpdate VARCHAR(20) not null, firstInstalation int not null);";
    private static final String CREATE_TABLE_GYMS = "CREATE TABLE IF NOT EXISTS " + Table_Gyms +
            " ( "+KEY_GYM_ID+" integer primary key autoincrement,"+KEY_GYM_NAME+" VARCHAR(20) not null,"
            +KEY_GYM_TYPE+" VARCHAR(5) not null)";


    private static final String[] RO_ROWS={KEY_RO_ID,KEY_RO_OBJ,KEY_RO_NAME,KEY_RO_PREMIUM,
            KEY_RO_GYM};
    private static final String[] EX_ROWS=new String[]{KEY_EX_ID, KEY_EX_NAME, KEY_EX_DESC,
            KEY_EX_MUSCLE, KEY_EX_IMG, KEY_EX_TAG};
    private static final String[] GY_ROWS= new String[] {KEY_GYM_ID,KEY_GYM_NAME,KEY_GYM_TYPE};

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {


        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ROUTINES);
            db.execSQL(CREATE_TABLE_EXERCISES);
            db.execSQL(CREATE_TABLE_GYMS);
            db.execSQL(CREATE_TABLE_RELATIONEXRO);
            db.execSQL(CREATE_TABLE_UPDATES);
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
            db.execSQL("DROP TABLE IF EXISTS " + Table_Gyms);
            db.execSQL("DROP TABLE IF EXISTS " + Table_Updates);
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
        Cursor c = this.getExerciseByName(e.getName());
        if (c.getCount() == 0) {
            Log.d("TAG", "Insertando "+e.getName());
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
        } else {
            Log.d("TAG", "Actualizando "+e.getName());
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
            return Db.update(Table_Exercise,v, KEY_EX_ID + "=" + c.getLong(c.getColumnIndex(KEY_EX_ID)),null);
        }
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
        Cursor mCursor = Db.rawQuery("SELECT * FROM " + Table_Exercise + " WHERE " + KEY_EX_NAME
                + " LIKE '" + name + "%';", null);
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
        Cursor mCursor = Db.rawQuery("SELECT * FROM " + Table_Exercise + " WHERE " + KEY_EX_MUSCLE
                + " LIKE '" + muscle + "%';", null);
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
    public long createFreemiumRoutine(Routine r, ArrayList<ExFromRoutine> ex) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_GYM, r.getNameGym());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, false);
        //Introducimos la rutina
        long id = Db.insert(Table_Routine,null,v);
        //Añadimos los ejercicios de la rutina
        for (ExFromRoutine e : ex) {
            ContentValues v2 = new ContentValues();
            v2.put(KEY_EXRO_IDR,id);
            v2.put(KEY_EXRO_IDE,e.getId());
            v2.put(KEY_EXRO_EXSER, e.getSeries());
            v2.put(KEY_EXRO_EXRT, e.getRelxTime());
            v2.put(KEY_EXRO_EXREP, e.getRep());
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
    public long createPremiumRoutine(Routine r, ArrayList<ExFromRoutine> ex) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_GYM, r.getNameGym());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, true);
        //Introducimos la rutina
        long id = Db.insert(Table_Routine,null,v);
        //Añadimos los ejercicios de la rutina
        for (ExFromRoutine e : ex) {
            ContentValues v2 = new ContentValues();
            v2.put(KEY_EXRO_IDR,id);
            v2.put(KEY_EXRO_IDE,e.getId());
            v2.put(KEY_EXRO_EXSER, e.getSeries());
            v2.put(KEY_EXRO_EXRT, e.getRelxTime());
            v2.put(KEY_EXRO_EXREP, e.getRep());
            Db.insert(Table_ExOfRoutine,null,v2);
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
    public boolean updateFreemiumRoutine(long id, Routine r, ArrayList<ExFromRoutine> ex) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_GYM, r.getNameGym());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, false);
        boolean updateRo = Db.update(Table_Routine, v, KEY_RO_ID + "=" + id, null) > 0;
        Db.delete(Table_ExOfRoutine,KEY_EXRO_IDR+"=" + id,null);
        if (ex != null || ex.size() != 0) {
            for (ExFromRoutine e : ex) {
                ContentValues v2 = new ContentValues();
                v2.put(KEY_EXRO_IDR,id);
                v2.put(KEY_EXRO_IDE,e.getId());
                v2.put(KEY_EXRO_EXSER, e.getSeries());
                v2.put(KEY_EXRO_EXREP, e.getRep());
                v2.put(KEY_EXRO_EXRT, e.getRelxTime());
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
    public boolean updatePremiumRoutine(long id, Routine r, ArrayList<ExFromRoutine> ex) {
        ContentValues v = new ContentValues();
        v.put(KEY_RO_NAME, r.getName());
        v.put(KEY_RO_GYM, r.getNameGym());
        v.put(KEY_RO_OBJ, r.getObjective());
        v.put(KEY_RO_PREMIUM, true);
        boolean updateRo = Db.update(Table_Routine, v, KEY_RO_ID + "=" + id, null) > 0;
        Db.delete(Table_ExOfRoutine,KEY_EXRO_IDR+"="+id,null);
        if (ex != null || ex.size() != 0) {
            for (ExFromRoutine e : ex) {
                ContentValues v2 = new ContentValues();
                v2.put(KEY_EXRO_IDR,id);
                v2.put(KEY_EXRO_IDE,e.getId());
                v2.put(KEY_EXRO_EXSER, e.getSeries());
                v2.put(KEY_EXRO_EXRT, e.getRelxTime());
                v2.put(KEY_EXRO_EXREP, e.getRep());
                Db.insert(Table_ExOfRoutine,null,v2);
            }
        }
        return updateRo;
    }

    /**
     * Return a Cursor over the list of all freemium routines in the database
     *
     * @return Cursor over all freemium routines.
     */
    public Cursor fetchFreemiumRoutines() {
        return Db.query(Table_Routine, RO_ROWS, KEY_RO_PREMIUM + "=" + 0, null,
                null, null, null, null);
    }

    /**
     * Return a Cursor over the list of all premium routines in the database of the concrete Gym
     *
     * @return Cursor over all premium routines concrete Gym
     */
    public Cursor fetchPremiumRoutines(String gym_name) {
        return Db.query(Table_Routine, RO_ROWS, KEY_RO_PREMIUM + "=" + 1 + " AND " + KEY_RO_GYM + "=" + "'"+ gym_name +"'", null,
                null, null, null, null);
    }


    /**
     * Returns the number of routines that exist in the database
     *
     * @return Number of routines.
     */
    public int getNumberOfRoutines() {
         Cursor c =
                 Db.query(Table_Routine, RO_ROWS, null,
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

                Db.query(Table_Routine, RO_ROWS, KEY_RO_ID + "=" + rowId, null,
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
        Cursor mCursor = Db.rawQuery("SELECT * FROM " + Table_Routine + " WHERE " + KEY_RO_NAME
                + " LIKE '" + name + "%';", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }



    /**
     * Return a Cursor positioned at the routines which match the given objetive
     *
     * @param obj objective of exercises to retrieve
     * @return Cursor positioned to matching routine, if found
     * @throws SQLException if exercise could not be found/retrieved
     */
    public Cursor getRoutineByObj(String obj) throws SQLException {
        Cursor mCursor = Db.rawQuery("SELECT * FROM " + Table_Routine + " WHERE " + KEY_RO_OBJ
                + " LIKE '" + obj + "%';", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public Cursor getExercisesFromRoutine(long id) {
        String selectQuery = "SELECT * FROM "+ Table_Exercise+" AS ex, "+
                Table_ExOfRoutine+" AS exro WHERE exro."+KEY_EXRO_IDR+"="+id+" AND exro."
                + KEY_EXRO_IDE + "= ex."+KEY_EX_ID;
        Log.w("TAG",selectQuery);
        Cursor c = Db.rawQuery(selectQuery,null);
        if (c.moveToFirst()) {
            return c;
        } else {
            return null;
        }
    }

    public boolean deleteExercise (long id) {
        return Db.delete(Table_Exercise,KEY_EX_ID+"="+id,null)>0;
    }

    public boolean logged () {
        boolean logged = false;
        Cursor mCursor  = Db.query(Table_Gyms, GY_ROWS, null,null,
                null,null,null);
        if (mCursor.getCount() != 0 ) {
          logged = true;
        }
        Log.w("Login", String.valueOf(logged));
        return logged;
    }
    public long loginAsUser(String nameGym) {
        if (!this.logged()) {
            Log.d("TAG", "Insertando " + nameGym);
            ContentValues v = new ContentValues();
            v.put(KEY_GYM_NAME, nameGym);
            v.put(KEY_GYM_TYPE, "user");
            Log.d("DBInsertion", "Inserting gym to database");
            return Db.insert(Table_Gyms, null, v);
        } else return -1;
    }
    public long loginAsAdmin(String nameGym){
        if (!this.logged()) {
            Log.d("TAG", "Insertando " + nameGym);
            ContentValues v = new ContentValues();
            v.put(KEY_GYM_NAME, nameGym);
            v.put(KEY_GYM_TYPE, "admin");
            Log.d("DBInsertion", "Inserting gym to database");
            return Db.insert(Table_Gyms, null, v);
        } else return -1;
    }

    public boolean logout() {
        return Db.delete(Table_Gyms,null,null)>0;
    }


    public Cursor getLoginData() {
        Cursor c =  Db.query(Table_Gyms, GY_ROWS, null,null,
                null,null,null);
        if (c.getCount() != 0 ) {
            Log.d("INSIDE", "");
        }
        if (c.moveToFirst()) {
            return c;
        } else {
            return null;
        }
    }
}
