package com.patan.gimnasio.services;


import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.domain.DBRData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handler for API methods. A facade of sorts for the remote API.
 */
public class ApiHandler {


    private final String urlDb = "http://54.171.225.70:32001/dbdata/";
    private final String urlLogin = "http://54.171.225.70:32001/gym/login";

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
}

