package com.commandus.buynshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.FridgeUsers;
import bs.Fridges;

public class FridgeListActivity extends AppCompatActivity
        implements OnServiceResponse
{

    public static final String PAR_FRIDGE_POS = "fridge_pos";
    private static final int RET_ADD_FRIDGE = 1;
    private FridgeAdapter lvFridgesAdapter;
    private ListView mListViewFridges;
    private Client mClient;
    private ContentLoadingProgressBar mProgressBarFridgeList;
    private long mRetFridgeId;
    private FloatingActionButton mFabFridgeAdd;
    private ApplicationSettings mAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppSettings = ApplicationSettings.getInstance(this);
        setContentView(R.layout.activity_fridge_list);

        Toolbar toolbar = findViewById(R.id.toolbar_fridge_list);
        setSupportActionBar(toolbar);
        ActionBar b = getSupportActionBar();
        if (b != null)
            b.setDisplayHomeAsUpEnabled(true);


        mFabFridgeAdd = findViewById(R.id.fab_fridge_list_add);
        mFabFridgeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FridgeListActivity.this, FridgeAddActivity.class);
                startActivityForResult(intent, RET_ADD_FRIDGE);
            }
        });

        mListViewFridges = findViewById(R.id.listview_fridge_list);
        mListViewFridges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                select(position);
            }
        });

        mProgressBarFridgeList = findViewById(R.id.progressBarFridgeList);
        setLoadProgress(true);
        Client.lsFridges(mAppSettings, getString(R.string.default_locale), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fridge_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        int id = item.getItemId();
        switch(id) {
            case R.id.action_fridge_list_back:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void select(int position) {
        Intent intent = new Intent();
        intent.putExtra(PAR_FRIDGE_POS, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case RET_ADD_FRIDGE:
                mRetFridgeId = data.getLongExtra(FridgeAddActivity.PAR_FRIDGE_ID, 0L);
                String fridge_cn = data.getStringExtra(FridgeAddActivity.PAR_FRIDGE_CN);
                if (mRetFridgeId > 0) {
                    mProgressBarFridgeList.show();
                    Client.lsFridges(mAppSettings, getString(R.string.default_locale), this);
                    Client.getUserFridges(mAppSettings, this);
                }
                break;
        }
    }

    private void setLoadProgress(boolean value) {
        mFabFridgeAdd.setEnabled(!value);
        if (value)
            mProgressBarFridgeList.show();
        else
            mProgressBarFridgeList.hide();
    }

    @Override
    public void onSuccess(int code, Object response) {
        setLoadProgress(false);
        switch (code) {
            case Client.CODE_LSFRIDGES:
                lvFridgesAdapter  = new FridgeAdapter(mClient, (Fridges) response);
                mListViewFridges.setAdapter(lvFridgesAdapter);
                break;
            case Client.CODE_GETUSERFRIDGES:
                if (mRetFridgeId > 0) {
                    // After we got a new fridge list, return position in the list
                    int p = Client.getFridgePos(mRetFridgeId);
                    if (p >= 0)
                        select(p);
                    mRetFridgeId = 0;
                }
                break;
        }
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        setLoadProgress(false);
        String s;
        switch (errorcode) {
            case Client.ERRCODE_NO_USER_YET:
                s = getString(R.string.error_no_user_yet);
                break;
            default:
                s = errorDescription;
        }
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        return 0;
    }

}
