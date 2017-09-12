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
import java.util.Date;
import java.util.HashMap;

import bs.Fridge;
import bs.FridgeUser;
import bs.FridgeUsers;
import bs.Fridges;
import bs.MealCard;
import bs.Meals;
import bs.Purchase;
import bs.Purchases;
import bs.User;
import bs.UserFridges;

public class Client {
    private static final String URL = "http://f.commandus.com/a/";

    private static final String TAG = Client.class.getSimpleName();
    public static final int CODE_GETUSERFRIDGESTEST = 1;
    public static final int CODE_GETUSERFRIDGES = 2;
    public static final int CODE_LSFRIDGES = 3;
    public static final int CODE_ADDUSER = 4;
    public static final int CODE_ADDFRIDGEUSER = 5;

    private static Client mInstance = null;
    private static HashMap<Long, Purchases> mFridgePurchases;
    private static Meals mMeals;
    private static Fridges mFridges;
    private HashMap<Integer, Integer> mMealCardQty;
    private static UserFridges mUserFridges;
    private static Context mContext;

    public synchronized static Client getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Client();
        }
        mInstance.setContext(context);
        return mInstance;
    }

    public static Meals getMeals(Context context, String locale) {
        ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(Helper.loadResource(context, R.raw.ls_meal));
            mMeals = Meals.getRootAsMeals(byteBuffer);
        } catch (Exception e) {
            mMeals = null;
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
        return mMeals;
    }

    public static Purchases getFridgePurchases(Context context, long fridge_id) {
        ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(Helper.loadResource(context, R.raw.ls_purchase_2));
            mFridgePurchases.put(fridge_id, Purchases.getRootAsPurchases(byteBuffer));
        } catch (Exception e) {
            mUserFridges = null;
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
        return mFridgePurchases.get(fridge_id);
    }

    /**
     * Return UserFridges
     * @param context
     * @return UserFridges
     */
    public static void getUserFridgesTest(Context context, final OnServiceResponse onServiceResponse) {
        ByteBuffer byteBuffer;
        try {
            byteBuffer = ByteBuffer.wrap(Helper.loadResource(context, R.raw.ls_userfridge_2));
            mUserFridges = UserFridges.getRootAsUserFridges(byteBuffer);
            if (onServiceResponse != null)
                onServiceResponse.onSuccess(CODE_GETUSERFRIDGESTEST, mUserFridges);
        } catch (Exception e) {
            mUserFridges = null;
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_GETUSERFRIDGESTEST, -1, e.getLocalizedMessage());
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Return UserFridges
     * @param context
     * @return UserFridges
     */
    public static void getUserFridges(Context context, final OnServiceResponse onServiceResponse) {
        ByteBuffer byteBuffer;
        try {
            AndroidNetworking.post(URL + "ls_userfridge.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("user_id", String.valueOf(ApplicationSettings.getInstance(context).getUserId()))
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            ByteBuffer byteBuffer;
                            try {
                                mUserFridges = UserFridges.getRootAsUserFridges(ByteBuffer.wrap(response.getBytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_GETUSERFRIDGES, mUserFridges);
                                Log.i(TAG, "User fridges count: " + mUserFridges.mealcardsLength());
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_GETUSERFRIDGES, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });
        } catch (Exception e) {
            mUserFridges = null;
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_GETUSERFRIDGES, -1, e.getLocalizedMessage());
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Return List of fridges
     * @param context
     * @return UserFridges
     */
    public static void lsFridges(Context context, String locale, final OnServiceResponse onServiceResponse) {
        ByteBuffer byteBuffer;
        try {
            AndroidNetworking.post(URL + "ls_fridge.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("locale", context.getString(R.string.default_locale))
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            ByteBuffer byteBuffer;
                            try {
                                mFridges = Fridges.getRootAsFridges(ByteBuffer.wrap(response.getBytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_LSFRIDGES, mFridges);
                                Log.i(TAG, "Fridges count: " + mFridges.fridgesLength());
                                for (int f = 0; f < mFridges.fridgesLength(); f++) {
//                                    Log.i(TAG, "Fridge id: " + mFridges.fridges(f).id());
//                                    Log.i(TAG, "Fridge cn: " + mFridges.fridges(f).cn());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_LSFRIDGES, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });
        } catch (Exception e) {
            mFridges = null;
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_LSFRIDGES, -1, e.getLocalizedMessage());
            Log.e(TAG, e.toString());
        }
    }

    public static void addUser(String cn, String locale,
                               final OnServiceResponse onServiceResponse) {
        FlatBufferBuilder fbb = new FlatBufferBuilder(0);
        int scn = fbb.createString(cn);
        int sKey = fbb.createString("");
        int slocale = fbb.createString(locale);
        User.startUser(fbb);
        User.addId(fbb, 0);
        User.addCn(fbb, scn);
        User.addKey(fbb, sKey);
        User.addLocale(fbb, slocale);
        // User.addGeo(fbb, Geo.createGeo(fbb, 0.0f, 0.0f, 0));
        int u = User.endUser(fbb);
        fbb.finish(u);
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
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_ADDUSER, u);
                                Log.i(TAG, "User " + u.cn() + " created, id: " + u.id() + ", token:" + u.key() + ", locale: " + u.locale());
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_ADDUSER, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });
        } catch (Exception e) {
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_ADDUSER, -1, e.getLocalizedMessage());
            Log.e(TAG, e.toString());
        }
    }

    public void addFridgeUser(long userId, Fridge fridge, final OnServiceResponse onServiceResponse, long balance) {
        FlatBufferBuilder fbb = new FlatBufferBuilder(0);
        User.startUser(fbb);
        User.addId(fbb, 0);
        int u = User.endUser(fbb);
        Date start = new Date();
        FridgeUser.createFridgeUser(fbb, fridge.id(), u, start.getTime()/1000, 0, balance);

        fbb.finish(u);
        try {
            AndroidNetworking.post(URL + "add_fridgeuser.php")
                    .setContentType("application/octet-stream")
                    .addByteBody(fbb.dataBuffer().array())
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            ByteBuffer byteBuffer;
                            try {
                                User u = User.getRootAsUser(ByteBuffer.wrap(response.getBytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_ADDFRIDGEUSER, u);
                                Log.i(TAG, "User " + u.cn() + " created, id: " + u.id() + ", token:" + u.key() + ", locale: " + u.locale());
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_ADDFRIDGEUSER, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });
        } catch (Exception e) {
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_ADDFRIDGEUSER, -1, e.getLocalizedMessage());
            Log.e(TAG, e.toString());
        }
    }

    public void setMealCardQty(MealCard mc, int value)
    {

    }

    private  Client() {
        mMealCardQty = new HashMap<Integer, Integer>();
        mFridgePurchases = new HashMap<Long, Purchases>();
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

    public static Fridges lastFridges() {
        return mFridges;
    }

    public static Fridge lastFridge(int fridge_pos) {
        if (mFridges == null)
            return null;
        if (fridge_pos < 0 || fridge_pos >= mFridges.fridgesLength())
            return null;
        return mFridges.fridges(fridge_pos);
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
        return mUserFridges.mealcards(fridge_position).fridge().id();
    }

    /**
     * @brief Return fridge common name for User Fridge at position in
     * @param fridge_position position, zero based
     * @return fridge identifier for User Fridge at position in
     */
    public String getFridgeCN(int fridge_position ) {
        if (mUserFridges == null)
            return "";
        if ((fridge_position >= mUserFridges.mealcardsLength() || fridge_position < 0))
            return "";
        return mUserFridges.mealcards(fridge_position).fridge().cn();
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

    /**
     * @brief Check has user voted
     * @param userId
     * @param p
     * @return
     */
    public static boolean voteExists(long userId, Purchase p) {
        if (p == null)
            return false;
        for (int v = 0; v < p.votesLength(); v++) {
            if (p.votes(v).id() == userId)
                return true;
        }
        return false;
    }

    public long getMealId(String meal_cn) {
        if (mMeals == null)
            return -1;
        String umeal_cn = meal_cn.toUpperCase();
        for (int m = 0; m < mMeals.mealsLength(); m++) {
            if (mMeals.meals(m).cn().toUpperCase().contains(umeal_cn))
                return mMeals.meals(m).id();
        }
        return -1;
    }

    public void addMealCard(long mealId, long cost, int qty) {
        // TODO
    }

    public void rmFridge(long fridgeId) {
        // TODO
    }

    public void rmUser() {
        // TODO
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

}
