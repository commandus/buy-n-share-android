package com.commandus.buynshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class UserEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        Toolbar toolbarUserEdit = (Toolbar) findViewById(R.id.toolbar_user_edit);
        setTitle(getString(R.string.title_activity_user_add));
        // setSupportActionBar(toolbarUserEdit);
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
