package hr.droidcon.conference.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * Utility class
 * @author Arnaud Camus
 */
public class Utils {

    public static final String FACEBOOK_URL = "https://www.facebook.com/droidconzg";
    public static final String INSTAGRAM_URL = "https://www.instagram.com/explore/tags/droidconzg/";
    public static final String TWITTER_URL = "https://twitter.com/droidconzg";

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

    public static void openWebURL(Context context, String inURL) {
        Intent browse;

        if (!inURL.startsWith("http://") && !inURL.startsWith("https://"))
            inURL = String.format("http://%s", inURL);

        browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        context.startActivity(browse);
    }

    public static Intent getOpenFacebookIntent(Context context) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/852029344859298")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/droidconzg")); //catches and opens a url to the desired page
        }
    }

}
