package com.patan.gimnasio.services;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.domain.DBRData;
import com.patan.gimnasio.domain.ExFromRoutine;
import com.patan.gimnasio.domain.Routine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

/**
 * Handler for API methods. A facade of sorts for the remote API.
 */
public class ApiHandler {

    private final String urlDb = "http://54.171.225.70:32001/dbdata/";
    private final String urlLogin = "http://54.171.225.70:32001/gym/login";
    private final String urlUpdate ="http://54.171.225.70:32001/exercises/";
    private final String urlImg = "http://54.171.225.70:32001/exercises/download";
    private final String urlRoutine = "http://54.171.225.70:32001/routines/";

    private final String u = "gpsAdmin";
    private final String p = "Gps@1718";

    private GymnasioDBAdapter db;

    public ApiHandler(Context mCtx) {
        db = new GymnasioDBAdapter(mCtx);
    }

    public boolean loginPremium (String nameGym, String key) {
        db.open();
        boolean login = false;
        ANRequest request = AndroidNetworking.get(urlLogin)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addHeaders("namegym", nameGym)
                .addHeaders("key", key)
                .build();
        ANResponse<JSONObject> response = request.executeForJSONObject();
        JSONObject respuesta = response.getResult();
        if (response.isSuccess()) {
            try {
                boolean exito = respuesta.getBoolean("success");
                if(exito) {
                    String type = respuesta.getString("type");
                    if (type.equals("user")) {
                        login = true;
                        db.loginAsUser(nameGym,key);
                        Log.d("LogPr", "Logged as normal user of gym: " + nameGym);
                    } else if (type.equals("admin")) {
                        login = true;
                        db.loginAsAdmin(nameGym,key);
                        Log.d("LogPr", "Logged as admin of gym: " + nameGym);
                    } else {
                        login = false;
                        Log.d("LogPr", "Not Logged in gym: " + nameGym);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ANError error = response.getError();
            login = false;
            // Handle Error
            Log.e("Premium", error.getErrorBody());
        }
        db.close();
        return login;
    }

    public DBRData dbData() {
        ANRequest request = AndroidNetworking.get(urlDb)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .build();
        ANResponse<JSONObject> response = request.executeForJSONObject();
        JSONObject respuesta = response.getResult();
        Log.d("DBData", response.toString());
        if (response.isSuccess()) {
            try {
                double imageSize = respuesta.getDouble("imageSize");
                double dataSize = respuesta.getDouble("dataSize");
                double totalSize = respuesta.getDouble("totalSize");
                String lastUpdate = respuesta.getString("lastUpdate");
                DBRData res = new DBRData(imageSize,dataSize,totalSize,lastUpdate);
                return res;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            ANError error = response.getError();
            // Handle Error
            Log.e("Premium", error.getErrorBody());
            return null;
        }
    }

    public JSONObject updateDB() {
        ANRequest request = AndroidNetworking.get(urlUpdate)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .build();
        ANResponse<JSONObject> response = request.executeForJSONObject();

        Log.d("DBData", response.toString());
        if (response.isSuccess()) {
            return response.getResult();
        } else {
            ANError error = response.getError();
            // Handle Error
            Log.e("Premium", error.getErrorBody());
            return null;
        }
    }

    public JSONObject updatePremiumDB(String nameGym, String key) {
        ANRequest request = AndroidNetworking.get(urlRoutine)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addHeaders("key",key)
                .addHeaders("nameGym",nameGym)
                .build();
        ANResponse<JSONObject> response = request.executeForJSONObject();

        //Log.d("UpdPre", response.toString());
        if (response.isSuccess()) {
            return response.getResult();
        } else {
            ANError error = response.getError();
            // Handle Error
            //Log.e("Premium", error.getErrorBody());
            return null;
        }
    }

    public Bitmap downloadIMG(String imgName) {
        ANRequest request = AndroidNetworking.get(urlImg)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addHeaders("image", remove1(imgName)+".jpg")
                .addHeaders("Content-type", "application/json;charset=utf-8")
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        ANResponse<Bitmap> response = request.executeForBitmap();
        Log.d("ImgDwn","Trying to download " + imgName);
        if (response.isSuccess()) {
            Log.d("ImgDwn","Image from " + imgName + " downloaded");
            Bitmap res = (Bitmap) response.getResult();
            return res;
        } else {
            ANError error = response.getError();
            // Handle Error
            Log.e("ImgError", error.getErrorBody());
            return null;
        }
    }
    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input: Cadena de entrada
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    private static String remove1(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }


    /**
     * Función que sirve de pasarela enter la aplicacion y el servidor para la creacion de rutinas
     * @param r
     * @param exercises
     * @return
     */
    public JSONObject createPremiumRoutine(Routine r, ArrayList<ExFromRoutine> exercises) {
        String urlNewRoutine = urlRoutine + "newRoutine";
        db.open();
        Cursor c = db.getLoginData();
        String key = "";
        if (c != null) {
            key = c.getString(c.getColumnIndex("key"));
        }
        String nameGym = r.getNameGym();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> repetitions = new ArrayList<>();
        ArrayList<Integer> series = new ArrayList<>();
        ArrayList<Double> rT = new ArrayList<>();
        for (ExFromRoutine exercise : exercises) {
            names.add(db.getExerciseNameById(exercise.getId()));
            repetitions.add(exercise.getRep());
            series.add(exercise.getSeries());
            rT.add(exercise.getRelxTime());
        }

        JSONObject json = new JSONObject();
        JSONArray jNames = new JSONArray(names);
        JSONArray jRepetitions = new JSONArray(repetitions);
        JSONArray jSeries = new JSONArray(series);
        JSONArray jRT = new JSONArray(rT);

        try {
            json.put("name", r.getName());
            json.put("objective", r.getObjective());
            json.put("exercises", jNames);
            json.put("repetitions", jRepetitions);
            json.put("series", jSeries);
            json.put("relaxTime", jRT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("CrtPrRou", json.toString());
        ANRequest request = AndroidNetworking.post(urlNewRoutine)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addHeaders("nameGym", nameGym)
                .addHeaders("key", key)
                .addJSONObjectBody(json)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            boolean res = false;
            try {
                Log.d("CrtPrRou", jsonObject.toString());
                res = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (res) return jsonObject;
        } else {
            ANError error = response.getError();
            Log.e("CrtPrRou", error.getErrorBody().toString());
            // Handle Error
        }
        db.close();
        return null;
    }

    /**
     * Función que sirve de pasarela enter la aplicacion y el servidor para la actualización de rutinas
     * @param r
     * @param exercises
     * @return
     */
    public JSONObject updatePremiumRoutine(String idR, Routine r, ArrayList<ExFromRoutine> exercises) {
        String urlUpdRoutine = urlRoutine + "update";
        db.open();
        Cursor c = db.getLoginData();
        String key = "";
        if (c != null) {
            key = c.getString(c.getColumnIndex("key"));
        }
        String nameGym = r.getNameGym();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> repetitions = new ArrayList<>();
        ArrayList<Integer> series = new ArrayList<>();
        ArrayList<Double> rT = new ArrayList<>();
        for (ExFromRoutine exercise : exercises) {
            names.add(db.getExerciseNameById(exercise.getId()));
            repetitions.add(exercise.getRep());
            series.add(exercise.getSeries());
            rT.add(exercise.getRelxTime());
        }
        JSONObject json = new JSONObject();

        JSONArray jNames = new JSONArray(names);
        JSONArray jRepetitions = new JSONArray(repetitions);
        JSONArray jSeries = new JSONArray(series);
        JSONArray jRT = new JSONArray(rT);

        try {
            json.put("name", r.getName());
            json.put("objective", r.getObjective());
            json.put("exercises", jNames);
            json.put("repetitions", jRepetitions);
            json.put("series", jSeries);
            json.put("relaxTime", jRT);
            json.put("id", idR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("UpdPrRou", json.toString());
        ANRequest request = AndroidNetworking.put(urlUpdRoutine)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addHeaders("nameGym", nameGym)
                .addHeaders("key", key)
                .addJSONObjectBody(json)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();


        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            Log.d("UpdPrRou", jsonObject.toString());
            boolean res = false;
            try {
                res = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (res) return jsonObject;
        } else {
            ANError error = response.getError();
            Log.e("UpdPrRou", error.getErrorBody().toString());
            // Handle Error
        }
        db.close();
        return null;

    }

    /**
     * Función que sirve de pasarela enter la aplicacion y el servidor para el borrado de rutinas
     * @param idR
     * @return
     */
    public boolean deletePremiumRoutine(String idR) {
        db.open();
        Cursor c = db.getLoginData();
        String key ="";
        String gymName ="";
        if (c!=null){
            key = c.getString(c.getColumnIndex(GymnasioDBAdapter.KEY_GYM_KEY));
            gymName = c.getString(c.getColumnIndex( GymnasioDBAdapter.KEY_GYM_NAME));
        }
        String urlNewRoutine = urlRoutine + "delete";

        JSONObject json = new JSONObject();
        try {
            json.put("id", idR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("PETICION", json.toString());
        ANRequest request = AndroidNetworking.delete(urlNewRoutine)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addHeaders("nameGym", gymName)
                .addHeaders("key",key)
                .addJSONObjectBody(json)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            Log.d("DelPrRou", jsonObject.toString());
            boolean res = false;
            try {
                res = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (res) return true;
        } else {
            ANError error = response.getError();
            Log.e("DelPrRou", error.getErrorBody().toString());
            // Handle Error
        }
        db.close();
        return false;
    }
}

