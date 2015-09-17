// Author :  Ravi Tamada
// source : http://www.androidhive.info/2015/04/android-getting-started-with-material-design/

package red.panda.NavDrawer;

public class NavDrawerItem
{
    private boolean showNotify;
    private String title;


    public NavDrawerItem()
    {
    }

    public NavDrawerItem(boolean showNotify, String title)
    {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify()
    {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify)
    {
        this.showNotify = showNotify;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}

