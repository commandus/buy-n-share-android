package com.commandus.svc;

import android.content.Context;
import android.util.Log;

import com.commandus.buynshare.Helper;
import com.commandus.buynshare.R;

import java.nio.ByteBuffer;
import java.util.HashMap;

import bs.FridgeUsers;
import bs.MealCard;
import bs.UserFridges;

public class Client {
    private static final String TAG = Client.class.getSimpleName();
    private static Client mInstance = null;

    private HashMap<Integer, Integer> mMealCardQty;
    private static UserFridges mUserFridges;

    public synchronized static Client getInstance() {
        if (mInstance == null) {
            mInstance = new Client();
        }
        return mInstance;
    }

    public static UserFridges getUserFridges(Context context) {
        ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(Helper.loadResource(context, R.raw.ls_userfridge_2));
            mUserFridges = UserFridges.getRootAsUserFridges(byteBuffer);
        } catch (Exception e) {
            mUserFridges = null;
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
        return mUserFridges;
    }

    public void setMealCardQty(MealCard mc, int value)
    {

    }

    private  Client() {
        mMealCardQty = new HashMap<Integer, Integer>();
    }

    public int getMealcardQtyDiff(int position) {
        Integer r = mMealCardQty.get(position);
        if (r == null)
            r = 0;
        return r;
    }

    public void setMealcardQtyDiff(int position, int value) {
        mMealCardQty.put(position, value + getMealcardQtyDiff(position));
    }

    public void clearMealcardQtyDiff() {
        mMealCardQty.clear();
    }

    public void saveMealcardQtyDiff(UserFridges userFridges) {
        // clearMealcardQtyDiff();
    }

    public static UserFridges lastUserFridges() {
        return mUserFridges;
    }

    public long getFridgeId(int fridge_position) {
        if (mUserFridges == null)
            return -1;
        if ((fridge_position >= mUserFridges.mealcardsLength() || fridge_position < 0))
            return -1;
        long id = mUserFridges.mealcards(fridge_position).fridge().id();
        return id;
    }

    public FridgeUsers getFridgeUsers(int fridge_position) {
        long id = getFridgeId(fridge_position);
        if (id < 0)
            return null;
        for (int f = 0; f < mUserFridges.usersLength(); f++) {
            if (mUserFridges.users(f).fridge().id() != id)
                continue;
            return mUserFridges.users(f);
        }
        return null;
    }
}
