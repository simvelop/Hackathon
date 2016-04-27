package hr.droidcon.conference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import hr.droidcon.conference.adapters.SpeakersAdapter;
import hr.droidcon.conference.timeline.Speaker;

/**
 * Created by
 * @author Mateusz Kłodnicki
 * on 27.04.16.
 */

public class SpeakersActivity extends AppCompatActivity implements SpeakersAdapter.OnSpeakerClickListener {

    private SpeakersAdapter speakersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speakers);

        initToolbar();
        initRecyclerView();
        downloadSpeakers();
    }

    @Override
    public void onSpeakerClick(Speaker speaker) {
        Intent intent = new Intent(this, SpeakerInfoActivity.class);
        intent.putExtra(SpeakerInfoActivity.SPEAKER_UID, speaker.getUid());
        startActivity(intent);
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

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.speakersList);
        speakersAdapter = new SpeakersAdapter(this);
        speakersAdapter.setOnSpeakerClickListener(this);
        recyclerView.setAdapter(speakersAdapter);
    }

    private void downloadSpeakers() {
        SpeakersManager.INSTANCE.downloadAll(new ResultDownloadListener() {
            @Override
            public void onSuccess() {
                speakersAdapter.setItems(SpeakersManager.INSTANCE.getSpeakers());
            }

            @Override
            public void onFail() {
                Toast.makeText(SpeakersActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
