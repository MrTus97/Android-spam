package com.nhutlv.vietnam.quangnam.thangbinh.napchai;
import android.content.Context;
import android.content.SharedPreferences;
public class SessionManager {
    private static final String SHARED_PREFERENCES_NAME = "";
    private static final String KEY_SAVE_BUY_INAPP = "key_save_buy_inapp";
    private static SessionManager sInstance;
    private SharedPreferences sharedPref;

    public synchronized static SessionManager getInstance() {
        if (sInstance == null) {
            sInstance = new SessionManager();
        }
        return sInstance;
    }
    private SessionManager() {
        // no instance
    }
    public void setKeySaveBuyInApp(String token) {
        sharedPref.edit().putString(KEY_SAVE_BUY_INAPP, token).apply();
    }
    public String getKeySaveBuyInApp() {
        return sharedPref.getString(KEY_SAVE_BUY_INAPP, "");
    }

    public void init(Context context) {
        sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}