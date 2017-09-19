package com.commandus.buynshare;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.User;

public class UserEditActivity extends AppCompatActivity
    implements OnServiceResponse
{

    private static final String TAG = UserEditActivity.class.getSimpleName();
    private EditText mCN;
    private ContentLoadingProgressBar mProgressBarUserEdit;
    private boolean mInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        Toolbar toolbarUserEdit = findViewById(R.id.toolbar_user_edit);
        mCN = findViewById(R.id.et_user_cn);
        mProgressBarUserEdit = findViewById(R.id.progress_bar_user_edit);

        setTitle(getString(R.string.title_activity_user_add));
        // setSupportActionBar(toolbarUserEdit);
        setLoadProgress(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void mkUser(String cn) {
        Client.addUser(cn, getString(R.string.default_locale), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_edit, menu);
        MenuItem mi = menu.findItem(R.id.action_adduser);
        if (mi != null)
            mi.setEnabled(!mInProgress);
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
        int id = item.getItemId();

        if (id == R.id.action_adduser) {
            String cn = mCN.getText().toString().trim();
            if (cn.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_cn, Toast.LENGTH_LONG).show();
                return true;
            }

            mkUser(cn);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLoadProgress(boolean value) {
        mInProgress = value;
        mCN.setEnabled(!value);
        invalidateOptionsMenu();
        if (value)
            mProgressBarUserEdit.show();
        else
            mProgressBarUserEdit.hide();
    }

    @Override
    public void onSuccess(int code, Object response) {
        switch (code) {
            case Client.CODE_ADDUSER:
                ApplicationSettings s = ApplicationSettings.getInstance(this);
                s.saveUser((User)response);
                // Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
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
