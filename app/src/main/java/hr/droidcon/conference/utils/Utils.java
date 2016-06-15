package hr.droidcon.conference.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import hr.droidcon.conference.ChromeBinder;
import hr.droidcon.conference.R;

/**
 * Utility class
 * @author Arnaud Camus
 */
public class Utils {

    public static final String FACEBOOK_URL = "https://www.facebook.com/droidconzg";
    public static final String INSTAGRAM_URL = "https://www.instagram.com/explore/tags/droidconzg/";
    public static final String TWITTER_URL = "https://twitter.com/droidconzg";
    public static final String FACEBOOK_APP = "com.facebook.katana";
    public static final String FACEBOOK_APP_URI = "fb://profile/852029344859298";

    private static final String EXTRA_CUSTOM_TABS_SESSION =
            "android.support.customtabs.extra.SESSION";

    private static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR =
            "android.support.customtabs.extra.TOOLBAR_COLOR";

    public static final String EXTRA_CUSTOM_TABS_EXIT_ANIMATION_BUNDLE =
            "android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE";

    private Utils() {}

    public static int dpToPx(int dp, final Context ctx) {
        if (ctx==null || ctx.getResources() == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return (int)(dp * (displayMetrics.densityDpi / 160f));
    }

    public static int pxToDp(int px, final Context ctx) {
        if (ctx==null || ctx.getResources() == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return (int)(px / (displayMetrics.densityDpi / 160f));
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void openUrl(Context context, String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Bundle extras = new Bundle();
            extras.putBinder(
                    EXTRA_CUSTOM_TABS_SESSION,
                    new ChromeBinder() /* Set to null for no session */
            );
            intent.putExtras(extras);
        }

        intent.putExtra(
                EXTRA_CUSTOM_TABS_TOOLBAR_COLOR,
                ContextCompat.getColor(context, R.color.colorPrimary)
        );
        context.startActivity(intent);
    }

    public static void openFb(Context context) {
        try {
            //Checks if FB is even installed.
            context.getPackageManager()
                   .getPackageInfo(FACEBOOK_APP, 0);
            //Tries to make intent with FB's URI
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(FACEBOOK_APP_URI)
            ));
        } catch (PackageManager.NameNotFoundException e) {
            openUrl(context, FACEBOOK_URL);
        }
    }
}
