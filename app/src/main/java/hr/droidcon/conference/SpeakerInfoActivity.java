package hr.droidcon.conference;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import hr.droidcon.conference.adapters.ContactAdapter;
import hr.droidcon.conference.timeline.Link;
import hr.droidcon.conference.timeline.Speaker;
import hr.droidcon.conference.utils.Utils;

/**
 * Created by
 *
 * @author Mateusz Kłodnicki
 *         on 27.04.16.
 */

public class SpeakerInfoActivity extends AppCompatActivity implements
        ContactAdapter.OnLinkClickListener {

    public static final String SPEAKER_UID = "speaker_uid";

    private Speaker speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_info);

        initToolbar();
        downloadSpeakerInfo();
    }

    private void setUpSpeakerInfo() {
        if (speaker != null) {
            ImageView image = (ImageView) findViewById(R.id.speakerImage);
            TextView name = (TextView) findViewById(R.id.speakerName);
            TextView job = (TextView) findViewById(R.id.speakerJob);
            TextView description = (TextView) findViewById(R.id.description);

            Picasso.with(getApplicationContext())
                    .load(speaker.getImage())
                    .transform(((BaseApplication) getApplicationContext()).mPicassoTransformation)
                    .into(image);

            name.setText(getString(
                    R.string.speaker_name,
                    speaker.getFirstName(),
                    speaker.getLastName()
            ));
            job.setText(getString(
                    R.string.speaker_company,
                    speaker.getCompanyPosition(),
                    speaker.getCompany()
            ));
            description.setText(Html.fromHtml(speaker.getDescription()));

            initContactLayout();
        }
    }

    private void downloadSpeakerInfo() {
        final String speakerUId = getIntent().getStringExtra(SPEAKER_UID);
        if (speakerUId != null) {
            SpeakersManager.INSTANCE.downloadAll(new ResultDownloadListener() {
                @Override
                public void onSuccess() {
                    speaker = SpeakersManager.INSTANCE.getSpeakersSparseArray().get(Integer.valueOf(speakerUId));
                    setUpSpeakerInfo();
                }

                @Override
                public void onFail() {
                    Toast.makeText(SpeakerInfoActivity.this, "Couldn't download info about speaker", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initContactLayout() {
        CardView contactLayout = (CardView) findViewById(R.id.contactLayout);

        if (speaker.getLinks().size() == 0) {
            contactLayout.setVisibility(View.GONE);
        } else {
            ContactAdapter contactAdapter = new ContactAdapter(speaker.getLinks());
            contactAdapter.setOnLinkClickListener(this);
            RecyclerView contactList = (RecyclerView) findViewById(R.id.contactList);
            contactList.setAdapter(contactAdapter);
        }
    }

    @Override
    public void onLinkCLick(Link link) {
        Utils.openUrl(this, link.getUrl());
    }
}
