package com.commandus.buynshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.Fridge;

public class FridgeAddActivity extends AppCompatActivity implements OnServiceResponse {

    private static final String TAG = FridgeAddActivity.class.getSimpleName();
    public static final String PAR_FRIDGE_ID = "id";
    public static final String PAR_FRIDGE_CN = "cn";
    private AutoCompleteTextView tvFridgeCN;
    private FloatingActionButton mFabSave;
    private boolean mInProgress;
    private ContentLoadingProgressBar mProgressBarFridgeAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_add);
        Toolbar toolbar = findViewById(R.id.toolbar_fridge_add);
        setSupportActionBar(toolbar);
        ActionBar b = getSupportActionBar();
        if (b != null)
            b.setDisplayHomeAsUpEnabled(true);


        mProgressBarFridgeAdd = findViewById(R.id.progress_bar_fridge_add);

        mFabSave = findViewById(R.id.fab_fridge_add);
        mFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        tvFridgeCN = findViewById(R.id.tv_fridge_add_cn);
        setLoadProgress(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fridge_add, menu);
        if (mInProgress) {
            MenuItem mi = menu.findItem(R.id.action_add_fridge);
            if (mi != null) {
                mi.setEnabled(false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_add_fridge:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        setLoadProgress(true);
        String cn = "";
        try {
            cn = tvFridgeCN.getText().toString();
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
        if (cn.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_cn, Toast.LENGTH_LONG).show();
            return;
        }
        ApplicationSettings mAppSettings = ApplicationSettings.getInstance(this);
        long balance = 0;
        Client.addFridge(mAppSettings, getString(R.string.default_locale), mAppSettings.getUserId(), cn, balance, this);
    }

    private void setLoadProgress(boolean value) {
        mInProgress = value;
        invalidateOptionsMenu();
        mFabSave.setEnabled(!value);
        tvFridgeCN.setEnabled(!value);
        if (value)
            mProgressBarFridgeAdd.show();
        else
            mProgressBarFridgeAdd.hide();
    }

    @Override
    public void onSuccess(int code, Object response) {
        setLoadProgress(false);
        switch (code) {
            case Client.CODE_ADDFRIDGE:
                Fridge f = (Fridge) response;
                Intent intent = new Intent();
                intent.putExtra(PAR_FRIDGE_ID, f.id());
                intent.putExtra(PAR_FRIDGE_CN, f.cn());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        setLoadProgress(false);
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }

}
