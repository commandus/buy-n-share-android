package com.commandus.buynshare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.commandus.svc.Client;

import bs.FridgeMealCards;
import bs.UserFridges;

public class UserFridgeMealCardAdapter extends BaseAdapter {
    private static final String TAG = UserFridgeMealCardAdapter.class.getSimpleName();
    private UserFridges mUserFridges;
    private int mPosition;
    private Client mClient;

    private FridgeMealCards getMealCards()
    {
        if (mUserFridges == null || mPosition < 0 || mPosition >= mUserFridges.mealcardsLength()) {
            Log.e(TAG, "getMealCards position wrong: " + Integer.toString(mPosition));
            return null;
        }
        return mUserFridges.mealcards(mPosition);
    }

    public UserFridgeMealCardAdapter(Client client, UserFridges values, int position) {
        mClient = client;
        mPosition = position;
        if (position < 0 || position >= values.mealcardsLength())
            mUserFridges = null;
        else
            mUserFridges = values;
    }

    @Override
    public int getCount() {
        if (mUserFridges == null)
            return 0;
        FridgeMealCards mc = getMealCards();
        if (mc == null)
            return 0;
        return mc.mealcardsLength();
    }

    @Override
    public Object getItem(int position) {
        return getMealCards().mealcards(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_item_meal_card, null);
        }
        TextView tv_cn = (TextView) convertView.findViewById(R.id.list_item_meal_card_meal_cn);
        TextView tv_qty = (TextView) convertView.findViewById(R.id.list_item_meal_card_qty);
        tv_cn.setText(getMealCards().mealcards(position).meal().cn());
        int qty = getMealCards().mealcards(position).qty();
        int q = qty + mClient.getMealcardQtyDiff(position);
        if (q <= 0)
            tv_qty.setBackgroundResource(R.drawable.rounded_textview_red);
        else
            tv_qty.setBackgroundResource(R.drawable.rounded_textview_green);
        tv_qty.setText(Integer.toString(q));
        return convertView;
    }
}
