package com.commandus.buynshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.Fridges;

public class FridgeListActivity extends AppCompatActivity
        implements OnServiceResponse
{

    public static final String PAR_FRIDGE_POS = "fridge_pos";
    private FridgeAdapter lvFridgesAdapter;
    private ListView mListViewFridges;
    private Client mClient;
    private ContentLoadingProgressBar mProgressBarFridgeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_list);

        Toolbar toolbar = findViewById(R.id.toolbar_fridge_list);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_fridge_list_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FridgeListActivity.this, FridgeAddActivity.class);
                startActivity(intent);
            }
        });

        mListViewFridges = findViewById(R.id.listview_fridge_list);
        mListViewFridges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(PAR_FRIDGE_POS, position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mProgressBarFridgeList = findViewById(R.id.progressBarFridgeList);

        mProgressBarFridgeList.show();
        Client.lsFridges(this, getString(R.string.default_locale), this);
    }

    @Override
    public void onSuccess(int code, Object response) {
        switch (code) {
            case Client.CODE_LSFRIDGES:
                lvFridgesAdapter  = new FridgeAdapter(mClient, (Fridges) response);
                mListViewFridges.setAdapter(lvFridgesAdapter);
                mProgressBarFridgeList.hide();
                break;
        }
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }

}
