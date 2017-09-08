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

import bs.FridgeMealCards;
import bs.Meal;
import bs.Meals;
import bs.UserFridges;

public class MealAdapter extends BaseAdapter implements Filterable {
    private Meals mMeals;
    private Client mClient;

    public MealAdapter(Meals values) {
        mClient = Client.getInstance();
        mMeals = values;
    }

    @Override
    public int getCount() {
        if (mMeals == null)
            return 0;
        return mMeals.mealsLength();
    }

    @Override
    public Object getItem(int position) {
        if (mMeals == null)
            return null;
        if (position < 0 || position >= mMeals.mealsLength())
            return null;
        return mMeals.meals(position).cn();
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
            convertView = vi.inflate(R.layout.list_item_meal, null);
        }
        TextView tv_cn = (TextView) convertView.findViewById(R.id.text_meal_cn);
        if (mMeals == null)
            return convertView;
        if (position < 0 || position >= mMeals.mealsLength())
            return convertView;
        tv_cn.setText(mMeals.meals(position).cn());
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
                    List<Meal> meallist = findMeal(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = meallist;
                    filterResults.count = meallist.size();
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

    private List<Meal> findMeal(String constraint) {
        List<Meal> r = new ArrayList<Meal>();
        if (mMeals == null)
            return r;
        String uconstraint = constraint.toUpperCase();
        for (int m = 0; m < mMeals.mealsLength(); m++) {
            if (mMeals.meals(m).cn().toUpperCase().contains(uconstraint))
                r.add(mMeals.meals(m));
        }
        return r;
    }
}
