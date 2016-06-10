package hr.droidcon.conference;

import android.app.Application;
import android.graphics.Bitmap;
import com.firebase.client.Firebase;
import com.squareup.picasso.Transformation;
import com.urbanairship.UAirship;
import hr.droidcon.conference.events.FilterUpdateEvent;
import hr.droidcon.conference.utils.ImageManager;
import org.greenrobot.eventbus.EventBus;

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

    private Firebase firebase;


    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://droidconzg.firebaseio.com/");

        filterFavorites = false;

        UAirship.takeOff(this, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(UAirship airship) {

                // Enable user notifications
                airship.getPushManager().setUserNotificationsEnabled(true);
            }
        });

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

    public void toggleFilterFavorite() {
        this.filterFavorites = !filterFavorites;
        EventBus.getDefault().post(new FilterUpdateEvent(filterFavorites));
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

    public Firebase getFirebase(){
        return firebase;
    }
}
