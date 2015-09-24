package red.panda.utils.misc;

import android.content.Context;

public class Constants
{
    public Constants() {}

    public static class Conversation
    {
        public static final int AUTHOR_IS_ME = 1;
        public static final int AUTHOR_IS_NOT_ME = 0;
    }

    public static class User
    {
        public static String USER_DETAILS;
        public static String AUTH_TOKEN;
    }

    public static void init(Context context)
    {
        new Constants();
        User.AUTH_TOKEN = SharedPrefUtils.getAuthToken(context);
        User.USER_DETAILS = SharedPrefUtils.getUserDetails(context);
    }
}