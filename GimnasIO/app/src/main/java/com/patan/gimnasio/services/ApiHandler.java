package com.patan.gimnasio.services;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.patan.gimnasio.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for API methods. A facade of sorts for the remote API.
 */
public class ApiHandler {

    public JsonObjectRequest getDbData(String url, final String lUL, final int fI, final int id) {
        return new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String lUR = null;
                        try {
                            Log.w("TAG",response.toString());
                            lUR = response.getString("lastUpdate");
                            lUR = lUR.replace('T','_');
                            lUR = lUR.substring(0,19);
                            //Launch update
                            if (!lUR.equals(lUL) || fI==1) {
                                Log.d("INFO", "Update needed because new " +
                                        "installation or new remote db");
                                checkIfUserWantsDownload(id, lUR,response.getString("totalSize"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }


        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", "gpsAdmin");
                params.put("pwd", "Gps@1718");
                return params;
            }

        };
    }
}
