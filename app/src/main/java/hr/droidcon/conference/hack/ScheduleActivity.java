package hr.droidcon.conference.hack;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.Window;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.hack.adapters.MainTabAdapter;
import hr.droidcon.conference.hack.objects.Conference;
import hr.droidcon.conference.hack.objects.StaticHelper;
import hr.droidcon.conference.hack.utils.Utils;

public class ScheduleActivity extends AppCompatActivity {

    private List<Conference> conferences;

    @Bind(R.id.schedule_tab_layout)
    TabLayout scheduleTabLayout;

    @Bind(R.id.schedule_view_pager)
    ViewPager scheduleViewPager;

    private MainTabAdapter mainTabAdapter;

    /**
     * Enable to share views across activities with animation on Android 5.0 Lollipop
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupLollipop() {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new ChangeBounds());
        getWindow().setSharedElementEnterTransition(new ChangeBounds());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        conferences = getScheduledConferences();
        if (Utils.isLollipop()) {
            setupLollipop();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        initTabs();
    }

    @Override
    protected void onResume(){
        super.onResume();
        conferences = getScheduledConferences();

        if(StaticHelper.hasDataChanged()){
            updateView();
        }
    }

    private List<Conference> getScheduledConferences() {
        List<Conference> rawConferences = (List<Conference>) StaticHelper.object;
        List<Conference> filteredConferences = new ArrayList<>();
        for (Conference conference : rawConferences) {
            if(conference.getSpeaker().length() == 0 || conference.isInSchedule(getBaseContext())){
                filteredConferences.add(conference);
            }
        }
        return filteredConferences;
    }

    private void initTabs() {
        scheduleTabLayout.setTabTextColors(Color.parseColor("#64FFFFFF"), Color.WHITE);
        scheduleTabLayout.addTab(scheduleTabLayout.newTab()
                .setText("DAY 1"));
        scheduleTabLayout.addTab(scheduleTabLayout.newTab()
                .setText("DAY 2"));
        scheduleTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        scheduleViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(scheduleTabLayout));
        scheduleTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                scheduleViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        updateView();
    }

    private void updateView() {
        mainTabAdapter = new MainTabAdapter(
                getSupportFragmentManager(),
                scheduleTabLayout.getTabCount(),
                conferences
        );
        scheduleViewPager.setAdapter(mainTabAdapter);
    }
}
