package hr.droidcon.conference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tale.prettysharedpreferences.BooleanEditor;
import hr.droidcon.conference.adapters.MainTabAdapter;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.timeline.Session;
import hr.droidcon.conference.timeline.Speaker;
import hr.droidcon.conference.timeline.TimelineAPI;
import hr.droidcon.conference.utils.PreferenceManager;
import hr.droidcon.conference.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity of the application, list all conferences slots into a listView
 *
 * @author Arnaud Camus
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.main_tab_layout)
    TabLayout mainTabLayout;

    @Bind(R.id.main_view_pager)
    ViewPager mainViewPager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.nav_view)
    NavigationView navView;

    private List<Conference> mConferences = new ArrayList<>();

    private int mTimeout = 5 * 60 * 1000; //  5 mins timeout for refreshing data

    private List<Speaker> mSpeakers;

    private MainTabAdapter mainTabAdapter;

    private ActionBarDrawerToggle drawerToggle;

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

        if (Utils.isLollipop()) {
            setupLollipop();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (mToolbar != null) {
            mToolbar.setTitle(getString(R.string.app_name));
            setSupportActionBar(mToolbar);
        }

        initTabs();
        initDrawer();

        //        ListView mListView = (ListView) findViewById(R.id.listView);
        //        mAdapter = new MainAdapter(this, 0x00, mConferences);
        //        mListView.setAdapter(mAdapter);

        // TODO: LOADING SPINNER
        // reading API moved to onResume
        //        readCalendarAPI();

        //        mListView.setOnScrollListener(this);
        //        mListView.setOnItemClickListener(this);

        trackOpening();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        drawerToggle.onConfigurationChanged(config);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawers();
        switch (item.getItemId()) {
            case R.id.drawer_speakers:
                startActivity(new Intent(this, SpeakersActivity.class));
                break;
            case R.id.drawer_facebook:
                Utils.openFb(this);
                break;
            case R.id.drawer_instagram:
                Utils.openUrl(this, Utils.INSTAGRAM_URL);
                break;
            case R.id.drawer_twitter:
                Utils.openUrl(this, Utils.TWITTER_URL);
                break;
            case R.id.drawer_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_more:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_news_twitter:
                Utils.openUrl(this, Utils.TWITTER_URL);
                break;
            case R.id.action_news_fb:
                Utils.openFb(this);
                break;
            case R.id.action_kill_time:
                startActivity(new Intent(this, TimeKillGame.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                mToolbar,
                R.string.drawer_layout_open,
                R.string.drawer_layout_close
        );

        drawerLayout.addDrawerListener(drawerToggle);
        navView.setNavigationItemSelectedListener(this);
    }

    private void initTabs() {
        mainTabLayout.setTabTextColors(Color.parseColor("#64FFFFFF"), Color.WHITE);
        mainTabLayout.addTab(mainTabLayout.newTab().setText("DAY 1"));
        mainTabLayout.addTab(mainTabLayout.newTab().setText("DAY 2"));
        mainTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mainViewPager.setCurrentItem(((BaseApplication) getApplication()).getSelectedTab());

        mainViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mainTabLayout)
        );

        mainTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        readCalendarAPI();

        BooleanEditor<PreferenceManager> editor =
                new PreferenceManager(getSharedPreferences("MyPref", Context.MODE_PRIVATE))
                        .favoritesChanged();

        // Update main adapter if favorite's have been changed through conference activity.
        if (editor.getOr(true)) {
            editor.put(false).apply();
            updateMainAdapterSessions();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setToolbarElevation(int elevation) {
        if (Utils.isLollipop()) {
            mToolbar.setElevation(elevation);
        }
    }

    /**
     * fetches speakers and sessions and making a list of {@link Conference}.
     */
    public void readCalendarAPI() {
        // fetch speakers and sessions

        SharedPreferences prefs =
                android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getLong(Constants.PREFS_TIMEOUT_REFRESH, 0) + mTimeout <
                System.currentTimeMillis() || !getCachedContent()) {
            fetchSpeakers();
        }
    }

    private void fetchSpeakers() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SIMVELOP_ENDPOINT)
                .build();

        TimelineAPI timelineAPI = retrofit.create(TimelineAPI.class);

        Call<List<Speaker>> getSpeakers = timelineAPI.getSpeakers();

        getSpeakers.enqueue(new Callback<List<Speaker>>() {
            @Override
            public void onResponse(Call<List<Speaker>> call, Response<List<Speaker>> response) {

                if (response.isSuccessful()) {
                    mSpeakers = response.body();
                    fetchSessions();
                } else {
                    getCachedContent();
                }
            }

            @Override
            public void onFailure(Call<List<Speaker>> call, Throwable t) {
                Log.e("TAG", t.getMessage());
                getCachedContent();
                Toast.makeText(MainActivity.this, "No internet connection :(", Toast.LENGTH_SHORT)
                     .show();
            }
        });
    }

    private void fetchSessions() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SIMVELOP_ENDPOINT)
                .build();

        TimelineAPI timelineAPI = retrofit.create(TimelineAPI.class);

        Call<List<Session>> getSessions = timelineAPI.getSessions();
        final SharedPreferences prefs =
                android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        getSessions.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {

                if (response.isSuccessful()) {
                    mConferences.clear();

                    for (Session session : response.body()) {
                        addSession(session);
                    }

                    updateMainAdapterSessions();
                    cacheSessions();
                    prefs.edit()
                         .putLong(Constants.PREFS_TIMEOUT_REFRESH, System.currentTimeMillis())
                         .apply();
                } else {
                    getCachedContent();
                }
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                getCachedContent();
                Toast.makeText(MainActivity.this, "No internet connection :(", Toast.LENGTH_SHORT)
                     .show();
            }
        });
    }

    private void updateMainAdapterSessions() {
        mainTabAdapter = new MainTabAdapter(
                getSupportFragmentManager(),
                mainTabLayout.getTabCount(),
                mConferences
        );
        mainViewPager.setAdapter(mainTabAdapter);

        mainViewPager.setCurrentItem(((BaseApplication) getApplication()).getSelectedTab());

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                ((BaseApplication) getApplication()).setSelectedTab(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void cacheSessions() {

        SharedPreferences prefs =
                android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        String json = gson.toJson(mConferences);

        prefs.edit()
             .putString(Constants.PREFS_SESSIONS_CACHE, json)
             .apply();
    }

    private void addSession(Session session) {
        String imageURL = "";
        for (String speakerUID : session.getSpeakerUIDs()) {
            Speaker speaker = findSpeakerByUID(speakerUID);
            if (speaker != null) {
                imageURL = speaker.getImage();
            }
        }

        mConferences.add(new Conference(session, imageURL));
    }

    private Speaker findSpeakerByUID(String speakerUID) {

        for (Speaker speaker : mSpeakers) {
            if (speaker.getUid()
                       .equals(speakerUID)) {
                return speaker;
            }
        }
        return null;
    }


    /**
     * Track how many times the Activity is launched and send a push notification {@link
     * hr.droidcon.conference.utils.SendNotification} to ask the user for feedback on the event.
     */
    private void trackOpening() {
        PreferenceManager prefManager =
                new PreferenceManager(getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        long nb = prefManager.openingApp()
                             .getOr(0L);
        prefManager.openingApp()
                   .put(++nb)
                   .apply();

        if (nb == 10) {
            // SendNotification.feedbackForm(this);
        }
    }

    private boolean getCachedContent() {

        SharedPreferences prefs =
                android.preference.PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.contains(Constants.PREFS_SESSIONS_CACHE)) {

            if (!mConferences.isEmpty()) {
                return true;
            }

            Gson gson = new Gson();
            String json = prefs.getString(Constants.PREFS_SESSIONS_CACHE, "");


            Type type = new TypeToken<List<Conference>>() {
            }.getType();

            mConferences = gson.fromJson(json, type);
            updateMainAdapterSessions();

            return true;
        } else {
            return false;
        }
    }
}
