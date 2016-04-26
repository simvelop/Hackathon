package hr.droidcon.conference;

import android.app.Application;
import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;
import com.urbanairship.UAirship;

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

    @Override
    public void onCreate() {
        super.onCreate();

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
}
