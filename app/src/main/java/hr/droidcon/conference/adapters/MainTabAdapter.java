package hr.droidcon.conference.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import hr.droidcon.conference.ConferenceListFragment;
import hr.droidcon.conference.objects.Conference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Hrvoje Kozak on 31/03/16.
 */
public class MainTabAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;
    private List<Conference> conferences = new ArrayList<>();

    private ConferenceListFragment dayOneFragment;
    private ConferenceListFragment dayTwoFragment;

    List<Conference> dayOneConferences = new ArrayList<>();
    List<Conference> dayTwoConferences = new ArrayList<>();

    public MainTabAdapter(FragmentManager fm, int numberOfTabs, List<Conference> conferences) {
        super(fm);
        this.numberOfTabs = numberOfTabs;

        setConferences(conferences);

        dayOneFragment = ConferenceListFragment.newInstance(0, dayOneConferences);
        dayTwoFragment = ConferenceListFragment.newInstance(1, dayTwoConferences);
    }

    private List<Conference> splitConferencesByDays(List<Conference> conferences) {

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        final Calendar cal = Calendar.getInstance();

        Comparator timeComparator = new Comparator<Conference>(){
            @Override
            public int compare(Conference c1, Conference c2) {
                Date date1 = null;
                Date date2 = null;

                try {
                    date1 = dateFormat.parse(c1.getStartDate());
                    date2 = dateFormat.parse(c2.getStartDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (date1 == null || date2 == null) {
                    return 0;
                } else if (date1.getTime() - date2.getTime() == 0) {
                    // if times are the same compare stages
                    char stage1 = c1.getLocation().charAt(c1.getLocation().length() - 1);
                    char stage2 = c2.getLocation().charAt(c2.getLocation().length() - 1);
                    return stage1 - stage2;
                } else {
                    return (int) (date1.getTime() - date2.getTime());
                }

            }
        };

        Collections.sort(conferences, timeComparator);

        for (Conference conference : conferences) {
            try {
                Date conferenceDate = dateFormat.parse(conference.getStartDate());
                cal.setTime(conferenceDate);
                if (cal.get(Calendar.YEAR) == 2016 && cal.get(Calendar.MONTH) == 3
                        && cal.get(Calendar.DAY_OF_MONTH) == 28) {
                    dayOneConferences.add(conference);
                }
                else if (cal.get(Calendar.YEAR) == 2016 && cal.get(Calendar.MONTH) == 3
                        && cal.get(Calendar.DAY_OF_MONTH) == 29) {
                    dayTwoConferences.add(conference);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dayOneConferences;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return dayOneFragment;
            case 1:
                return dayTwoFragment;
            default:
                return null;
        }
    }

    public List<Conference> getConferences() {
        return conferences;
    }

    public void setConferences(List<Conference> conferences) {
        this.conferences = conferences;
        splitConferencesByDays(conferences);
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
