package hr.droidcon.conference.timeline;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

/**
 * Created by Hrvoje Kozak on 29/03/16.
 */
public interface TimelineAPI {

    @GET("sessions.json")
    Call<List<Session>> getSessions();

    @GET("speakers.json")
    Call<List<Speaker>> getSpeakers();
}
