package edu.aku.ramshasaeed.mnch.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.aku.ramshasaeed.mnch.R;
import edu.aku.ramshasaeed.mnch.core.CONSTANTS;
import edu.aku.ramshasaeed.mnch.core.MainApp;
import edu.aku.ramshasaeed.mnch.data.AppDatabase;
import edu.aku.ramshasaeed.mnch.databinding.ActivityMainBinding;
import edu.aku.ramshasaeed.mnch.get.server.GetAllData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding bi;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    String DirectoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi= DataBindingUtil.setContentView(this,R.layout.activity_main);
        bi.setCallback(this);
        setSupportActionBar(bi.appbarmain.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, bi.drawerLayout, bi.appbarmain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        bi.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        dbBackup();

        bi.navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (bi.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            bi.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hfa) {
            // Handle the camera action
        } else if (id == R.id.nav_rsd) {
            startActivity(new Intent(MainActivity.this,RSDInfoActivity.class));
        } else if (id == R.id.nav_q_mnch) {

        } else if (id == R.id.nav_dhmt) {

        } else if (id == R.id.nav_upload) {


        }else if (id == R.id.nav_download) {
            //TODO implement

            // Require permissions INTERNET & ACCESS_NETWORK_STATE
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new syncData(this).execute();
            } else {
                Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_opendb) {

        }


        bi.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    public class syncData extends AsyncTask<String, String, String> {

        private Context mContext;

        public syncData(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Toast.makeText(MainActivity.this, "Sync Users", Toast.LENGTH_LONG).show();
                    new GetAllData(mContext, "User", MainApp._HOST_URL + CONSTANTS.URL_USERS).execute();
                    Toast.makeText(MainActivity.this, "Sync District", Toast.LENGTH_LONG).show();
                    new GetAllData(mContext, "District", MainApp._HOST_URL + CONSTANTS.URL_DISTRICT).execute();
                    Toast.makeText(MainActivity.this, "Sync Tehsil", Toast.LENGTH_LONG).show();
                    new GetAllData(mContext, "Tehsil", MainApp._HOST_URL + CONSTANTS.URL_TEHSIL).execute();
                    Toast.makeText(MainActivity.this, "Sync UCs", Toast.LENGTH_LONG).show();
                    new GetAllData(mContext, "UCs", MainApp._HOST_URL + CONSTANTS.URL_UCS).execute();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    editor.putBoolean("flag", true);
                    editor.commit();

                    dbBackup();

                }
            }, 1200);
        }
    }
    public void dbBackup() {

        sharedPref = getSharedPreferences("uen_mnch", MODE_PRIVATE);
        editor = sharedPref.edit();

        if (sharedPref.getBoolean("flag", true)) {

            String dt = sharedPref.getString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

            if (dt != new SimpleDateFormat("dd-MM-yy").format(new Date())) {
                editor.putString("dt", new SimpleDateFormat("dd-MM-yy").format(new Date()));

                editor.commit();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "DMU-MNCH");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {

                DirectoryName = folder.getPath() + File.separator + sharedPref.getString("dt", "");
                folder = new File(DirectoryName);
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {

                    try {
                        /*File dbFile = new File(this.getDatabasePath(AppDatabase.Sub_DBConnection.DATABASE_NAME).getAbsolutePath());
                        FileInputStream fis = new FileInputStream(dbFile);

                        String outFileName = DirectoryName + File.separator +
                                AppDatabase.Sub_DBConnection.DATABASE_NAME + ".db";

                        // Open the empty db as the output stream
                        OutputStream output = new FileOutputStream(outFileName);

                        // Transfer bytes from the inputfile to the outputfile
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                        // Close the streams
                        output.flush();
                        output.close();
                        fis.close();*/

                        String dbFileName = this.getDatabasePath(AppDatabase.Sub_DBConnection.DATABASE_NAME).getAbsolutePath();
                        String outFileName = DirectoryName + File.separator + AppDatabase.Sub_DBConnection.DATABASE_NAME + ".db";

                        File currentDB = new File(dbFileName);
                        File backupDB = new File(outFileName);

                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();


                    } catch (IOException e) {
                        Log.e("dbBackup:", e.getMessage());
                    }

                }

            } else {
                Toast.makeText(this, "Not create folder", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
