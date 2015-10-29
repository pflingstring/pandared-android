package red.panda.utils;

import red.panda.utils.misc.Constants;

public class UserUtils
{
    public static boolean userIsMe(String id)
    {
        return Constants.User.ID
                .equals(id);
    }
}
