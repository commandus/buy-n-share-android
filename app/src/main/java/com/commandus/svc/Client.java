package com.commandus.svc;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.util.SparseIntArray;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.commandus.buynshare.ApplicationSettings;
import com.commandus.buynshare.Helper;
import com.commandus.buynshare.R;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.Date;

import bs.Fridge;
import bs.FridgeUser;
import bs.FridgeUsers;
import bs.Fridges;
import bs.Geo;
import bs.Meal;
import bs.MealCard;
import bs.Meals;
import bs.Purchase;
import bs.Purchases;
import bs.User;
import bs.UserFridges;
import okhttp3.Response;

public class Client {
    private static final String URL = "http://f.commandus.com/a/";

    private static final String TAG = Client.class.getSimpleName();
    public static final int CODE_GETUSERFRIDGES = 1;
    public static final int CODE_LSFRIDGES = 2;
    public static final int CODE_ADDUSER = 3;
    public static final int CODE_ADDFRIDGEUSER = 4;
    public static final int CODE_LSMEAL = 5;
    public static final int CODE_GETFRIDGEPURCHASES = 6;
    public static final int CODE_ADDPURCHASE = 7;
    public static final int CODE_ADDFRIDGE = 8;
    public static final int CODE_ADDMEAL = 9;

    private static Client mInstance = null;
    private static Meals mMeals;
    private static Fridges mFridges;
    private SparseIntArray mMealCardQty;
    private static LongSparseArray<Purchases> mFridgePurchases;
    private static UserFridges mUserFridges;

    public synchronized static Client getInstance() {
        if (mInstance == null) {
            mInstance = new Client();
        }
        return mInstance;
    }

    public static void getMeals(Context context, String locale, final OnServiceResponse onServiceResponse) {
        try {
            AndroidNetworking.post(URL + "ls_meal.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("locale", locale)
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                mMeals = Meals.getRootAsMeals((ByteBuffer.wrap(response.body().bytes())));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_LSMEAL, mMeals);
                            } catch (Exception e) {
                                Log.e(TAG, "getMeals() error: " + e.toString());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_LSMEAL, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });

        } catch (Exception e) {
            mMeals = null;
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_LSMEAL, -1, e.getLocalizedMessage());
            Log.e(TAG, "lsMeals() error: " + e.toString());
            e.printStackTrace();
        }
    }

    public static void getFridgePurchases(Context context, final long fridge_id, final OnServiceResponse onServiceResponse) {
        try {
            AndroidNetworking.post(URL + "ls_purchase.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("user_id", String.valueOf(0))
                    .addQueryParameter("fridge_id", String.valueOf(fridge_id))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                mFridgePurchases.put(fridge_id, Purchases.getRootAsPurchases((ByteBuffer.wrap(response.body().bytes()))));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_GETFRIDGEPURCHASES, mFridgePurchases.get(fridge_id));
                            } catch (Exception e) {
                                Log.e(TAG, "getFridgePurchases(): " + e.toString());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_GETFRIDGEPURCHASES, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });

        } catch (Exception e) {
            mMeals = null;
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_LSMEAL, -1, e.getLocalizedMessage());
            Log.e(TAG, "getFridgePurchases() " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Return UserFridges
     * @param context Application contetx
     */
    public static void getUserFridges(final Context context, final OnServiceResponse onServiceResponse) {
        ApplicationSettings settings = ApplicationSettings.getInstance(context);
        final long userId = settings.getUserId();
        if (userId <= 0) {
            // No user created yet
            mUserFridges = null;
            if (onServiceResponse != null) {
                onServiceResponse.onError(CODE_GETUSERFRIDGES, -1,
                        context.getString(R.string.error_no_user_yet));
                return;
            }
        }
        try {
            AndroidNetworking.post(URL + "ls_userfridge.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("user_id", String.valueOf(userId))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                mUserFridges = UserFridges.getRootAsUserFridges(ByteBuffer.wrap(response.body().bytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_GETUSERFRIDGES, mUserFridges);
                            } catch (Exception e) {
                                Log.e(TAG, "getUserFridges(" + String.valueOf(userId) + ") " + e.toString());
                                e.printStackTrace();
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
            Log.e(TAG, "getUserFridges() " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Return List of fridges
     * @param context Application context
     */
    public static void lsFridges(Context context, String locale, final OnServiceResponse onServiceResponse) {
        try {
            AndroidNetworking.post(URL + "ls_fridge.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("locale", context.getString(R.string.default_locale))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                mFridges = Fridges.getRootAsFridges(ByteBuffer.wrap(response.body().bytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_LSFRIDGES, mFridges);
                            } catch (Exception e) {
                                Log.e(TAG, "lsFridges() " + e.toString());
                                e.printStackTrace();
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
            Log.e(TAG, "lsFridges() " + e.toString());
            e.printStackTrace();
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
        int u = User.endUser(fbb);
        fbb.finish(u);
        try {
            AndroidNetworking.post(URL + "add_user.php")
                    .setContentType("application/octet-stream")
                    .addByteBody(Helper.getFBBytes(fbb))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                User u = User.getRootAsUser(ByteBuffer.wrap(response.body().bytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_ADDUSER, u);
                                Log.i(TAG, "User " + u.cn() + " created, id: " + u.id() + ", token:" + u.key() + ", locale: " + u.locale());
                            } catch (Exception e) {
                                Log.e(TAG, "addUser() " + e.toString());
                                e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void addFridgeUser(long userId, Fridge fridge, long balance, String locale,
                              final OnServiceResponse onServiceResponse) {
        FlatBufferBuilder fbb = new FlatBufferBuilder(0);
        Date start = new Date();
        int scn = fbb.createString("");
        int skey = fbb.createString("");
        int slocale = fbb.createString(locale);
        User.startUser(fbb);
        User.addId(fbb, userId);
        User.addCn(fbb, scn);
        User.addKey(fbb, skey);
        User.addLocale(fbb, slocale);
        int u = User.endUser(fbb);
        int fu = FridgeUser.createFridgeUser(fbb, fridge.id(), u, start.getTime()/1000, 0, balance);
        fbb.finish(fu);
        FridgeUser fut = FridgeUser.getRootAsFridgeUser(fbb.dataBuffer());
        Log.i(TAG, "User id: " + Long.toString(fut.user().id()));

        try {
            AndroidNetworking.post(URL + "add_fridgeuser.php")
                    .setContentType("application/octet-stream")
                    .addByteBody(Helper.getFBBytes(fbb))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                User u = User.getRootAsUser(ByteBuffer.wrap(response.body().bytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_ADDFRIDGEUSER, u);
                                Log.i(TAG, "User created");
                            } catch (Exception e) {
                                Log.e(TAG, "addFridgeUser() " + e.toString());
                                e.printStackTrace();
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
            Log.e(TAG, "addFridgeUser() " + e.toString());
            e.printStackTrace();
        }
    }

    public void setMealCardQty(MealCard mc, int value)
    {

    }

    private  Client() {
        mMealCardQty = new SparseIntArray();
        mFridgePurchases = new LongSparseArray<>();
    }

    public int getMealcardQtyDiff(int position) {
        return mMealCardQty.get(position, 0);
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

    public static void clearAccount() {
        mUserFridges = null;
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
     * Return fridge identifier for User Fridge at position in
     * @param fridge_position position, zero based
     * @return fridge identifier for User Fridge at position in
     */
    public static long getFridgeId(int fridge_position) {
        if (mUserFridges == null)
            return -1;
        if ((fridge_position >= mUserFridges.mealcardsLength() || fridge_position < 0))
            return -1;
        return mUserFridges.mealcards(fridge_position).fridge().id();
    }

    public static int getFridgePos(long fridgeId) {
        if (mUserFridges == null)
            return -1;
        for (int f = 0; f < mUserFridges.mealcardsLength(); f++) {
            if (mUserFridges.mealcards(f).fridge().id() == fridgeId)
                return f;
        }
        return -1;
    }

    /**
     * Return fridge common name for User Fridge at position in
     * @param fridge_position position, zero based
     * @return fridge identifier for User Fridge at position in
     */
    public static String getFridgeCN(int fridge_position ) {
        if (mUserFridges == null)
            return "";
        if ((fridge_position >= mUserFridges.mealcardsLength() || fridge_position < 0))
            return "";
        return mUserFridges.mealcards(fridge_position).fridge().cn();
    }

    /**
     * Return Users of the fridge
     * @param fridge_position fridge position
     * @return Users of the fridge
     */
    public static FridgeUsers getFridgeUsers(int fridge_position) {
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
     * Check has user voted
     * @param userId User identifier
     * @param purchase Purchase
     * @return true- voted
     */
    public static boolean voteExists(long userId, Purchase purchase) {
        if (purchase == null)
            return false;
        for (int v = 0; v < purchase.votesLength(); v++) {
            if (purchase.votes(v).id() == userId)
                return true;
        }
        return false;
    }

    public static long getMealId(String meal_cn) {
        if (mMeals == null)
            return -1;
        String umeal_cn = meal_cn.toUpperCase();
        for (int m = 0; m < mMeals.mealsLength(); m++) {
            String c = mMeals.meals(m).cn();
            if (c != null) {
                if (c.toUpperCase().contains(umeal_cn))
                    return mMeals.meals(m).id();
            }
        }
        return -1;
    }

    public static void addPurchase(
            String locale, long userId, long fridgeId, long mealId,
            long cost, int qty,
            final OnServiceResponse onServiceResponse) {
        FlatBufferBuilder fbb = new FlatBufferBuilder(0);
        Date start = new Date();
        int slocale = fbb.createString(locale);
        int meal = Meal.createMeal(fbb, mealId, 0, slocale);
        User.startUser(fbb);
        User.addId(fbb, userId);
        int user = User.endUser(fbb);
        fbb.finish(user);
        int votes = Purchase.createVotesVector(fbb, new int[] {user});
        int purchase = Purchase.createPurchase(fbb, 0, userId, fridgeId, meal, cost, start.getTime()/1000, 0, votes);
        fbb.finish(purchase);
        try {
            AndroidNetworking.post(URL + "add_purchase.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("qty", String.valueOf(qty))
                    .addQueryParameter("all", "")
                    .addByteBody(Helper.getFBBytes(fbb))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                Purchase purchaseRet = Purchase.getRootAsPurchase(ByteBuffer.wrap(response.body().bytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_ADDPURCHASE, purchaseRet);
                                Log.i(TAG, "Purchase created, id: " + purchaseRet.id());
                            } catch (Exception e) {
                                Log.e(TAG, "addPurchase() " + e.toString());
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_ADDPURCHASE, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });
        } catch (Exception e) {
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_ADDPURCHASE, -1, e.getLocalizedMessage());
            Log.e(TAG, "addPurchase() " + e.toString());
            e.printStackTrace();
        }
    }

    public static void rmFridge(long fridgeId) {
        // TODO
    }

    public static void rmUser() {
        // TODO
    }

    public static void addFridge(String locale, long userId, String cn,
                                 long balance,
                                 final OnServiceResponse onServiceResponse) {
        FlatBufferBuilder fbb = new FlatBufferBuilder(0);
        int scn = fbb.createString(cn);
        int slocale = fbb.createString(locale);
        Fridge.startFridge(fbb);
        Fridge.addCn(fbb, scn);
        Fridge.addLocale(fbb, slocale);
        Fridge.addGeo(fbb, Geo.createGeo(fbb, 0.0f, 0.0f, 0));
        int f = Fridge.endFridge(fbb);
        fbb.finish(f);
        try {
            AndroidNetworking.post(URL + "add_fridge.php")
                    .setContentType("application/octet-stream")
                    .addQueryParameter("user_id", String.valueOf(userId))
                    .addQueryParameter("balance", String.valueOf(balance))
                    .addByteBody(Helper.getFBBytes(fbb))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                Fridge fridgeRet = Fridge.getRootAsFridge(ByteBuffer.wrap(response.body().bytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_ADDFRIDGE, fridgeRet);
                                Log.i(TAG, "Fridge created, id: " + fridgeRet.id());
                            } catch (Exception e) {
                                Log.e(TAG, "addFridge() " + e.toString());
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_ADDFRIDGE, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });
        } catch (Exception e) {
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_ADDFRIDGE, -1, e.getLocalizedMessage());
            Log.e(TAG, "addFridge() " + e.toString());
            e.printStackTrace();
        }
    }

    public static void addMeal(String locale, String cn, final OnServiceResponse onServiceResponse) {
        FlatBufferBuilder fbb = new FlatBufferBuilder(0);
        int scn = fbb.createString(cn);
        int slocale = fbb.createString(locale);
        int m = Meal.createMeal(fbb, 0, scn, slocale);
        fbb.finish(m);
        try {
            AndroidNetworking.post(URL + "add_meal.php")
                    .setContentType("application/octet-stream")
                    .addByteBody(Helper.getFBBytes(fbb))
                    .build()
                    .getAsOkHttpResponse(new OkHttpResponseListener() {
                        @Override
                        public void onResponse(Response response) {
                            try {
                                Fridge fridgeRet = Fridge.getRootAsFridge(ByteBuffer.wrap(response.body().bytes()));
                                if (onServiceResponse != null)
                                    onServiceResponse.onSuccess(CODE_ADDMEAL, fridgeRet);
                                Log.i(TAG, "Fridge created, id: " + fridgeRet.id());
                            } catch (Exception e) {
                                Log.e(TAG, "addFridge() " + e.toString());
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (onServiceResponse != null)
                                onServiceResponse.onError(CODE_ADDMEAL, anError.getErrorCode(), anError.getLocalizedMessage());
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
                        }
                    });
        } catch (Exception e) {
            if (onServiceResponse != null)
                onServiceResponse.onError(CODE_ADDMEAL, -1, e.getLocalizedMessage());
            Log.e(TAG, "addFridge() " + e.toString());
            e.printStackTrace();
        }
    }
}
