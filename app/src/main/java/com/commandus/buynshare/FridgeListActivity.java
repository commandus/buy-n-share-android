package com.commandus.buynshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.Fridges;

public class FridgeListActivity extends AppCompatActivity
        implements OnServiceResponse
{

    private FridgeAdapter lvFridgesAdapter;
    private ListView mListViewFridges;
    private Client mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fridge_list);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_fridge_list_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FridgeListActivity.this, FridgeAddActivity.class);
                startActivity(intent);
            }
        });

        mListViewFridges = (ListView) findViewById(R.id.listview_fridge_list);

        mClient = Client.getInstance(this);
        mClient.lsFridges(this, getString(R.string.default_locale), this);
    }

    @Override
    public void onSuccess(Object response) {
        lvFridgesAdapter  = new FridgeAdapter(mClient, (Fridges) response);
        mListViewFridges.setAdapter(lvFridgesAdapter);
    }

    @Override
    public int onError(int errorcode, String errorDescription) {
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }

}
