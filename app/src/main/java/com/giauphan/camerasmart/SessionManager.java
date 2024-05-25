package com.giauphan.camerasmart;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.thingclips.smart.android.user.bean.User;

public class SessionManager {

    private static final String PREF_NAME = "MyAppSession";
    private static final String USER_KEY = "user";

    /**
     * Saves the user object to SharedPreferences.
     *
     * @param context The application context.
     * @param user    The user object to be saved.
     */
    public static void saveSession(Context context, String Key,Object user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(Key, userJson);
        editor.apply();
    }

    /**
     * Retrieves the user object from SharedPreferences.
     *
     * @param context The application context.
     * @return The user object if found, otherwise null.
     */
    public static <T> T getSession(Context context, String key, Class<T> clazz) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(key, "");
            return gson.fromJson(json, clazz);
        }
        return null;
    }

    /**
     * Removes the user object from SharedPreferences.
     *
     * @param context The application context.
     * @param key     The key of the user object to be removed.
     */
    public static void removeSession(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }


}
