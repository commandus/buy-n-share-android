package com.commandus.buynshare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.commandus.svc.Client;

import java.util.ArrayList;
import java.util.List;

import bs.Fridge;
import bs.Fridges;
import bs.Meal;

public class FridgeAdapter extends BaseAdapter implements Filterable {
    private Fridges mFridges;
    private Client mClient;

    public FridgeAdapter(Client client, Fridges values) {
        mClient = client;
        mFridges = values;
    }

    @Override
    public int getCount() {
        if (mFridges == null)
            return 0;
        return mFridges.fridgesLength();
    }

    @Override
    public Object getItem(int position) {
        if (mFridges == null)
            return null;
        if (position < 0 || position >= mFridges.fridgesLength())
            return null;
        return mFridges.fridges(position).cn();
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
            convertView = vi.inflate(R.layout.list_item_fridge, null);
        }
        TextView tv_cn = (TextView) convertView.findViewById(R.id.text_fridge_cn);
        if (mFridges == null)
            return convertView;
        if (position < 0 || position >= mFridges.fridgesLength())
            return convertView;
        tv_cn.setText(mFridges.fridges(position).cn());
        return convertView;
    }

    private List<Meal> resultList = new ArrayList<Meal>();

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Fridge> fridgeList = findFridge(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = fridgeList;
                    filterResults.count = fridgeList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<Meal>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    private List<Fridge> findFridge(String constraint) {
        List<Fridge> r = new ArrayList<Fridge>();
        if (mFridges == null)
            return r;
        String uconstraint = constraint.toUpperCase();
        for (int f = 0; f < mFridges.fridgesLength(); f++) {
            if (mFridges.fridges(f).cn().toUpperCase().contains(uconstraint))
                r.add(mFridges.fridges(f));
        }
        return r;
    }
}
