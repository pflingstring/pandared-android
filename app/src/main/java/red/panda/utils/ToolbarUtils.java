package red.panda.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.Toolbar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

public class ToolbarUtils
{
    public static void setTitle(Toolbar toolbar, String title)
    {
        toolbar.setTitle(title);
    }

    public static void setAvatar(final Toolbar toolbar, String url, ImageLoader loader, final Activity activity)
    {
        // TODO: set default avatar if none available
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
