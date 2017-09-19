package com.commandus.buynshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.Meal;
import bs.Meals;
import bs.Purchase;

public class MealCardAddActivity extends AppCompatActivity implements OnServiceResponse {

    private static final String TAG = MealCardAddActivity.class.getSimpleName();
    public static final String PAR_FRIDGE_ID = "fridge_id";
    private static final String PAR_PURCHASE_ID = "purchase_id";
    private AutoCompleteTextView mMealCN;
    private EditText mEtCost;
    private EditText mEtQty;
    private Meals mMeals = null;
    private long mFridgeId;
    private Client mClient;
    private ApplicationSettings mAppSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = Client.getInstance();
        mAppSettings = ApplicationSettings.getInstance(this);
        setContentView(R.layout.activity_meal_card_add);

        Intent intent = getIntent();
        mFridgeId = intent.getLongExtra(PAR_FRIDGE_ID, 0);

        Toolbar toolbar = findViewById(R.id.toolbar_meal_card_add);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_meal_card_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        mMealCN = findViewById(R.id.actv_meal_card_add_cn);
        Client.getMeals(this, getString(R.string.default_locale), this);
        mEtCost = findViewById(R.id.et_meal_card_add_cost);
        mEtQty = findViewById(R.id.et_meal_card_add_qty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meal_card_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_add_meal_card:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        String meal = "";
        long cost;
        int qty;
        try {
            meal = mMealCN.getText().toString();
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
        try {
            qty = Integer.parseInt(mEtQty.getText().toString());
        } catch (Exception e)
        {
            qty = 1;
        }
        try {
            cost = Long.parseLong(mEtCost.getText().toString());
        } catch (Exception e)
        {
            cost = 0;
        }
        long mealId = Client.getMealId(meal);
        if (mealId < 0)
            Client.addMeal(getString(R.string.default_locale), meal, this);
        else {
            Client.addPurchase(getString(R.string.default_locale), mAppSettings.getUserId(), mFridgeId, mealId, cost, qty, this);
        }
    }

    @Override
    public void onSuccess(int code, Object response) {
        switch (code) {
            case Client.CODE_ADDMEAL:
                Meal meal = (Meal) response;
                long cost;
                int qty;
                try {
                    qty = Integer.parseInt(mEtQty.getText().toString());
                } catch (Exception e)
                {
                    qty = 1;
                }
                try {
                    cost = Long.parseLong(mEtCost.getText().toString());
                } catch (Exception e)
                {
                    cost = 0;
                }

                Client.addPurchase(getString(R.string.default_locale), mAppSettings.getUserId(), mFridgeId,
                        meal.id(), cost, qty, this);
                break;
            case Client.CODE_LSMEAL:
                mMeals = (Meals) response;
                mMealCN.setAdapter(new MealAdapter(mClient, mMeals));
                break;
            case Client.CODE_ADDPURCHASE:
                Purchase purchase = (Purchase) response;
                // Just in case return purchase and fridge ids
                Intent intent = new Intent();
                intent.putExtra(PAR_FRIDGE_ID, mFridgeId);
                if (purchase != null) {
                    intent.putExtra(PAR_PURCHASE_ID, purchase.id());
                }
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }
}
