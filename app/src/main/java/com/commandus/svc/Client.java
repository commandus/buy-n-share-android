package com.commandus.svc;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.commandus.buynshare.ApplicationSettings;
import com.commandus.buynshare.Helper;
import com.commandus.buynshare.R;
import com.google.flatbuffers.FlatBufferBuilder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import bs.FridgePurchases;
import bs.FridgeUsers;
import bs.MealCard;
import bs.Meals;
import bs.Purchase;
import bs.Purchases;
import bs.User;
import bs.UserFridges;

public class Client {
    private static final String URL = "http://f.commandus.com/a/";

    private static final String TAG = Client.class.getSimpleName();
    private static Client mInstance = null;
    private static HashMap<Long, Purchases> mFridgePurchases;
    private static Meals mMeals;
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
        Log.i(TAG, "Serialized: " + Integer.toString(fbb.sizedByteArray().length));
        try {
            AndroidNetworking.post(URL + "add_user.php")
                    .setContentType("application/octet-stream")
                    .addByteBody(fbb.sizedByteArray())
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            ByteBuffer byteBuffer;
                            try {
                                User u = User.getRootAsUser(ByteBuffer.wrap(response.getBytes()));
                                ApplicationSettings s = ApplicationSettings.getInstance(context);
                                s.saveUser(u);
                                Log.i(TAG, "User " + u.cn() + " created, id: " + u.id() + ", token:" + u.key() + ", locale: " + u.locale());
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(Client.mContext, anError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, URL + ": " + anError.getErrorDetail() + ": " + anError.getLocalizedMessage());
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
