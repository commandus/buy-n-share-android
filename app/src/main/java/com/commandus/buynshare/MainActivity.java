package com.commandus.buynshare;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor>
    {

    private UserFridgeAdapter mUserFridgeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FloatingActionButton fab_meal_card_add = (FloatingActionButton) findViewById(R.id.fab_main_meal_card_add);
        fab_meal_card_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MealCardAddActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView lvUserFridge = (ListView) findViewById(R.id.lv_meal_list);
        if (lvUserFridge != null) {
            mUserFridgeAdapter = new UserFridgeAdapter(this, android.R.layout.simple_list_item_1, null,
                    UserFridgeProvider.FIELDS,
                    new int[] {R.id.list_item_meal_card_fridge_cn, R.id.list_item_meal_card_meal_cn,  R.id.list_item_meal_card_qty}, 0);
            lvUserFridge.setAdapter(mUserFridgeAdapter);
            getSupportLoaderManager().initLoader(0, null, this);

            lvUserFridge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO
					/*
					Cursor c = (Cursor) mUserFridgeAdapter.getItem(position);
					int certificateId = c.getInt(UserCertificateProvider.ID);
					String cert = c.getString(UserCertificateProvider.CERT);
					String pk = c.getString(UserCertificateProvider.PKEY);
					int start = c.getInt(UserCertificateProvider.START);
					int finish = c.getInt(UserCertificateProvider.FINISH);
					*/
                }
            });
            lvUserFridge.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    return false;
                }
            });
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                UserFridgeProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mUserFridgeAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mUserFridgeAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_meal_list:
                break;
            case R.id.nav_add_meal:
                break;
            case R.id.nav_fridge_list:
                Intent intent = new Intent(MainActivity.this, FridgeListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_share:
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
/**
     * chat message list
     * custom cursor adapter
     */
    public class UserFridgeAdapter extends SimpleCursorAdapter {

        private Context mContext;

        private UserFridgeAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            mContext = context;
        }

            /*
            @Override
            public void setViewText(TextView v, String text) {
                int id = v.getId();
                switch (id) {
                    case android.R.id.text1:
                        break;
                    default:
                }
                v.setText(text);
            }
            */

        /**
         * Return custom chat item view
         *
         * @param position    position
         * @param convertView view
         * @param parent      parent view
         * @return custom view
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_meal_card, null);
            }
            Cursor c = (Cursor) mUserFridgeAdapter.getItem(position);
            TextView tv_fridge_cn = (TextView) convertView.findViewById(R.id.list_item_meal_card_fridge_cn);
            TextView tv_cn = (TextView) convertView.findViewById(R.id.list_item_meal_card_meal_cn);
            TextView tv_qty = (TextView) convertView.findViewById(R.id.list_item_meal_card_qty);
            tv_fridge_cn.setText(c.getString(1));
            tv_cn.setText(c.getString(2));
            tv_qty.setText(Integer.toString(c.getInt(3)));
            return convertView;
        }
    }
}
