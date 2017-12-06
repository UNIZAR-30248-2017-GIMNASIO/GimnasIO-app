package com.patan.gimnasio.services;


import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.patan.gimnasio.database.GymnasioDBAdapter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handler for API methods. A facade of sorts for the remote API.
 */
public class ApiHandler {
    private final String url = "http://54.171.225.70:32001/gym/login";;
    private GymnasioDBAdapter db;

    public ApiHandler(Context mCtx) {
        db = new GymnasioDBAdapter(mCtx);
    }

    public boolean loginPremium (String nameGym, String key) {
        db.open();
        boolean login = false;
        ANRequest request1 = AndroidNetworking.get(url)
                .addHeaders("user", "gpsAdmin")
                .addHeaders("pwd", "Gps@1718")
                .addHeaders("namegym", nameGym)
                .addHeaders("key", key)
                .build();

        ANResponse<JSONObject> response = request1.executeForJSONObject();
        JSONObject respuesta = response.getResult();

        if (response.isSuccess()) {
            try {
                boolean exito = respuesta.getBoolean("success");
                if(exito) {
                    String type = respuesta.getString("type");
                    if (type.equals("user")) {
                        login = true;
                        db.loginAsUser(nameGym);
                    } else if (type.equals("admin")) {
                        login = true;
                        db.loginAsAdmin(nameGym);
                    } else {
                        login = false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ANError error = response.getError();
            login = false;
            // Handle Error
        }
        db.close();
        return login;
    }

}
