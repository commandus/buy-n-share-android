package com.commandus.buynshare;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class MealCardAddActivity extends AppCompatActivity {

    private static final String TAG = MealCardAddActivity.class.getSimpleName();
    private AutoCompleteTextView mMealCN;
    private EditText mEtCost;
    private EditText mEtQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_card_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_meal_card_add);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_meal_card_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        mMealCN = (AutoCompleteTextView) findViewById(R.id.actv_meal_card_add_cn);
        mEtCost = (EditText) findViewById(R.id.et_meal_card_add_cost);
        mEtQty = (EditText) findViewById(R.id.et_meal_card_add_qty);
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
        // TODO
        finish();
    }
}
