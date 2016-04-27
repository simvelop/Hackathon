package hr.droidcon.conference.hack;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hr.droidcon.conference.R;
import hr.droidcon.conference.hack.adapters.MainAdapter;
import hr.droidcon.conference.hack.adapters.MainTabAdapter;
import hr.droidcon.conference.hack.objects.Conference;
import hr.droidcon.conference.hack.timeline.Session;
import hr.droidcon.conference.hack.timeline.Speaker;
import hr.droidcon.conference.hack.timeline.TimelineAPI;
import hr.droidcon.conference.hack.utils.PreferenceManager;
import hr.droidcon.conference.hack.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Main activity of the application, list all conferences slots into a listView
 *
 * @author Arnaud Camus
 */
public class MainActivity extends AppCompatActivity {

    private MainAdapter mAdapter;
    private List<Conference> mConferences = new ArrayList<Conference>();
    private Toolbar mToolbar;

    private int mTimeout = 5 * 60 * 1000; //  5 mins timeout for refreshing data

    private List<Speaker> mSpeakers;

    @Bind(R.id.main_tab_layout)
    TabLayout mainTabLayout;

    @Bind(R.id.main_view_pager)
    ViewPager mainViewPager;

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

        if (Utils.isLollipop()) {
            setupLollipop();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle(getString(R.string.app_name));
            setSupportActionBar(mToolbar);
        }

        initTabs();

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

    private void initTabs() {
        mainTabLayout.setTabTextColors(Color.parseColor("#64FFFFFF"), Color.WHITE);
        mainTabLayout.addTab(mainTabLayout.newTab()
                .setText("EXPLORE"));
        mainTabLayout.addTab(mainTabLayout.newTab()
                .setText("ATTENDING"));
        mainTabLayout.addTab(mainTabLayout.newTab()
                .setText("SCHEDULE"));
        mainTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mainViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(mainTabLayout));
        mainTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            //we refresh the views in case a conference has been (un)favorite
            mAdapter.notifyDataSetChanged();
        }

        readCalendarAPI();
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
                System.currentTimeMillis()) {
            fetchSpeakers();
            Log.d("REFRESH", "10 minutes has passed, app refreshed");

        }

        if (!getCachedContent()) {
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
                Toast.makeText(MainActivity.this, "No internet connection :(",
                        Toast.LENGTH_SHORT)
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
                Log.e("TAG", t.getMessage());
                getCachedContent();
                Toast.makeText(MainActivity.this, "No internet connection :(", Toast
                        .LENGTH_SHORT).show();
            }
        });
    }

    private void updateMainAdapterSessions() {
        if (mainTabAdapter == null) {
            mainTabAdapter = new MainTabAdapter(
                    getSupportFragmentManager(),
                    mainTabLayout.getTabCount(),
                    mConferences
            );
            mainViewPager.setAdapter(mainTabAdapter);
        } else {
            mainTabAdapter.updateConferences(mConferences);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_more) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        if (id == R.id.business_cat_id) {
            ArrayList<Conference> resultConferences = filterCategories(FilteredActivity.BUSINESS_FILTER);
            Intent intent = new Intent(this, FilteredActivity.class);
            intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
            startActivity(intent);
            return true;
        }
        if (id == R.id.development_cat_id) {
            ArrayList<Conference> resultConferences = filterCategories(FilteredActivity.DEVELOPMENT_FILTER);
            Intent intent = new Intent(this, FilteredActivity.class);
            intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
            startActivity(intent);
            return true;
        }
        if (id == R.id.ux_cat_id) {
            ArrayList<Conference> resultConferences = filterCategories(FilteredActivity.UX_UI_FILTER);
            Intent intent = new Intent(this, FilteredActivity.class);
            intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
            startActivity(intent);
            return true;
        }
        if (id == R.id.other_cat_id) {
            ArrayList<Conference> resultConferences = filterCategories(FilteredActivity.OTHER_FILTER);
            Intent intent = new Intent(this, FilteredActivity.class);
            intent.putExtra(FilteredActivity.ARGS_KEY, resultConferences);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Conference> filterCategories(String filter) {
        ArrayList<Conference> resultConferences = new ArrayList<>();
        for (Conference conference:this.mConferences){
            if (conference.getCategory().equalsIgnoreCase(filter)) {
                resultConferences.add(conference);
            }
        }
        return resultConferences;
    }

    /**
     * Track how many times the Activity is launched and send a push notification {@link
     * hr.droidcon.conference.hack.utils.SendNotification} to ask the user for feedback on the event.
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
                return false;
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
