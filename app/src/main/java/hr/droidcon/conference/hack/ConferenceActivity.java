package hr.droidcon.conference.hack;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.ChangeBounds;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hr.droidcon.conference.hack.R;
import hr.droidcon.conference.hack.objects.Conference;
import hr.droidcon.conference.hack.utils.Utils;
import hr.droidcon.conference.hack.utils.ViewAnimations;
import hr.droidcon.conference.hack.utils.WordColor;
import hr.droidcon.conference.hack.views.FABView;

/**
 * Display the detail for one {@link Conference}
 * Must receive one {@link Conference} object in its {@link android.content.Intent}
 *
 * @author Arnaud Camus
 */
public class ConferenceActivity extends ActionBarActivity {

    Conference mConference;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleDateFormat2;

    /**
     * Enable to share views across activities with animation
     * on Android 5.0 Lollipop
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
        setContentView(R.layout.activity_conference);

        simpleDateFormat = new SimpleDateFormat("E, HH:mm");
        simpleDateFormat2 = new SimpleDateFormat(" - HH:mm");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.app_name));
            toolbar.setNavigationIcon(getResources()
                        .getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        mConference = (Conference)getIntent().getSerializableExtra("conference");

        ((TextView) findViewById(R.id.headline)).setText(Html.fromHtml(mConference.getHeadline()));
        ((TextView) findViewById(R.id.speaker)).setText(Html.fromHtml(mConference.getSpeaker()));
        ((TextView) findViewById(R.id.text)).setText(Html.fromHtml(mConference.getText()));
        ((TextView) findViewById(R.id.location)).setText(String.format(getString(R.string.location),
                mConference.getLocation()));
        ((TextView) findViewById(R.id.location)).setTextColor(
                                                WordColor.generateColor(mConference.getLocation()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        try {
            Date startDate = dateFormat.parse(mConference.getStartDate());
            Date endDate = dateFormat.parse(mConference.getEndDate());
            ((TextView)findViewById(R.id.date)).setText(
                    simpleDateFormat.format(startDate)
                    + simpleDateFormat2.format(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(getApplicationContext())
                .load(mConference.getSpeakerImageUrl())
                .transform(((BaseApplication) getApplicationContext()).mPicassoTransformation)
                .into((ImageView)findViewById(R.id.image));

        findViewById(R.id.text).post(new Runnable() {
            @Override
            public void run() {
                ViewAnimations.expand(findViewById(R.id.text));
            }
        });

        setupFAB();
    }

    /**
     * Setup a {@link hr.droidcon.conference.hack.views.FABView} to allow the
     * user to favorite the current {@link hr.droidcon.conference.hack.objects.Conference}
     */
    private void setupFAB() {
        final FABView fab = new FABView.Builder(this)
                .withDrawable(getResources().getDrawable(
                        (mConference.isFavorite(getBaseContext())
                                ? R.drawable.ic_favorite_white_24dp
                                : R.drawable.ic_favorite_outline_white_24dp)
                )).withButtonColor(getResources().getColor(R.color.accentColor))
                .withGravity(Gravity.TOP| Gravity.LEFT)
                .withMargins(14, 80, 0, 0)
                .create();

        /**
         * On click on the FAB, we save the new state in a
         * {@link android.content.SharedPreferences} file and
         * toggle the heart icon.
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConference.toggleFavorite(getBaseContext())) {
                    fab.setFloatingActionButtonDrawable(
                            getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                } else {
                    fab.setFloatingActionButtonDrawable(
                            getResources().getDrawable(R.drawable.ic_favorite_outline_white_24dp));
                }
            }
        });
    }

}
