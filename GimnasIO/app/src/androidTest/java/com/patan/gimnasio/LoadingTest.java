package com.patan.gimnasio;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class LoadingTest extends ActivityInstrumentationTestCase2<LoadingActivity> {

    private LoadingActivity loadingActivityList;
    private GymnasioDBAdapter db;
    private long id1, id2, id3;
    private String url ="http://10.0.2.2:32001/exercises/";

    public LoadingTest() {
        super(LoadingActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        loadingActivityList = getActivity();
        db = loadingActivityList.getGymnasioDbAdapter();
    }

    // Test that check if the response when a ue
    @Test
    public void testFailUser() {
        RequestQueue mQueue = Volley.newRequestQueue(loadingActivityList.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean res = response.getBoolean("success");
                            String res2 = response.getString("message");
                            assertEquals(false,res);
                            assertEquals("Cabecera de la peticion vacía o incorrecta.",res2);

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
                params.put("user", "GPS");
                params.put("pwd", "gmnasIOapp");
                return params;
            }

        };
        mQueue.add(jsonObjectRequest);
    }




}
