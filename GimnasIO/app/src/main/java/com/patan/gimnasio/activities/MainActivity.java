package com.patan.gimnasio.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.patan.gimnasio.database.GymnasioDBAdapter;
import com.patan.gimnasio.R;
import com.patan.gimnasio.domain.DBRData;
import com.patan.gimnasio.services.ApiHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_RW = 0;
    private static String[] EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private View mLayout;
    private View dLayout;

    private GymnasioDBAdapter db;
    private String lUR;
    private int rowId;
    private String downloadSize;
    private DbDataTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goToStartApp();
    }

    private void startApp() {
        db = new GymnasioDBAdapter(this);
        db.open();
        final Cursor c = db.checkForUpdates();
        ApiHandler api = new ApiHandler(this);
        //RequestQueue mQueue = Volley.newRequestQueue(this);
        if (c.moveToFirst()){
            final String lUL = c.getString(c.getColumnIndex("lastUpdate"));
            final int fI = c.getInt(c.getColumnIndex("firstInstalation"));
            final int id = c.getInt(c.getColumnIndex("_id"));
            rowId = id;
            task = new DbDataTask(lUL, fI,this);
            task.execute((Void) null);
        }
        c.close();
    }

    private void checkIfUserWantsDownload(String size){
        CharSequence options[] = new CharSequence[] {"De acuerdo", "En otro momento"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("La aplicación necesita descargar " + size +"MB ¿De acuerdo?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    goToLoadingActivity();
                    db.updateLastUpdate(rowId, lUR);
                }
            }
        });
        builder.show();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.update:
                goToStartApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
    }

    public void goToStartApp() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i("TAG", "Contact permissions has NOT been granted. Requesting permissions.");
            requestRWPermission();
        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            Log.i("TAG",
                    "Contact permissions have already been granted. Displaying contact details.");
            startApp();
        }
    }


    public void goToExercise(View v) {
        Intent intent = new Intent(this, ExerciseListActivity.class);
        intent.putExtra("MODE","view");
        startActivity(intent);
    }

    public void goToRoutine(View v) {
        Intent intent = new Intent(this, RoutineListActivity.class);
        intent.putExtra("USERTYPE","free");
        startActivity(intent);
    }

    public void goToPremium(View v) {
        Intent intent = new Intent(this, PremiumLoginActivity.class);
        startActivity(intent);
    }
    /**
     * Requests the ReadWrite permission.
     */
    private void requestRWPermission() {
        Log.i("TAG", "READWRITE permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i("TAG",
                    "Displaying readwrite permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_rw_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, EXTERNAL_STORAGE,
                                            REQUEST_RW);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, EXTERNAL_STORAGE, REQUEST_RW);
        }
        // END_INCLUDE(contacts_permission_request)
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_RW) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i("TAG", "Received response for readwrite permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i("TAG", "ReadWrite permission has now been granted. Showing preview.");
                startApp();

            } else {
                Log.i("TAG", "ReadWrite permission was NOT granted.");
            }
            // END_INCLUDE(permission_result)

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class DbDataTask extends AsyncTask<Void, Void, Boolean> {

        private final String lastUpdateLocal;
        private final int firstInstallation;
        private Context mCtx;

        DbDataTask(String lUL, int fI, Context ctx) {
            lastUpdateLocal = lUL;
            firstInstallation = fI;
            mCtx = ctx;
        }

        @Override
        protected Boolean doInBackground (Void... params) {
            // TODO: attempt authentication against a network service.
            ApiHandler api = new ApiHandler(mCtx);
            DBRData respuesta = api.dbData(lastUpdateLocal, firstInstallation);
            if (respuesta != null) {
                String lastUpdateRemote = respuesta.getLastUpdate();
                lastUpdateRemote = lastUpdateRemote.replace('T', '_');
                lastUpdateRemote = lastUpdateRemote.substring(0, 19);
                //Launch update
                lUR = lastUpdateRemote;
                if (!lUR.equals(lastUpdateLocal) || firstInstallation == 1) {
                    Log.d("INFO", "Update needed because new " +
                            "installation or new remote db");
                    downloadSize = respuesta.getTotalSize() + "";
                    return true;
                } else return false;
            } return false;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                checkIfUserWantsDownload(downloadSize);
            } else {
                CharSequence text = "No hay nada que descargar";
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
