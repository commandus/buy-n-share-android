package com.commandus.buynshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.GzipRequestInterceptor;
import com.commandus.svc.Client;

import bs.FridgeUsers;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ApplicationSettings mApplicationSettings;
    private Client mClient;
    private ViewPager mViewPager;
    private FridgeFragmentPagerAdapter mFridgeFragmentPagerAdapter;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new GzipRequestInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);

        mApplicationSettings = ApplicationSettings.getInstance(this);
        mClient = Client.getInstance();

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        mFridgeFragmentPagerAdapter = new FridgeFragmentPagerAdapter(getSupportFragmentManager(), Client.getUserFridges(this));

        mViewPager = (ViewPager) findViewById(R.id.vp_fridge);
        mViewPager.setAdapter(mFridgeFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                showFridgeDetails(position);
                buildNavigationMenu();
                buildFridgeMenu(position);
            }

            @Override
            public void onPageSelected(int position) {
                showFridgeDetails(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void showFridgeDetails(int position) {
        if (mClient.lastUserFridges()!= null) {
            if (mClient.lastUserFridges().mealcardsLength() > position && position >= 0) {
                mToolbar.setTitle(mClient.lastUserFridges().mealcards(position).fridge().cn());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mApplicationSettings.isUserRegistered())
        {
            Intent intent = new Intent(MainActivity.this, UserEditActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClient.saveMealcardQtyDiff(Client.lastUserFridges());
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
        if (mClient.lastUserFridges() == null)
            return true;
        int position =  mViewPager.getCurrentItem();
        FridgeUsers fu =  mClient.getFridgeUsers(position);
        // SubMenu subMenu = menu.addSubMenu(getString(R.string.nav_my_fridges));
        for (int u = 0; u < fu.fridgeusersLength(); u++) {
            String s = fu.fridgeusers(u).user().cn() + ": " + Long.toString(fu.fridgeusers(u).balance());
            MenuItem item = menu.add(R.id.options_group_fridge_users,
                    -1 - u,
                    Menu.NONE,
                    s);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent  intent;
        switch(id) {
            case R.id.action_lspurchase:
                intent = new Intent(MainActivity.this, PurchaseListActivity.class);
                intent.putExtra(PurchaseListActivity.PAR_USER_ID, mApplicationSettings.getUserId());
                intent.putExtra(PurchaseListActivity.PAR_FRIDGE_ID, mClient.getFridgeId(mViewPager.getCurrentItem()));
                intent.putExtra(PurchaseListActivity.PAR_FRIDGE_CN, mClient.getFridgeCN(mViewPager.getCurrentItem()));
                startActivity(intent);
                return true;
            case R.id.action_rmfridge:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id < 0)
            setFridgePage(- id - 1);
        else {
            Intent intent;
            switch (id) {
                case 0:
                    intent = new Intent(MainActivity.this, FridgeAddActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_fridge_list_around:
                    intent = new Intent(MainActivity.this, FridgeListActivity.class);
                    startActivity(intent);
                    break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFridgePage(int position) {
        mViewPager.setCurrentItem(position);
        // showFridgeDetails(position);
    }

    private void buildNavigationMenu() {
        Menu menu = mNavigationView.getMenu();
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        buildMenuFridges(menu);
    }

    private void buildFridgeMenu(int position) {
        invalidateOptionsMenu();
    }

    private void buildMenuFridges(final Menu menu) {
        if (mClient.lastUserFridges() == null)
            return;

        SubMenu subMenu = menu.addSubMenu(getString(R.string.nav_my_fridges));
        for (int f = 0; f < mClient.lastUserFridges().mealcardsLength(); f++) {
            MenuItem item = subMenu.add(R.id.nav_group_fridges,
                -1 - f, // (int) mClient.lastUserFridges().mealcards(f).fridge().id(),
                Menu.NONE,
                mClient.lastUserFridges().mealcards(f).fridge().cn());
        }
        MenuItem item = subMenu.add(R.id.nav_group_fridges,
                0,
                Menu.NONE,
                getString(R.string.title_activity_fridge_add));

    }
}
