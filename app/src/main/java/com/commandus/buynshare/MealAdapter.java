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

class MealAdapter extends BaseAdapter implements Filterable {
    private Meals mMeals;
    private Client mClient;
    private List<Meal> resultList = new ArrayList<>();

    MealAdapter(Client client, Meals values) {
        mClient = client;
        mMeals = values;
    }

    @Override
    public int getCount() {
        if (resultList == null)
            return 0;
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        if (resultList == null)
            return null;
        if (position < 0 || position >= resultList.size())
            return null;
        return resultList.get(position).cn();
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
            convertView = vi.inflate(R.layout.list_item_meal, parent, false);
        }
        TextView tv_cn = convertView.findViewById(R.id.text_meal_cn);
        if (mMeals == null)
            return convertView;
        if (position < 0 || position >= resultList.size())
            return convertView;
        tv_cn.setText(resultList.get(position).cn());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
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

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<Meal>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
    }

    private List<Meal> findMeal(String constraint) {
        List<Meal> r = new ArrayList<>();
        if (mMeals == null)
            return r;
        String uconstraint = constraint.toUpperCase();
        for (int m = 0; m < mMeals.mealsLength(); m++) {
            Meal meal = mMeals.meals(m);
            if (meal != null) {
                String cn = meal.cn();
                if (cn != null) {
                    if (cn.toUpperCase().contains(uconstraint))
                        r.add(mMeals.meals(m));
                }
            }
        }
        return r;
    }
}
