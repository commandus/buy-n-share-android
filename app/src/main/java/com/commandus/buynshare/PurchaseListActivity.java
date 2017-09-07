package com.commandus.buynshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.commandus.svc.Client;

public class PurchaseListActivity extends AppCompatActivity {

    public static final String PAR_FRIDGE_ID = "fridge_id";
    public static final String PAR_FRIDGE_CN = "fridge_cn";
    public static final String PAR_USER_ID = "user_id";
    private String mFridgeCN;
    private long mUserId;
    private long mFridgeId;

    private FridgePurchaseAdapter mFridgePurchaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        ListView listviewPurchase = (ListView) findViewById(R.id.listview_purchase_list);
        setTitle(mFridgeCN);

        Intent intent = getIntent();
        mUserId = intent.getLongExtra(PAR_USER_ID, 0);
        mFridgeId = intent.getLongExtra(PAR_FRIDGE_ID, 0);
        mFridgeCN = intent.getStringExtra(PAR_FRIDGE_CN);

        mFridgePurchaseAdapter = new FridgePurchaseAdapter(mUserId, Client.getFridgePurchases(this, mFridgeId));
        listviewPurchase.setAdapter(mFridgePurchaseAdapter);
    }
}
