package hr.droidcon.conference;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.ChangeBounds;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import hr.droidcon.conference.database.DatabaseKt;
import hr.droidcon.conference.database.OnTotalRatingChange;
import hr.droidcon.conference.database.OnUserRatingChange;
import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.DeviceInfoKt;
import hr.droidcon.conference.utils.PreferenceManager;
import hr.droidcon.conference.utils.Utils;
import hr.droidcon.conference.utils.ViewAnimations;
import hr.droidcon.conference.utils.WordColor;
import hr.droidcon.conference.views.FABView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Display the detail for one {@link Conference}
 * Must receive one {@link Conference} object in its {@link Intent}
 * @author Arnaud Camus
 */
public class ConferenceActivity extends AppCompatActivity {

    private static final Pattern CONFERENCE_ID_PAT = Pattern.compile("[^a-zA-Z0-9]+");

    Conference conference;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, HH:mm");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(" - HH:mm");

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

        conference = (Conference) getIntent().getSerializableExtra("conference");

        ((TextView) findViewById(R.id.headline)).setText(Html.fromHtml(conference.getHeadline()));
        ((TextView) findViewById(R.id.speaker)).setText(Html.fromHtml(conference.getSpeaker()));
        ((TextView) findViewById(R.id.text)).setText(Html.fromHtml(conference.getText()));
        ((TextView) findViewById(R.id.location)).setText(String.format(getString(R.string.location),
                conference.getLocation()));
        ((TextView) findViewById(R.id.location)).setTextColor(
                WordColor.generateColor(conference.getLocation())
        );

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        try {
            Date startDate = dateFormat.parse(conference.getStartDate());
            Date endDate = dateFormat.parse(conference.getEndDate());
            ((TextView) findViewById(R.id.date)).setText(
                    simpleDateFormat.format(startDate)
                            + simpleDateFormat2.format(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(getApplicationContext())
                .load(conference.getSpeakerImageUrl())
                .transform(((BaseApplication) getApplicationContext()).mPicassoTransformation)
                .into((ImageView) findViewById(R.id.image));

        findViewById(R.id.text).post(new Runnable() {
            @Override
            public void run() {
                ViewAnimations.expand(findViewById(R.id.text));
            }
        });

        setupFAB();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        initSpeakerLayout();
        initRatings();
    }

    private void initRatings() {
        final RatingBar ratingBar = findViewById(R.id.rating_bar);
        final TextView resultRatingTextView = findViewById(R.id.rating_result_bar);
        final TextView resultParticipantsTextView = findViewById(R.id.rating_result_participants);

        final String conferenceId = conference.getConferenceId();
        final String deviceId = DeviceInfoKt.getDeviceId(this);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                DatabaseKt.setRating(conferenceId, deviceId, rating);
            }
        });

        DatabaseKt.onOverallRatingChange(conferenceId, new OnTotalRatingChange() {
            @Override
            @SuppressLint("SetTextI18n")
            public void onChange(double rating, int count) {
                if (Double.isNaN(rating)) {
                    resultRatingTextView.setText(getString(R.string.rating_result_empty));
                    findViewById(R.id.rating_image).setVisibility(View.INVISIBLE);

                    resultParticipantsTextView.setText("");
                    findViewById(R.id.rating_participants_image).setVisibility(View.INVISIBLE);
                } else {
                    resultRatingTextView.setText(Double.toString(rating));
                    findViewById(R.id.rating_image).setVisibility(View.VISIBLE);

                    resultParticipantsTextView.setText(Integer.toString(count));
                    findViewById(R.id.rating_participants_image).setVisibility(View.VISIBLE);
                }
            }
        });

        DatabaseKt.onUserRatingChange(conferenceId, deviceId, new OnUserRatingChange() {
            @Override
            public void onChange(double rating) {
                ratingBar.setRating((float) rating);
            }
        });
    }

    /**
     * Setup a {@link FABView} to allow the
     * user to favorite the current {@link Conference}
     */
    private void setupFAB() {
        final FABView fab = new FABView.Builder(this)
                .withDrawable(getResources().getDrawable(
                        conference.isFavorite(getBaseContext())
                                ? R.drawable.ic_favorite_white_24dp
                                : R.drawable.ic_favorite_outline_white_24dp
                ))
                .withButtonColor(getResources().getColor(R.color.colorAccent))
                .withGravity(Gravity.TOP | Gravity.LEFT)
                .withMargins(14, 80, 0, 0)
                .create();

        /**
         * On click on the FAB, we save the new state in a
         * {@link SharedPreferences} file and
         * toggle the heart icon.
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFav = conference.toggleFavorite(getBaseContext());
                if (isFav) {
                    fab.setFloatingActionButtonDrawable(
                            getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                } else {
                    fab.setFloatingActionButtonDrawable(
                            getResources().getDrawable(R.drawable.ic_favorite_outline_white_24dp));
                }
                new PreferenceManager(getSharedPreferences("MyPref", Context.MODE_PRIVATE))
                        .favoritesChanged()
                        .put(true)
                        .apply();

                DatabaseKt.setFavorite(
                        conference.getConferenceId(),
                        DeviceInfoKt.getDeviceId(ConferenceActivity.this),
                        isFav
                );
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Conference Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://hr.droidcon.conference/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Conference Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://hr.droidcon.conference/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private String getDeviceId() {
        return android.provider.Settings.Secure.getString(
                getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID
        );
    }

    private void initSpeakerLayout() {
        RelativeLayout speakerLayout = (RelativeLayout) findViewById(R.id.speakerLayout);
        speakerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conference.getSpeakerUID() != null) {
                    Intent intent = new Intent(ConferenceActivity.this, SpeakerInfoActivity.class);
                    intent.putExtra(SpeakerInfoActivity.SPEAKER_UID, conference.getSpeakerUID());
                    startActivity(intent);
                }
            }
        });
    }
}
