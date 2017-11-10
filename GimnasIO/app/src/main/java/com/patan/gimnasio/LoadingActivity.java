package com.patan.gimnasio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {
    private GymnasioDBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        String url ="http://10.0.2.2:32001/exercises/";
        RequestQueue mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            updateDatabase(response);
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
                        params.put("user", "adminGPS");
                        params.put("pwd", "gimnasIOapp");
                        return params;
                    }

        };
        mQueue.add(jsonObjectRequest);
    }

    private void updateDatabase(JSONObject list) throws JSONException {
        db= new GymnasioDBAdapter(this);
        db.open();
        for (int i = 0;i<list.length() ; i++) {
            JSONObject ejercicio = list.getJSONObject(i+"");
            Log.w("ELEMENTO"+i,ejercicio.toString());
            ArrayList<String> tags = new ArrayList<String>();
            String tag = ejercicio.getString("tag");
            String[] parts = tag.split(",");
            for (String s: parts) {
                tags.add(s);
            }
            Exercise e = new Exercise(ejercicio.getString("name"),
                    ejercicio.getString("muscle"),
                    ejercicio.getString("description"),
                    ejercicio.getString("name"),
                    tags);
            db.createExercise(e);
        }
        Log.d("Update","Database updated");
        this.finish();
    }
}
