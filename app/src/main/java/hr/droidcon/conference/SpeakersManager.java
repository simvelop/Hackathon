package hr.droidcon.conference;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import hr.droidcon.conference.timeline.Speaker;
import hr.droidcon.conference.timeline.TimelineAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by
 *
 * @author Mateusz Kłodnicki
 *         on 27.04.16.
 */
public enum SpeakersManager {
    INSTANCE;

    private SparseArray<Speaker> speakersSparseArray = new SparseArray<>();
    private List<Speaker> speakers = new ArrayList<>();

    public List<Speaker> getSpeakers() {
        return speakers;
    }

    public SparseArray<Speaker> getSpeakersSparseArray() {
        return speakersSparseArray;
    }

    public void downloadAll(final ResultDownloadListener listener) {
        if (speakers.size() != 0) {
            listener.onSuccess();
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.SIMVELOP_ENDPOINT)
                    .build();

            TimelineAPI timelineAPI = retrofit.create(TimelineAPI.class);

            Call<List<Speaker>> call = timelineAPI.getSpeakers();
            call.enqueue(new Callback<List<Speaker>>() {
                @Override
                public void onResponse(Call<List<Speaker>> call, Response<List<Speaker>> response) {
                    if (response.isSuccessful()) {
                        speakers = response.body();
                        for (Speaker speaker : response.body()) {
                            speakersSparseArray.append(Integer.valueOf(speaker.getUid()), speaker);
                        }

                        listener.onSuccess();
                    } else {
                        listener.onFail();
                    }
                }

                @Override
                public void onFailure(Call<List<Speaker>> call, Throwable t) {
                    listener.onFail();
                }
            });
        }
    }
}
