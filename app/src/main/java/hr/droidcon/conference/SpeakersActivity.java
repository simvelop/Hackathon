package hr.droidcon.conference;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import hr.droidcon.conference.adapters.SpeakersAdapter;
import hr.droidcon.conference.timeline.Speaker;
import hr.droidcon.conference.timeline.TimelineAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        Toast.makeText(SpeakersActivity.this, "Speaker: " + speaker.getFirstName() + " " + speaker.getLastName(), Toast.LENGTH_SHORT).show();
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
                    speakersAdapter.setItems(response.body());
                } else {
                    Toast.makeText(SpeakersActivity.this, "Something went wrong :(", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Speaker>> call, Throwable t) {
                Log.e("TAG", t.getMessage());
                Toast.makeText(SpeakersActivity.this, "No internet connection :(",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

}
