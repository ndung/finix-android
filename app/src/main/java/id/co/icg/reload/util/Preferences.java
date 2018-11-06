package id.co.icg.reload.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import id.co.icg.reload.model.Reseller;

public class Preferences {
    private String TAG = Preferences.class.getSimpleName();

    // Shared Preferences
    SharedPreferences preferences;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "iReload";

    // All Shared Preferences Keys
    private static final String KEY_DEFAULT_PRINTER = "default_printer";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TRANS_ID = "trans_id";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String REMEMBER_PASSWORD = "remember_password";
    private static final String HAS_UNREAD_MESSAGE = "unread_message";
    private static final String ENCRYPTION_KEY = "encryption_key";
    private static final String  FIRST_TIME_USED = "first_time_used";

    // Constructor
    public Preferences(Context context) {
        this._context = context;
        preferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public void setUserName(String userName){
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public String getUserName(){
        return preferences.getString(KEY_USER_NAME, "");
    }

    public void setPassword(String password){
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public String getPassword(){
        return preferences.getString(KEY_PASSWORD, "");
    }

    public void setTransId(String transId){
        editor.putString(KEY_TRANS_ID, transId);
        editor.commit();
    }

    public String getTransId(){
        return preferences.getString(KEY_TRANS_ID, "");
    }

    public static void setDefaultPrinter(Context context, String defaultPrinter){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(KEY_DEFAULT_PRINTER, defaultPrinter);
        editor.commit();
    }

    public static String getDefaultPrinter(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getString(KEY_DEFAULT_PRINTER, "pdf");
    }

    public String getNotifications() {
        return preferences.getString(KEY_NOTIFICATIONS, null);
    }

    public String getEncryptionKey(){
        return preferences.getString(ENCRYPTION_KEY, "");
    }

    public void setUnreadMessage(Boolean param){
        editor.putBoolean(HAS_UNREAD_MESSAGE, param);
        editor.commit();
    }

    public Boolean hasUnreadMessage(){
        try {
            return preferences.getBoolean(HAS_UNREAD_MESSAGE, Boolean.FALSE);
        }catch(Exception ex){
            return null;
        }
    }

    public Boolean isPasswordRemembered(){
        return preferences.getBoolean(REMEMBER_PASSWORD, Boolean.FALSE);
    }

    public void setRememberPassword(Boolean remembered){
        editor.putBoolean(REMEMBER_PASSWORD, remembered);
        editor.commit();
    }

    public Boolean isFirstTimeUsed(){
        return preferences.getBoolean(FIRST_TIME_USED, Boolean.TRUE);
    }

    public void setFirstTimeUsed(Boolean firstTimeUsed){
        editor.putBoolean(FIRST_TIME_USED, firstTimeUsed);
        editor.commit();
    }

    public void setEncryptionKey(String key){
        editor.putString(ENCRYPTION_KEY, key);
        editor.commit();
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }


    public static void putString(Context context, String key, String value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key, value).commit();
    }

    public static void putBoolean(Context context, String key, boolean value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(key, value).commit();
    }

    public static void putInt(Context context, String key, int value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(key, value).commit();
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.edit();
    }

    public static String getString(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static boolean getBoolean(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static int getInt(Context context, String key){
        SharedPreferences preferences = context.getSharedPreferences(Static.MyPref, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static String getToken(Context context){
        return getString(context, Static.TOKEN);
    }

    public static void setToken(Context context, String token){
        putString(context, Static.TOKEN, token);
    }

    public static String getPublicKey(Context context){
        return getString(context, Static.PUBLIC_KEY);
    }

    public static void setPublicKey(Context context, String token){
        putString(context, Static.PUBLIC_KEY, token);
    }

    public static Reseller getUser(Context context){
        try{
            String json = getString(context, Static.USER_DATA);
            return new Gson().fromJson(json, Reseller.class);
        }catch (Exception e){
            return null;
        }
    }

    public static void setUser(Context context, Reseller user){
        putString(context, Static.USER_DATA, new Gson().toJson(user));
    }

    public static void setLoginFlag(Context context, boolean flag){
        putBoolean(context, Static.LOGIN_KEY, flag);
    }

    public static boolean getLoginFlag(Context context){
        return getBoolean(context, Static.LOGIN_KEY);
    }

}
