package red.panda.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import red.panda.Config;
import red.panda.utils.misc.Constants;

public class ToolbarUtils
{
    public static void setTitle(Toolbar toolbar, String title)
    {
        toolbar.setTitle(title);
    }

    // TODO: replace url with default avatar string
    // BUG : no avatar is set if url == null
    public static void setAvatar(final Toolbar toolbar, @Nullable String url, ImageLoader loader, final Activity activity)
    {
        // TODO: set default avatar if none available
        if (!Constants.SERVER_URL.equals(Config.ENV.LOCAL))
        {
            loader.get(url, new ImageLoader.ImageListener()
            {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate)
                {
                    Bitmap bitmap = response.getBitmap();
                    toolbar.setLogo(new BitmapDrawable(activity.getApplicationContext().getResources(), bitmap));
                }

                @Override
                public void onErrorResponse(VolleyError error)
                {

                }
            });
        }
    }

}
