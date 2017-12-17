package com.patan.gimnasio.activities;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.R;
import com.patan.gimnasio.domain.DBRData;
import com.patan.gimnasio.domain.Exercise;
import com.patan.gimnasio.services.ApiHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {


    private int status = 0;
    private int total = 0;
    private TextView state;
    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_RW = 0;

    private GymnasioDBAdapter db;
    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;
    private TextView texto;
    private ProgressBar barra;

    private UpdateTask task = null;
    private DownloadingImgTask task2 = null;

    private String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/GymnasIOapp";

    public GymnasioDBAdapter getGymnasioDbAdapter() {
        return db;
    }
    public Context getContext() {return this;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        barra = (ProgressBar) findViewById(R.id.progressBar);
        texto = (TextView) findViewById(R.id.descargando);
        state = (TextView) findViewById(R.id.descargando);
        state.setText("Descargando");
        ApiHandler api = new ApiHandler(this);
        task = new UpdateTask(this);
        task.execute((Void) null);
    }

    private void updateDatabase(JSONObject list) throws JSONException {
        db = new GymnasioDBAdapter(this);
        db.open();
        total = list.length();
        barra.setVisibility(View.VISIBLE);
        barra.setMax(list.length());
        //barra.setProgress(0);
        for (int i = 0; i < list.length(); i++) {
            //texto.setText("Descargando 0 ejercicios de " + list.length());
            //barra.setProgress(list.length(),i);
            JSONObject ejercicio = list.getJSONObject(i + "");
            Log.d("" + i, ejercicio.toString());
            ArrayList<String> tags = new ArrayList<String>();
            String tag = ejercicio.getString("tag");
            String[] parts = tag.split(",");
            for (String s : parts) {
                tags.add(s);
            }
            String rutaEj = ruta + "/" + ejercicio.getString("name").replaceAll("\\s+", "") + ".jpg";
            final Exercise e = new Exercise(ejercicio.getString("name"),
                    ejercicio.getString("muscle"),
                    ejercicio.getString("description"),
                    rutaEj,
                    tags);
            task2 = new DownloadingImgTask(i, ejercicio.getString("name"),this);
            task2.execute((Void) null);

            //new MyTask().execute(ejercicio.getString("name"));
            db.createExercise(e);
        }
        Log.d("Update", "Database updated");

        //this.finish();
    }
    private void SaveImage(Bitmap finalBitmap, String s) {
        File myDir = new File(ruta);
        Log.w("ImgSave",ruta);
        myDir.mkdirs();
        String fname = s+".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.d("SAVED","Image with name "+s+" saved on filesystem");
            status++;
            texto.setText("Descargando ejercicio "+status+"...");
            barra.setProgress(status);
            if (status == total) {
                state.setText("Descarga finalizada");
                Log.d("DWL", "DOWNLOAD AND STORAGE FINISHED");
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Represents an asynchronous update task.
     */
    public class UpdateTask extends AsyncTask<Void, Void, Boolean> {

        private Context mCtx;
        private JSONObject data;

        UpdateTask(Context ctx) {
            mCtx = ctx;
        }
        @Override
        protected Boolean doInBackground (Void... params) {
            // TODO: attempt authentication against a network service.
            ApiHandler api = new ApiHandler(mCtx);
            JSONObject respuesta = api.updateDB();
            if (respuesta != null) {
                data = respuesta;
                return true;
            } return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    updateDatabase(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                CharSequence text = "Error en la descarga";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(mCtx, text, duration);
                toast.setGravity(Gravity.TOP, 0, 100);
                toast.show();
            }
        }

        @Override
        protected void onCancelled() {
            task = null;
        }
    }

    /**
     * Represents an asynchronous downloadImg task.
     */
    public class DownloadingImgTask extends AsyncTask<Void, Void, Boolean> {

        private Context mCtx;
        private String imgName;
        private Bitmap data;
        private int count;

        DownloadingImgTask(int contador, String iN, Context ctx) {
            count=contador;
            mCtx = ctx;
            imgName = iN.replaceAll("\\s+","");
        }

        @Override
        protected Boolean doInBackground (Void... params) {
            ApiHandler api = new ApiHandler(mCtx);
            Bitmap ok = api.downloadIMG(imgName);
            if (ok != null) {
                data = ok;
                return true;
            } return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                SaveImage(data,imgName);
            } else {
                CharSequence text = "Error en la descarga de " + imgName;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(mCtx, text, duration);
                toast.setGravity(Gravity.TOP, 0, 100);
                toast.show();
            }
        }
        @Override
        protected void onCancelled() {
            task = null;
        }
    }

}