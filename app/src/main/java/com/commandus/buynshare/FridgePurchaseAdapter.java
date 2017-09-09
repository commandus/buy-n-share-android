package com.commandus.buynshare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.commandus.svc.Client;

import java.text.DateFormat;
import java.util.Date;

import bs.FridgeMealCards;
import bs.Purchase;
import bs.Purchases;

public class FridgePurchaseAdapter extends BaseAdapter {
    private static final String TAG = FridgePurchaseAdapter.class.getSimpleName();
    private final long mUserId;
    private Purchases mPurchases;
    private Client mClient;

    private Purchase getPurchase(int position)
    {
        if (position < 0 || position >= mPurchases.purchasesLength())
            return null;
        return mPurchases.purchases(position);
    }

    public FridgePurchaseAdapter(Client client, long user_id, Purchases values) {
        mClient = client;
        mPurchases = values;
        mUserId = user_id;
    }

    @Override
    public int getCount() {
        if (mPurchases == null)
            return 0;
        return mPurchases.purchasesLength();
    }

    @Override
    public Object getItem(int position) {
        return getPurchase(position);
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
            convertView = vi.inflate(R.layout.list_item_purchase, null);
        }
        TextView tv_cn = (TextView) convertView.findViewById(R.id.list_item_purchase_meal_cn);
        TextView tv_cost = (TextView) convertView.findViewById(R.id.list_item_purchase_cost);
        TextView tv_vote_count = (TextView) convertView.findViewById(R.id.list_item_purchase_vote_count);
        TextView tv_vote = (TextView) convertView.findViewById(R.id.list_item_purchase_vote);
        TextView tv_start = (TextView) convertView.findViewById(R.id.list_item_purchase_start);


        Purchase p = getPurchase(position);
        if (p == null)
            return convertView;
        if (p.meal() == null) {
            Log.e(TAG, "No meal in purchase");
        }
        else
            tv_cn.setText(p.meal().cn());

        tv_cost.setText(Long.toString(p.cost()));
        int votes = p.votesLength();
        tv_vote_count.setText(Long.toString(votes));

        if (Client.voteExists(mUserId, p))
            tv_vote.setBackgroundResource(R.drawable.thumb_up);
        else
            tv_vote.setBackgroundResource(R.drawable.thumb_down);

        Log.i(TAG, "Date: " + Long.toString(p.start()));
        String ft  = DateFormat.getDateInstance(DateFormat.FULL).format(new Date(1000L * p.start()));
        tv_start.setText(ft);
        return convertView;
    }
}
