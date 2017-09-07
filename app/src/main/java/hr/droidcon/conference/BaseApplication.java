package hr.droidcon.conference;

import android.app.Application;
import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;
import hr.droidcon.conference.utils.ImageManager;

/**
 * Basic implementation of the Application class
 * @author Arnaud Camus
 */
public class BaseApplication extends Application {

    /**l
     * A {@link com.squareup.picasso.Transformation} to crop
     * and round the speakers' pictures.
     */
    public Transformation mPicassoTransformation;

    private boolean filterFavorites;

    private int selectedTab = 0;
    private int selectedListItem = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mPicassoTransformation = new Transformation(){
            @Override
            public Bitmap transform(Bitmap source) {
                Bitmap bp = ImageManager.cropBitmapToCircle(source, BaseApplication.this);
                if (bp != source) {
                    source.recycle();
                }
                return bp;
            }

            @Override
            public String key() {
                return "rounded";
            }
        };
    }

    public boolean isFilterFavorites() {
        return filterFavorites;
    }

    public void setFilterFavorites(boolean filterFavorites) {
        this.filterFavorites = filterFavorites;
    }

    public void setSelectedTab(int selectedTab) {
        this.selectedTab = selectedTab;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedListItem(int selectedListItem) {
        this.selectedListItem = selectedListItem;
    }

    public int getSelectedListItem() {
        return selectedListItem;
    }
}
