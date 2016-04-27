package hr.droidcon.conference.hack.timeline;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Hrvoje Kozak on 29/03/16.
 */
public interface TimelineAPI {

    @GET("droidconschedule.php")
    Call<List<Session>> getSessions();

    @GET("droidconspeakers.php")
    Call<List<Speaker>> getSpeakers();
}
