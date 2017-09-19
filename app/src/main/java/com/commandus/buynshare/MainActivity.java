package com.commandus.buynshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interceptors.GzipRequestInterceptor;
import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.FridgeUsers;
import bs.UserFridges;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnServiceResponse
{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RET_FRIDGE = 1;
    private static final int RET_NEW_USER = 2;
    private ApplicationSettings mApplicationSettings;
    private Client mClient;
    private ViewPager mViewPager;
    private FridgeFragmentPagerAdapter mFridgeFragmentPagerAdapter;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ContentLoadingProgressBar mProgressBarMain;

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
        mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        mViewPager = findViewById(R.id.vp_fridge);

        mProgressBarMain = findViewById(R.id.progressBarMain);
        mProgressBarMain.show();

        Client.getUserFridges(this, this);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navigateToFridgePage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        FloatingActionButton fab_meal_card_add = findViewById(R.id.fab_main_meal_card_add);
        fab_meal_card_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MealCardAddActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void navigateToFridgePage(int position) {
        showFridgeDetails(position);
        buildNavigationMenu();
        buildFridgeMenu(position);
    }

    private void showFridgeDetails(int position) {
        String s = getString(R.string.app_name);
        if (Client.lastUserFridges()!= null) {
            if (Client.lastUserFridges().mealcardsLength() > position && position >= 0) {
                s = Client.lastUserFridges().mealcards(position).fridge().cn();
            }
        }
        mToolbar.setTitle(s);
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        if (Client.lastUserFridges() == null) {
            // Remove delete fridge
            disableMenuRemoveFridge(menu);
            return true;
        }
        if (Client.lastUserFridges().mealcardsLength() ==0)
            // Remove delete fridge
            disableMenuRemoveFridge(menu);

        int position =  mViewPager.getCurrentItem();
        FridgeUsers fu =  Client.getFridgeUsers(position);
        if (fu == null)
            return true;
        // SubMenu subMenu = menu.addSubMenu(getString(R.string.nav_my_fridges));
        long id = mApplicationSettings.getUserId();

        SpannableString s;
        // me
        for (int u = 0; u < fu.fridgeusersLength(); u++) {
            if (fu.fridgeusers(u).user().id() != id)
                continue;
            s = new SpannableString(fu.fridgeusers(u).user().cn() + ": " + Long.toString(fu.fridgeusers(u).balance()));
            MenuItem item = menu.add(R.id.options_group_fridge_users,
                    -1 - u,
                    Menu.NONE,
                    s);
            s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, s.length(), 0);
            item.setEnabled(false);
        }
        // others
        for (int u = 0; u < fu.fridgeusersLength(); u++) {
            if (fu.fridgeusers(u).user().id() == id)
                continue;
            s = new SpannableString(fu.fridgeusers(u).user().cn() + ": " + Long.toString(fu.fridgeusers(u).balance()));
            MenuItem item = menu.add(R.id.options_group_fridge_users,
                    -1 - u,
                    Menu.NONE,
                    s);
        }
        return true;
    }

    private void disableMenuRemoveFridge(Menu menu) {
        MenuItem mi = menu.findItem(R.id.action_rmfridge);
        if (mi != null)
            mi.setEnabled(false);
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
                intent.putExtra(PurchaseListActivity.PAR_FRIDGE_ID, Client.getFridgeId(mViewPager.getCurrentItem()));
                intent.putExtra(PurchaseListActivity.PAR_FRIDGE_CN, Client.getFridgeCN(mViewPager.getCurrentItem()));
                startActivity(intent);
                return true;
            case R.id.action_rmfridge:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.action_rm_fridge)
                        .setMessage(R.string.action_rm_fridge_confirm)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Client.rmFridge(Client.getFridgeId(mViewPager.getCurrentItem()));
                                Toast.makeText(MainActivity.this, R.string.action_rm_fridge_done, Toast.LENGTH_SHORT).show();
                                mFridgeFragmentPagerAdapter.notifyDataSetChanged();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            case R.id.action_rmuser:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.action_rm_user)
                        .setMessage(R.string.action_rm_user_confirm)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Client.rmUser();
                                mApplicationSettings.clearUser();
                                Client.clearAccount();
                                Toast.makeText(MainActivity.this, R.string.action_rm_user_done, Toast.LENGTH_SHORT).show();
                                refreshUserFridges(null);
                                Intent intent = new Intent(MainActivity.this, UserEditActivity.class);
                                startActivityForResult(intent, RET_NEW_USER);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id < 0)
            setFridgePage(- id - 1);
        else {
            Intent intent;
            switch (id) {
                /*
                case 0:
                    intent = new Intent(MainActivity.this, FridgeAddActivity.class);
                    startActivity(intent);
                    break;
                */
                case R.id.nav_fridge_list_around:
                    intent = new Intent(MainActivity.this, FridgeListActivity.class);
                    startActivityForResult(intent, RET_FRIDGE);
                    break;
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case RET_FRIDGE:
                int fridge_pos = data.getIntExtra(FridgeListActivity.PAR_FRIDGE_POS, -1);
                if (fridge_pos >= 0) {
                    // add user to the fridge
                    long balance = 0L;
                    mClient.addFridgeUser(mApplicationSettings.getUserId(), Client.lastFridge(fridge_pos),
                            balance, getString(R.string.default_locale), this);
                }
                break;
            case RET_NEW_USER:
                break;
        }
    }

    private void setFridgePage(int position) {
        mViewPager.setCurrentItem(position);
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
        if (Client.lastUserFridges() == null)
            return;

        SubMenu subMenu = menu.addSubMenu(getString(R.string.nav_my_fridges));
        for (int f = 0; f < Client.lastUserFridges().mealcardsLength(); f++) {
            MenuItem item = subMenu.add(R.id.nav_group_fridges,
                -1 - f, // (int) mClient.lastUserFridges().mealcards(f).fridge().id(),
                Menu.NONE,
                Client.lastUserFridges().mealcards(f).fridge().cn());
        }
        /*
        MenuItem item = subMenu.add(R.id.nav_group_fridges,
                0,
                Menu.NONE,
                getString(R.string.title_activity_fridge_add));
        */

    }

    private void refreshUserFridges(UserFridges userFridges) {
        mFridgeFragmentPagerAdapter = new FridgeFragmentPagerAdapter(getSupportFragmentManager(),
                userFridges);
        mViewPager.setAdapter(mFridgeFragmentPagerAdapter);
        navigateToFridgePage(0);
    }


    @Override
    public void onSuccess(int code, Object response) {
        mProgressBarMain.hide();
        switch (code) {
            case Client.CODE_ADDFRIDGEUSER:
                Client.getUserFridges(this, this);
                return;
            case Client.CODE_GETUSERFRIDGES:
                refreshUserFridges(Client.lastUserFridges());
                break;
        }
    }

    @Override
    public int onError(int code, int errorcode, String errorDescription) {
        mProgressBarMain.hide();
        Toast.makeText(this, errorDescription, Toast.LENGTH_LONG).show();
        return 0;
    }
}
