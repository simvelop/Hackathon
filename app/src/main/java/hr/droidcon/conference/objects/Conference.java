package hr.droidcon.conference.objects;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import hr.droidcon.conference.R;
import hr.droidcon.conference.timeline.Session;
import hr.droidcon.conference.utils.PreferenceManager;

import java.io.Serializable;

/**
 * Conference object, created by the CSV file
 * @author Arnaud Camus
 */
public class Conference implements Serializable {

    private String startDate;
    private String endDate;
    private String headline;
    private String speaker;
    private String speakerImageUrl;
    private String text;
    private String location;

    public Conference(String[] fromCSV) {
        startDate = fromCSV[0];
        endDate = fromCSV[1];
        headline = fromCSV[2];
        speaker = fromCSV[3];
        speakerImageUrl = fromCSV[4];
        text = fromCSV[5];
        location = fromCSV[6];
    }

    public Conference(Session session, String imageURL) {
        startDate = session.getStartISO().get(0);
        endDate = session.getEndISO().get(0);
        headline = session.getTitle();
        // TODO: multiple speakers
        speaker = TextUtils.join(", ", session.getSpeakerNames());
        // TODO: take image from speakers
        speakerImageUrl = imageURL;
        text = session.getAbstractHTML();
        location = session.getRoom().get(0);
    }


    /**
     * Save the new favorite state of the conference.
     * @param ctx a valid context
     * @return true if the hr.droidcon.conference is favorite
     */
    public boolean toggleFavorite(Context ctx) {
        PreferenceManager prefManager =
                new PreferenceManager(ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        boolean actual = prefManager.favorite(getHeadline())
                .getOr(false);
        prefManager.favorite(getHeadline())
                .put(!actual)
                .apply();

        Toast.makeText(
                ctx,
                actual
                        ? ctx.getString(R.string.remove_from_favorites)
                        : ctx.getString(R.string.add_to_favourites),
                Toast.LENGTH_SHORT
        ).show();
        return !actual;
    }

    //////////////////////////////////////
    //          GETTERS / SETTERS       //
    //////////////////////////////////////


    public boolean isFavorite(Context ctx) {
        PreferenceManager prefManager =
                new PreferenceManager(ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        return prefManager.favorite(getHeadline())
                .getOr(false);
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getSpeakerImageUrl() {
        if (speakerImageUrl.isEmpty()){
            // TODO: fix this
            return "http://lorempixel.com/200/200/";
        }
        return speakerImageUrl;
    }

    public void setSpeakerImageUrl(String speakerImageUrl) {
        this.speakerImageUrl = speakerImageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
