package com.commandus.svc;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.commandus.buynshare.ApplicationSettings;
import com.commandus.buynshare.Helper;
import com.commandus.buynshare.R;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.HashMap;

import bs.FridgeUsers;
import bs.MealCard;
import bs.User;
import bs.UserFridges;

public class Client {
    private static final String URL = "http://f.commandus.com/a/";

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

    public static long addUser(final Context context, String cn, String locale) {
        FlatBufferBuilder fbb = new FlatBufferBuilder(0);
        int scn = fbb.createString(cn);
        int slocale = fbb.createString(locale);

        User.startUser(fbb);
        User.addId(fbb, 0);
        User.addCn(fbb, scn);
        User.addKey(fbb, 0);
        User.addLocale(fbb, slocale);
        User.addGeo(fbb, 0);
        int u = User.endUser(fbb);
        fbb.finish(u);

        long id = 0;

        try {
            AndroidNetworking.post(URL + "add_user.php")
                    .setContentType("application/octet-stream")
                    .addByteBody(fbb.dataBuffer().array())
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            ByteBuffer byteBuffer;
                            try {
                                User u = User.getRootAsUser(ByteBuffer.wrap(response.getBytes()));
                                ApplicationSettings s = ApplicationSettings.getInstance(context);
                                s.saveUser(u);
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return 0;
        }
        return id;
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

    /**
     * @brief Return fridge identifier for User Fridge at position in
     * @param fridge_position position, zero based
     * @return fridge identifier for User Fridge at position in
     */
    public long getFridgeId(int fridge_position) {
        if (mUserFridges == null)
            return -1;
        if ((fridge_position >= mUserFridges.mealcardsLength() || fridge_position < 0))
            return -1;
        long id = mUserFridges.mealcards(fridge_position).fridge().id();
        return id;
    }

    /**
     * @brief Return Users of the fridge
     * @param fridge_position
     * @return Users of the fridge
     */
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