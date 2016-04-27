package hr.droidcon.conference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import hr.droidcon.conference.objects.Conference;
import hr.droidcon.conference.utils.Utils;
import hr.droidcon.conference.utils.ViewAnimations;
import hr.droidcon.conference.utils.WordColor;
import hr.droidcon.conference.views.FABView;

/**
 * Display the detail for one {@link Conference}
 * Must receive one {@link Conference} object in its {@link android.content.Intent}
 *
 * @author Arnaud Camus
 */
public class ConferenceActivity extends ActionBarActivity {

    private static final String TAG = "ConferenceActivity";
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
        
        final RatingBar ratingBar = ((RatingBar) findViewById(R.id.rating_bar));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG, "onRatingChanged: new rating " + rating);
                getFireBase().child("ratings").child(getConferenceId()).child(getDeviceId()).setValue((double) rating);
            }
        });

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

        final TextView resultRatingTextView = (TextView) findViewById(R.id.rating_result_bar);
        final TextView resultParticipantsTextView = (TextView) findViewById(R.id.rating_result_participants);

        getFireBase().child("ratings").child(getConferenceId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double ratingSum = 0;
                Log.d(TAG, "onDataChange:" + snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
               // JSONObject jsonObject  = (JSONObject) snapshot.getValue();
                for(DataSnapshot object: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: " + object);
                    ratingSum = ratingSum +  (double) object.getValue();

                }
                double rating = ratingSum / (double) snapshot.getChildrenCount();
                if(Double.isNaN(rating)) {
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
            @Override public void onCancelled(FirebaseError error) { }
        });

        getFireBase().child("ratings").child(getConferenceId()).child(getDeviceId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "my Result: " +dataSnapshot.getValue());
                if(dataSnapshot.getValue() == null) {
                    return;
                }
                double rating = (double) dataSnapshot.getValue();
                if(ratingBar.getRating() != rating) {
                    ratingBar.setRating((float) rating);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Setup a {@link hr.droidcon.conference.views.FABView} to allow the
     * user to favorite the current {@link hr.droidcon.conference.objects.Conference}
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

    private String getDeviceId() {


        final String  androidId;

        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);


        return androidId;
    }

    private String getConferenceId() {
        return mConference.getHeadline().replaceAll("[^a-zA-Z0-9]+",""); //'.', '#', '$', '[', or ']
    }

    private Firebase getFireBase() {
        return ((BaseApplication) getApplication()).getFirebase();
    }



}
