package hr.droidcon.conference.hack.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import hr.droidcon.conference.objects.Conference;

/**
 * Utility class
 * @author Arnaud Camus
 */
public class Utils {

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

    public static int getNextStartingConference(ArrayList<Conference> conferences) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

        for (int i = 0;i<conferences.size();i++) {
            Conference conference = conferences.get(i);
            try {
                Date conferenceDate = dateFormat.parse(conference.getStartDate());
                if (conferenceDate.getTime() > currentTime) {
                    return i;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static void scrollListToStartingConference(ArrayList<Conference> conferences, final ListView listView) {
        final int scrollPos = Utils.getNextStartingConference(conferences);
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPositionFromTop(scrollPos, 0);
            }
        });

    }
}
