package hr.droidcon.conference;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.Window;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tale.prettysharedpreferences.BooleanEditor;
import hr.droidcon.conference.adapters.MainTabAdapter;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.PreferenceManager;
import hr.droidcon.conference.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private List<Conference> conferences;

    @Bind(R.id.schedule_tab_layout)
    TabLayout scheduleTabLayout;

    @Bind(R.id.schedule_view_pager)
    ViewPager scheduleViewPager;

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

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        initTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        conferences = getScheduledConferences();
        BooleanEditor<PreferenceManager> editor =
                new PreferenceManager(getSharedPreferences("MyPref", Context.MODE_PRIVATE))
                        .scheduleChanged();
        if (editor.getOr(true)) {
            editor.put(false).apply();
            updateView();
        }
    }

    private List<Conference> getScheduledConferences() {

        @SuppressWarnings("unchecked")
        List<Conference> conferences = (ArrayList<Conference>)
                getIntent().getSerializableExtra(MainActivity.EXTRA_CONFERENCES);

        List<Conference> filteredConferences = new ArrayList<>();
        for (Conference conference : conferences) {
            if (conference.getSpeaker().length() == 0
                    || conference.isInSchedule(getBaseContext())) {
                filteredConferences.add(conference);
            }
        }
        return filteredConferences;
    }

    private void initTabs() {
        scheduleTabLayout.setTabTextColors(Color.parseColor("#64FFFFFF"), Color.WHITE);
        scheduleTabLayout.addTab(scheduleTabLayout.newTab().setText("DAY 1"));
        scheduleTabLayout.addTab(scheduleTabLayout.newTab().setText("DAY 2"));
        scheduleTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        scheduleViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(scheduleTabLayout)
        );
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
        MainTabAdapter mainTabAdapter = new MainTabAdapter(
                getSupportFragmentManager(),
                scheduleTabLayout.getTabCount(),
                conferences
        );
        scheduleViewPager.setAdapter(mainTabAdapter);
    }
}
