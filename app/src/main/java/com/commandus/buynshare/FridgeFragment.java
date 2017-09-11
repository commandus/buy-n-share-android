package com.commandus.buynshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.commandus.svc.Client;
import com.commandus.svc.OnServiceResponse;

import bs.UserFridges;

public class FridgeFragment extends Fragment {
    public static final String ARG_PAGE = "p";
    private static final String TAG = FridgeFragment.class.getSimpleName();

    private UserFridgeMealCardAdapter mUserFridgeAdapter;
    private Client mClient = Client.getInstance(this.getContext());
    private ListView mListViewUserFridge;
    private int mPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(R.layout.fragment_userfridge, container, false);
        Bundle args = getArguments();

        mPage = args.getInt(ARG_PAGE);

        mListViewUserFridge = (ListView) rootView.findViewById(R.id.lv_meal_list);
        if (mListViewUserFridge != null) {
            mUserFridgeAdapter = new UserFridgeMealCardAdapter(mClient, Client.lastUserFridges(), mPage);
            mListViewUserFridge.setAdapter(mUserFridgeAdapter);
            mListViewUserFridge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mClient.setMealcardQtyDiff(position, -1);
                    mUserFridgeAdapter.notifyDataSetChanged();
                }
            });
            mListViewUserFridge.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    mClient.setMealcardQtyDiff(position, 1);
                    mUserFridgeAdapter.notifyDataSetChanged();
                    return true;
                }
            });
        }
        return rootView;
    }
}
