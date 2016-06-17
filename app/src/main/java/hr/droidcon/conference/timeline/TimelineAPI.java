package hr.droidcon.conference.timeline;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

/**
 * Created by Hrvoje Kozak on 29/03/16.
 */
public interface TimelineAPI {

    @GET("droidconschedule.php")
    Call<List<Session>> getSessions();

    @GET("droidconspeakers.php")
    Call<List<Speaker>> getSpeakers();
}
