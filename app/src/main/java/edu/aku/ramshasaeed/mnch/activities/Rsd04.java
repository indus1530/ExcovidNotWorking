package edu.aku.ramshasaeed.mnch.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import edu.aku.ramshasaeed.mnch.R;
import edu.aku.ramshasaeed.mnch.RMOperations.crudOperations;
import edu.aku.ramshasaeed.mnch.core.MainApp;
import edu.aku.ramshasaeed.mnch.data.DAO.FormsDAO;
import edu.aku.ramshasaeed.mnch.databinding.ActivityRsd04Binding;
import edu.aku.ramshasaeed.mnch.validation.validatorClass;

import static edu.aku.ramshasaeed.mnch.activities.LoginActivity.db;
import static edu.aku.ramshasaeed.mnch.activities.RSDInfoActivity.fc;

public class Rsd04 extends AppCompatActivity {
    //Routine Service Delivery
    ActivityRsd04Binding bi;
    String rm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_rsd04);
        bi.setCallback(this);
        rm = getIntent().getStringExtra("rm");
        this.setTitle(getString(R.string.routineone) + "(" + rm + ")");
        //EventsCall();


    }

    void EventsCall() {

        /*bi.rs3999.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && bi.rs3997.isChecked()) {
                    bi.rs3997.setChecked(false);
                }
                if (b) {
                    bi.rs39.setVisibility(View.GONE);
                    bi.rs39.setText(null);
                } else {
                    bi.rs39.setVisibility(View.VISIBLE);
                }
            }
        });

        bi.rs3997.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && bi.rs3999.isChecked()) {
                    bi.rs3999.setChecked(false);
                }
                if (b) {
                    bi.rs39.setVisibility(View.GONE);
                    bi.rs39.setText(null);
                } else {
                    bi.rs39.setVisibility(View.VISIBLE);
                }
            }
        });

        bi.rs4099.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && bi.rs4097.isChecked()) {
                    bi.rs4097.setChecked(false);
                }
                if (b) {
                    bi.rs40.setVisibility(View.GONE);
                    bi.rs40.setText(null);
                } else {
                    bi.rs40.setVisibility(View.VISIBLE);
                }
            }
        });

        bi.rs4097.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && bi.rs4099.isChecked()) {
                    bi.rs4099.setChecked(false);
                }
                if (b) {
                    bi.rs40.setVisibility(View.GONE);
                    bi.rs40.setText(null);
                } else {
                    bi.rs40.setVisibility(View.VISIBLE);
                }
            }
        });*/

    }

    public void BtnContinue() {
        if (formValidation()) {
            try {
                SaveDraft();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (UpdateDB()) {
                Toast.makeText(this, "Starting Ending Section", Toast.LENGTH_SHORT).show();
                MainApp.endActivity(this, this, RsdMain.class, true, RSDInfoActivity.fc);

            } else {
                Toast.makeText(this, "Failed to Update Database!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void BtnEnd() {
        MainApp.endActivity(this, this, EndingActivity.class, false, RSDInfoActivity.fc);

    }

    private boolean UpdateDB() {

        try {

            Long longID = new crudOperations(db, RSDInfoActivity.fc).execute(FormsDAO.class.getName(), "formsDao", "updateForm").get();
            return longID == 1;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;

    }

    public boolean formValidation() {

        if (!bi.rs3399.isChecked()) {

            if (!validatorClass.EmptyTextBox(this, bi.rs33, getString(R.string.rs33))) {
                return false;
            }
        }
        if (!bi.rs3099.isChecked()) {


            if (!validatorClass.EmptyTextBox(this, bi.rs30, getString(R.string.rs30))) {
                return false;
            }
        }
        if (!bi.rs4499.isChecked()) {


            if (!validatorClass.EmptyTextBox(this, bi.rs44, getString(R.string.rs44))) {
                return false;
            }
        }
        if (!bi.rs3799.isChecked()) {

            if (!validatorClass.EmptyTextBox(this, bi.rs37, getString(R.string.rs37))) {
                return false;
            }
        }
        if (!bi.rs3899.isChecked()) {


            if (!validatorClass.EmptyTextBox(this, bi.rs38, getString(R.string.rs38))) {
                return false;
            }
        }
        if (!bi.rs3999.isChecked()) {
            if (!validatorClass.EmptyTextBox(this, bi.rs39, getString(R.string.rs39))) {
                return false;
            }
        }
        if (!bi.rs4099.isChecked()) {

            return validatorClass.EmptyTextBox(this, bi.rs40, getString(R.string.rs40));
        }

        return true;
    }


    private void SaveDraft() throws JSONException {

        JSONObject f01 = new JSONObject();

        f01.put("rs33", bi.rs3399.isChecked() ? "Mi" : bi.rs33.getText().toString());
        f01.put("rs30", bi.rs3099.isChecked() ? "Mi" : bi.rs30.getText().toString());
        f01.put("rs44", bi.rs4499.isChecked() ? "Mi" : bi.rs44.getText().toString());
        f01.put("rs37", bi.rs3799.isChecked() ? "Mi" : bi.rs37.getText().toString());
        f01.put("rs38", bi.rs3899.isChecked() ? "Mi" : bi.rs38.getText().toString());
        f01.put("rs39", bi.rs3999.isChecked() ? "Mi" : bi.rs39.getText().toString());
        f01.put("rs40", bi.rs4099.isChecked() ? "Mi" : bi.rs40.getText().toString());

        fc.setSD(String.valueOf(f01));

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You can't go back", Toast.LENGTH_SHORT).show();
    }

}
