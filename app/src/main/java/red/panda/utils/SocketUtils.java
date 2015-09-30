package red.panda.utils;

import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;
import red.panda.utils.misc.Constants;
import java.net.URISyntaxException;

public class SocketUtils
{
    public static Socket init()
    {
        try
        {
            return IO.socket(Constants.SERVER_URL);
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
    }
}
