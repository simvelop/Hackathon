package hr.droidcon.conference.hack.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import hr.droidcon.conference.hack.ConferenceListFragment;
import hr.droidcon.conference.hack.ExploreFragment;
import hr.droidcon.conference.hack.objects.Conference;

/**
 * Created by Hrvoje Kozak on 31/03/16.
 */
public class MainTabAdapter extends FragmentStatePagerAdapter {

    private enum Day {
        THURSDAY,
        FRIDAY
    }

    private int numberOfTabs;
    private List<Conference> conferences = new ArrayList<>();

    private Fragment exploreFragment;
    private ConferenceListFragment attendingFragment;
    private ConferenceListFragment scheduleFragment;


    List<Conference> dayOneConferences = new ArrayList<>();
    List<Conference> dayTwoConferences = new ArrayList<>();
    List<Conference> oneListToRuleThemAll = new ArrayList<>();


    public MainTabAdapter(FragmentManager fm, int numberOfTabs, List<Conference> conferences) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
        updateConferences(conferences);


        exploreFragment = ExploreFragment.newInstance();

        attendingFragment = ConferenceListFragment.newInstance(1, conferences, true);

        scheduleFragment = ConferenceListFragment.newInstance(2, oneListToRuleThemAll, false);
    }

    public void updateConferences(List<Conference> conferences) {
        oneListToRuleThemAll.clear();
        dayOneConferences.clear();
        dayTwoConferences.clear();
        setConferences(conferences);
        oneListToRuleThemAll.add(createSeparator(Day.THURSDAY));
        oneListToRuleThemAll.addAll(dayOneConferences);
        oneListToRuleThemAll.add(createSeparator(Day.FRIDAY));
        oneListToRuleThemAll.addAll(dayTwoConferences);
        if (attendingFragment != null) {
            attendingFragment.updateConferences(conferences, true);
        }
        if (scheduleFragment != null) {
            scheduleFragment.updateConferences(oneListToRuleThemAll, false);
        }

    }

    private Conference createSeparator(Day day) {
        Conference conferenceSeparator = new Conference();
        conferenceSeparator.setSpeaker("");
        conferenceSeparator.setLocation("");
        conferenceSeparator.setEndDate("");
        conferenceSeparator.setSpeakerImageUrl("");
        conferenceSeparator.setText("");
        switch (day) {
            case THURSDAY: {
                conferenceSeparator.setStartDate("2016-04-28 10:00");
                conferenceSeparator.setEndDate("2016-04-28 20:00");
                conferenceSeparator.setHeadline("Thursday");
                break;
            }
            case FRIDAY: {
                conferenceSeparator.setStartDate("2016-04-29 10:00");
                conferenceSeparator.setEndDate("2016-04-29 20:00");
                conferenceSeparator.setHeadline("Friday");
            }
            default:
        }

        return conferenceSeparator;

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
                return exploreFragment;
            case 1:
                return attendingFragment;
            case 2:
                return scheduleFragment;
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
