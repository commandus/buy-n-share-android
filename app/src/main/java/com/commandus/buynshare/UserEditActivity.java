package com.commandus.buynshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.commandus.svc.Client;

public class UserEditActivity extends AppCompatActivity {

    private static final String TAG = UserEditActivity.class.getSimpleName();
    private EditText mCN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        Toolbar toolbarUserEdit = (Toolbar) findViewById(R.id.toolbar_user_edit);
        mCN = (EditText) findViewById(R.id.et_user_cn);
        setTitle(getString(R.string.title_activity_user_add));
        // setSupportActionBar(toolbarUserEdit);
    }

    @Override
    protected void onDestroy() {
        Client c = Client.getInstance();
        long uid = c.addUser(this, mCN.getText().toString(), getString(R.string.default_locale));
        ApplicationSettings s = ApplicationSettings.getInstance(this);
        Log.i(TAG, "Add user id " + Long.toString(uid));
        if (uid <= 0)
            uid = 1;
        s.setUserId(uid);
        s.save();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_edit, menu);
        return true;
        /*
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar_user_edit);
        tb.inflateMenu(R.menu.user_edit);
        tb.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
        return true;
        */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_adduser) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
