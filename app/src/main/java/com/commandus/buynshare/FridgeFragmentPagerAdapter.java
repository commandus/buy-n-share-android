package com.commandus.buynshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import bs.UserFridges;

/**
 * Created by Андрей on 02.09.2017.
 */

public class FridgeFragmentPagerAdapter extends FragmentPagerAdapter {
    private final UserFridges mUserFridges;

    public FridgeFragmentPagerAdapter(FragmentManager fm, UserFridges values) {
        super(fm);
        mUserFridges = values;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new FridgeFragment();
        Bundle args = new Bundle();
        args.putInt(FridgeFragment.ARG_PAGE, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        if (mUserFridges == null)
            return 0;
        return mUserFridges.mealcardsLength();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mUserFridges == null)
            return "";
        return mUserFridges.mealcards(position).fridge().cn();
    }
}
