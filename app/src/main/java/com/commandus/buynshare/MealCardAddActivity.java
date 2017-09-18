package com.commandus.buynshare;

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

import bs.Meals;

public class MealCardAddActivity extends AppCompatActivity implements OnServiceResponse {

    private static final String TAG = MealCardAddActivity.class.getSimpleName();
    private AutoCompleteTextView mMealCN;
    private EditText mEtCost;
    private EditText mEtQty;
    private Meals mMeals = null;
    private Client mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_card_add);
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
        // mEtQty.setText("1");
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
        long cost = 0;
        int qty = 1;
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
            Log.e(TAG, e.toString());
        }
        try {
            cost = Long.parseLong(mEtCost.getText().toString());
        } catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
        long mealId = Client.getMealId(meal);
        Client.addMealCard(mealId, cost, qty);
        // TODO
        finish();
    }

    @Override
    public void onSuccess(int code, Object response) {
        mMeals = (Meals) response;
        mMealCN.setAdapter(new MealAdapter(mClient, mMeals));
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }
}
