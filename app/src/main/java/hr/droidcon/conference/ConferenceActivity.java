package hr.droidcon.conference;

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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import hr.droidcon.conference.objects.Conference;
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
 *
 * @author Arnaud Camus
 */
public class ConferenceActivity extends AppCompatActivity {

    private static final Pattern CONFERENCE_ID_PAT = Pattern.compile("[^a-zA-Z0-9]+");

    Conference mConference;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleDateFormat2;
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

        mConference = (Conference) getIntent().getSerializableExtra("conference");

        ((TextView) findViewById(R.id.headline)).setText(Html.fromHtml(mConference.getHeadline()));
        ((TextView) findViewById(R.id.speaker)).setText(Html.fromHtml(mConference.getSpeaker()));
        ((TextView) findViewById(R.id.text)).setText(Html.fromHtml(mConference.getText()));
        ((TextView) findViewById(R.id.location)).setText(String.format(getString(R.string.location),
                mConference.getLocation()));
        ((TextView) findViewById(R.id.location)).setTextColor(
                WordColor.generateColor(mConference.getLocation())
        );
        final RatingBar ratingBar = ((RatingBar) findViewById(R.id.rating_bar));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                getFireBase().child("ratings")
                             .child(getConferenceId())
                             .child(getDeviceId())
                             .setValue((double) rating);
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        try {
            Date startDate = dateFormat.parse(mConference.getStartDate());
            Date endDate = dateFormat.parse(mConference.getEndDate());
            ((TextView) findViewById(R.id.date)).setText(
                    simpleDateFormat.format(startDate)
                            + simpleDateFormat2.format(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(getApplicationContext())
                .load(mConference.getSpeakerImageUrl())
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

        final TextView resultRatingTextView = (TextView)
                findViewById(R.id.rating_result_bar);
        final TextView resultParticipantsTextView = (TextView)
                findViewById(R.id.rating_result_participants);

        //get the overall rating
        Firebase overallRating = getFireBase().child("ratings")
                                              .child(getConferenceId());
        overallRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double ratingSum = 0;
                for(DataSnapshot object: snapshot.getChildren()) {
                    ratingSum = ratingSum +  (double) object.getValue();
                }

                double rating = ratingSum / (double) snapshot.getChildrenCount();
                if (Double.isNaN(rating)) {
                    resultRatingTextView.setText(getString(R.string.rating_result_empty));
                    findViewById(R.id.rating_image).setVisibility(View.INVISIBLE);

                    resultParticipantsTextView.setText("");
                    findViewById(R.id.rating_participants_image).setVisibility(View.INVISIBLE);
                } else {
                    resultRatingTextView.setText("" + (float) rating);
                    findViewById(R.id.rating_image).setVisibility(View.VISIBLE);

                    resultParticipantsTextView.setText(snapshot.getChildrenCount()+"");
                    findViewById(R.id.rating_participants_image).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {}
        });

        //get my own rating (based on android Id)
        Firebase ownRating = getFireBase().child("ratings")
                                          .child(getConferenceId())
                                          .child(getDeviceId());
        ownRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    double rating = (double) dataSnapshot.getValue();
                    if (ratingBar.getRating() != rating) {
                        ratingBar.setRating((float) rating);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        initSpeakerLayout();
    }

    /**
     * Setup a {@link FABView} to allow the
     * user to favorite the current {@link Conference}
     */
    private void setupFAB() {
        final FABView fab = new FABView.Builder(this)
                .withDrawable(getResources().getDrawable(
                        mConference.isFavorite(getBaseContext())
                                ? R.drawable.ic_favorite_white_24dp
                                : R.drawable.ic_favorite_outline_white_24dp
                ))
                .withButtonColor(getResources().getColor(R.color.accentColor))
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
                if (mConference.toggleFavorite(getBaseContext())) {
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

    private String getConferenceId() {
        return CONFERENCE_ID_PAT.matcher(mConference.getHeadline()).replaceAll("");
    }

    private Firebase getFireBase() {
        return ((BaseApplication) getApplication()).getFirebase();
    }

    private void initSpeakerLayout() {
        RelativeLayout speakerLayout = (RelativeLayout) findViewById(R.id.speakerLayout);
        speakerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConference.getSpeakerUID() != null) {
                    Intent intent = new Intent(ConferenceActivity.this, SpeakerInfoActivity.class);
                    intent.putExtra(SpeakerInfoActivity.SPEAKER_UID, mConference.getSpeakerUID());
                    startActivity(intent);
                }
            }
        });
    }
}
