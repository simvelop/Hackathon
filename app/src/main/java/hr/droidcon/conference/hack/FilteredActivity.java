package hr.droidcon.conference.hack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.hack.adapters.FilterListAdapter;
import hr.droidcon.conference.hack.objects.Conference;

/**
 * Created by stefan.tanovic on 4/27/2016.
 */
public class FilteredActivity extends AppCompatActivity {

    @Bind(R.id.main_events_list)
    RecyclerView mainEventsListView;

    private RecyclerView.LayoutManager layoutManager;
    private FilterListAdapter mAdapter;
    private ArrayList<Conference> conferences;

    public static final String ARGS_KEY = "conferences";

    public static final String BUSINESS_FILTER = "BUSINESS";
    public static final String DEVELOPMENT_FILTER = "DEVELOPMENT";
    public static final String UX_UI_FILTER = "UX / UI";
    public static final String OTHER_FILTER = "OTHER";
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filtered);
        ButterKnife.bind(this);
        Bundle args = getIntent().getExtras();
        conferences = (ArrayList<Conference>)args.getSerializable(ARGS_KEY);
        mAdapter = new FilterListAdapter(this, 0x00, conferences);

        layoutManager = new LinearLayoutManager(this);
        mainEventsListView.setLayoutManager(layoutManager);

        mainEventsListView.setAdapter(mAdapter);
        mainEventsListView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
//        mainEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
//
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.status_bar));
        if (mToolbar != null) {
            if (!conferences.isEmpty()) {
                String category = conferences.get(0).getCategory();
                String lowercaseTitle = category.toLowerCase();
                String title = String.valueOf(lowercaseTitle.charAt(0)).toUpperCase() + lowercaseTitle.substring(1, lowercaseTitle.length());
                mToolbar.setTitle(title);
            }

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
//        Utils.scrollListToStartingConference(conferences, mainEventsListView);
        super.onResume();
    }
}
