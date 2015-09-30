package red.panda.utils.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefUtils
{
    public static final String AUTH_TOKEN = "red.panda.AUTH_TOKEN";
    public static final String USER_DETAILS = "red.panda.USER_DETAILS";

    public static SharedPreferences getPreferences(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getAuthToken(Context context)
    {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getString(AUTH_TOKEN, null);
    }

    public static String getUserDetails(Context context)
    {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getString(USER_DETAILS, null);
    }

    public static void clearPrefs(Context context)
    {
        SharedPreferences preferences = SharedPrefUtils.getPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}
