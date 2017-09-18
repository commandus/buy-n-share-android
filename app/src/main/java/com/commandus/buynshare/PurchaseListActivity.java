package com.commandus.buynshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.Purchases;

public class PurchaseListActivity extends AppCompatActivity implements OnServiceResponse {

    public static final String PAR_FRIDGE_ID = "fridge_id";
    public static final String PAR_FRIDGE_CN = "fridge_cn";
    public static final String PAR_USER_ID = "user_id";
    private String mFridgeCN;
    private long mUserId;
    private long mFridgeId;

    private FridgePurchaseAdapter mFridgePurchaseAdapter;
    private Client mClient;
    private ListView mListviewPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        mClient = Client.getInstance();
        mListviewPurchase = findViewById(R.id.listview_purchase_list);
        setTitle(mFridgeCN);

        Intent intent = getIntent();
        mUserId = intent.getLongExtra(PAR_USER_ID, 0);
        mFridgeId = intent.getLongExtra(PAR_FRIDGE_ID, 0);
        mFridgeCN = intent.getStringExtra(PAR_FRIDGE_CN);

        Client.getFridgePurchases(this, mFridgeId, this);
    }

    @Override
    public void onSuccess(int code, Object response) {
        mFridgePurchaseAdapter = new FridgePurchaseAdapter(mClient, mUserId, (Purchases) response);
        mListviewPurchase.setAdapter(mFridgePurchaseAdapter);
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }
}
