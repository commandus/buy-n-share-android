package com.commandus.buynshare;

import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.Purchase;
import bs.Purchases;

public class PurchaseListActivity extends AppCompatActivity implements OnServiceResponse {

    public static final String PAR_FRIDGE_ID = "fridge_id";
    public static final String PAR_FRIDGE_CN = "fridge_cn";
    public static final String PAR_USER_ID = "user_id";
    private static final String TAG = PurchaseListActivity.class.getSimpleName();
    private String mFridgeCN;
    private long mUserId;
    private String mUserCN;
    private long mFridgeId;

    private FridgePurchaseAdapter mFridgePurchaseAdapter;
    private Client mClient;
    private ListView mListviewPurchase;
    private ContentLoadingProgressBar mProgressBarPurchaseList;
    private ApplicationSettings mAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);
        mAppSettings = ApplicationSettings.getInstance(this);
        mClient = Client.getInstance();

        mListviewPurchase = findViewById(R.id.listview_purchase_list);
        mListviewPurchase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Purchase p = (Purchase) mFridgePurchaseAdapter.getItem(position);
                Client.toggleVote(mAppSettings, mUserId, mUserCN, p, PurchaseListActivity.this);
                if (mFridgePurchaseAdapter != null)
                    mFridgePurchaseAdapter.notifyDataSetChanged();
            }
        });
        setTitle(mFridgeCN);
        // getSupportActionBar().setTitle(mFridgeCN);
        ActionBar b = getSupportActionBar();
        if (b != null)
            b.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        mUserId = intent.getLongExtra(PAR_USER_ID, 0);
        ApplicationSettings s = ApplicationSettings.getInstance(this);
        if (mUserId <= 0)
            mUserId = s.getUserId();
        mUserCN = s.getUserCN();
        mFridgeId = intent.getLongExtra(PAR_FRIDGE_ID, 0);
        mFridgeCN = intent.getStringExtra(PAR_FRIDGE_CN);

        mProgressBarPurchaseList = findViewById(R.id.progress_bar_purchase);
        setLoadProgress(true);
        Client.getFridgePurchases(mAppSettings, mFridgeId, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        int id = item.getItemId();
        switch(id) {
            case R.id.action_purchase_list_back:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(int code, Object response) {
        setLoadProgress(false);
        switch (code) {
            case Client.CODE_GETFRIDGEPURCHASES:
                mFridgePurchaseAdapter = new FridgePurchaseAdapter(mClient, mUserId, (Purchases) response);
                mListviewPurchase.setAdapter(mFridgePurchaseAdapter);
                break;
            case Client.CODE_TOGGLE_VOTE:
                // TODO
                setLoadProgress(true);
                Client.getFridgePurchases(mAppSettings, mFridgeId, this);

                Purchase purchase = (Purchase) response;
                Log.i(TAG, String.valueOf(purchase.id()));
                break;
        }
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        setLoadProgress(false);
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }

    private void setLoadProgress(boolean value) {
        if (value)
            mProgressBarPurchaseList.show();
        else
            mProgressBarPurchaseList.hide();
    }
}
