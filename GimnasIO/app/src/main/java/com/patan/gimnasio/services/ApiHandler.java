package com.patan.gimnasio.services;


import android.content.Context;
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
                        db.loginAsUser(nameGym);
                        Log.d("Premium", "Logged as normal user of gym: " + nameGym);
                    } else if (type.equals("admin")) {
                        login = true;
                        db.loginAsAdmin(nameGym);
                        Log.d("Premium", "Logged as admin of gym: " + nameGym);
                    } else {
                        login = false;
                        Log.d("Premium", "Not Logged in gym: " + nameGym);
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

    public DBRData dbData(String lastUpdateLocal, int firstInstallation) {
        ANRequest request = AndroidNetworking.get(urlDb)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .build();
        ANResponse<JSONObject> response = request.executeForJSONObject();
        JSONObject respuesta = response.getResult();
        Log.w("DBData", response.toString());
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

        Log.w("DBData", response.toString());
        if (response.isSuccess()) {
            return response.getResult();
        } else {
            ANError error = response.getError();
            // Handle Error
            Log.e("Premium", error.getErrorBody());
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
     * @param input
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
     * This API method sends to server a request for creating a new Premium Routine.
     * @param r
     * @param exercises
     */
    public boolean createPremiumRoutine(Routine r, ArrayList<ExFromRoutine> exercises) {
        String urlNewRoutine = urlRoutine + "newRoutine";
        String[] names = new String[exercises.size()];
        int[] repetitions = new int[exercises.size()];
        int[] series = new int[exercises.size()];
        double[] rT = new double[exercises.size()];
        int i = 0;
        for (ExFromRoutine exercise: exercises){
            names[i] = db.getExerciseNameById(exercise.getId());
            repetitions[i] = exercise.getRep();
            series[i] = exercise.getSeries();
            rT[i] = exercise.getRelxTime();
            i++;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("name",r.getName());
            json.put("nameGym", r.getNameGym());
            json.put("objective",r.getObjective());
            json.put("exercises", names);
            json.put("repetitions",repetitions);
            json.put("series",series);
            json.put("relaxTime",rT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ANRequest request = AndroidNetworking.post(urlNewRoutine)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addJSONObjectBody(json)
                .build();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            boolean res = false;
            try {
                res = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (res)return true;
        } else {
            ANError error = response.getError();
            // Handle Error
        }
        return false;
    }

    public boolean updatePremiumRoutine(Routine r, ArrayList<ExFromRoutine> exercises) {
        String urlNewRoutine = urlRoutine + "update";
        String[] names = new String[exercises.size()];
        int[] repetitions = new int[exercises.size()];
        int[] series = new int[exercises.size()];
        double[] rT = new double[exercises.size()];
        int i = 0;
        for (ExFromRoutine exercise: exercises){
            names[i] = db.getExerciseNameById(exercise.getId());
            repetitions[i] = exercise.getRep();
            series[i] = exercise.getSeries();
            rT[i] = exercise.getRelxTime();
            i++;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("name",r.getName());
            json.put("nameGym", r.getNameGym());
            json.put("objective",r.getObjective());
            json.put("exercises", names);
            json.put("repetitions",repetitions);
            json.put("series",series);
            json.put("relaxTime",rT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ANRequest request = AndroidNetworking.post(urlNewRoutine)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addJSONObjectBody(json)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            boolean res = false;
            try {
                res = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (res)return true;
        } else {
            ANError error = response.getError();
            // Handle Error
        }
        return false;
    }

    public boolean deletePremiumRoutine(long id) {
        String name = db.getRoutineNameById(id);
        String urlNewRoutine = urlRoutine + "deleteRoutine";
        ANRequest request = AndroidNetworking.post(urlNewRoutine)
                .addHeaders("user", u)
                .addHeaders("pwd", p)
                .addBodyParameter("name", name)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            boolean res = false;
            try {
                res = jsonObject.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (res) return true;
        } else {
            ANError error = response.getError();
            // Handle Error
        }
        return false;
    }
}

